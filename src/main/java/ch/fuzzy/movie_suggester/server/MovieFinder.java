package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.MathUtil;
import ch.fuzzy.movie_suggester.util.ObjUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.fuzzy.movie_suggester.server.MovieFilter.Weight;
import static ch.fuzzy.movie_suggester.server.Settings.Distance;

/**
 * The MovieFinder calculates how well a {@link Movie} fits for a given {@link MovieFilter}
 * @author rbu
 */
public class MovieFinder {

    private static final Logger log = LoggerFactory.getLogger(MovieFinder.class);

    private static MovieFinder INSTANCE;
    private final MovieRepository repo;
    private final SettingsRepository settingsRepository;

    public static void initialize(MovieRepository repo, SettingsRepository sr){
        INSTANCE = new MovieFinder(repo, sr);
    }

    public static MovieFinder get(){ return INSTANCE; }

    public MovieFinder(MovieRepository repo, SettingsRepository settingsRepository){
        assert INSTANCE == null;
        this.repo = repo;
        this.settingsRepository = settingsRepository;
    }

    /**
     * Creates a List of {@link MovieResult results} of max. 24 elements which indicate how much they overlap with the given filter
     * @param filter the {@link MovieFilter filter} given by the user to find {@link Movie movies}
     * @return a list of distinct {@link MovieResult movie results} of size max. 24. Movies with an overlap of 0 are not shown
     */
    public List<MovieResult> findMovies(MovieFilter filter){
        if(filter == null) return new ArrayList<>(); //NOTE: rbu 29.10.2021, should only be the case if we go directly to the corresponding URL
        List<Movie> movies;
        if(filter.getLanguage() != null) {
            movies = repo.findByLanguagesContaining(filter.getLanguage());
        } else {
            movies = repo.findAll();
        }
        if(filter.getPlatforms() != null && filter.getPlatforms().size() > 0) {
            movies = movies.stream().filter(m -> m.getPlatforms().stream().anyMatch(p -> filter.getPlatforms().contains(p))).collect(Collectors.toList()); //NOTE: rbu 01.11.2021, add to Query for SQL
        }
        if(filter.getAgeRestriction() != null){
            movies = movies.stream().filter(m -> !filter.getAgeRestriction().restricts(m.getAgeRestriction())).collect(Collectors.toList());
        }
        List<MovieResult> results = movies.stream().map(m -> fittingMovie(m, filter)).filter(f -> f.getFit() > 0).sorted().collect(Collectors.toList());
        if(results.size() > 24){
            results = results.subList(0, 24);
        }
        return results;
    }

    /**
     * We calculate how much a {@link MovieFilter filter} fits into a {@link Movie movie}
     * All fuzzy variables ({@link Genre}, Watchers, {@link Relationship}, {@link Keyword Keywords}) which were set by the filter are taken into account
     * If the {@link Settings setting} {@link Settings#isConcentrationFit()} is true we calculate the numberWatchers Fit with the relationshipFit
     * if this flag isn't set we calculate the two fits separately, in this case the question about investment is also asked the user which will give a Fit as well
     * @return a {@link Movie} with its fit
     */
    private MovieResult fittingMovie(Movie movie, MovieFilter filter){
        TupleList<Integer, Weight> fits = new TupleList<>();
        Settings settings = ObjUtil.assertUniqueNotNull(settingsRepository.findAll());
        if(filter.getGenres() != null && filter.getGenres().size() > 0){ fits.add(genreFit(movie, filter.getGenres(), filter.getGenreWeight(), settings.getDistanceFunction()), filter.getGenreWeight()); }
        if(filter.getPositiveKeywords() != null){ fits.add(positiveKeywordFit(movie, filter.getPositiveKeywords(), filter.getPositiveKeywordsWeight(), settings.getDistanceFunction()), filter.getPositiveKeywordsWeight()); }
        if(filter.getNegativeKeywords() != null){ fits.add(negativeKeywordFit(movie, filter.getNegativeKeywords(), filter.getNegativeKeywordsWeight(), settings.getDistanceFunction()), filter.getNegativeKeywordsWeight()); }
        if(filter.getEmotionality() != null && movie.getOptimalEmotionality() != null){ fits.add(emotionalityFit(movie, filter.getEmotionality(), filter.getEmotionalityWeight()), filter.getEmotionalityWeight()); }
        if(filter.getScreen() != null){ fits.add(screenFit(movie, filter.getScreen(), filter.getScreenWeight()), filter.getScreenWeight()); }

        if(settings.isConcentrationFit()) {
            if (filter.getNumberWatchers() != null || filter.getRelationship() != null) {
                fits.add(concentrationFit(movie, filter.getNumberWatchers(), filter.getRelationship(), filter.getNumberWatchersWeight(), filter.getRelationshipWeight()), filter.getRelationshipWeight(), filter.getNumberWatchersWeight());
            }
        } else {
            if(filter.getNumberWatchers() != null){ fits.add(concentrationFit(movie, filter.getNumberWatchers(), null, filter.getNumberWatchersWeight(), null), filter.getNumberWatchersWeight()); }
            if(filter.getRelationship() != null){ fits.add(relationshipFit(movie, filter.getRelationship(), filter.getRelationshipWeight()), filter.getRelationshipWeight()); }
            if(filter.getInvested() != null && movie.getOptimalInvestment() != null){ fits.add(investedFit(movie,filter.getInvested(), filter.getInvestedWeight()), filter.getInvestedWeight());}
        }
        int fit = calculateFinalFit(fits, settings.getDistanceFunction());
        return new MovieResult(movie, fit);
    }

    /**
     * This function calculates the final fit according to the given distance function.
     * @param fits all Fits calculated represented as a Tuple of the Fit and its weight
     * @param distanceFunction The distance function defined by the settings which should be used to calculate the distance
     * @return a number between 0 and 100 which represents the average of all fits according to the distance function
     */
    private int calculateFinalFit(TupleList<Integer, Weight> fits, Distance distanceFunction){
        if(fits.size() == 0){ return 100; } //return 100 for no filter
        if(ObjUtil.isContained(distanceFunction, Distance.L1, Distance.L2_ONLY_ON_FITS)){
            //We just sum when using L2_ONLY_ON_FITS since we already used Euclidean Distance for each fit
            return (int)(fits.stream().map(t -> t.first).mapToInt(f -> f).sum()/fits.stream().mapToDouble(t -> t.seconds().mapToDouble(this::factorFor).sum()).sum());
        } else if(ObjUtil.isContained(distanceFunction, Distance.L2, Distance.L2_COMPLETE)){
            //we assume L2 Distance if not otherwise specified
            return (int)(100*Math.sqrt(fits.stream().map(t -> t.first).mapToInt(MathUtil::sq).sum()/fits.stream().map(t -> 100*t.seconds().mapToDouble(this::factorFor).sum()).mapToDouble(MathUtil::sq).sum()));
        }
        throw new IllegalArgumentException(ObjUtil.toString(distanceFunction) + "Not implemented");
    }

    /**
     * This function takes the {@link Relationship relationship} as well as the number of Watchers into account and calculates a fit for those
     */
    private Integer concentrationFit(Movie movie, Integer numberWatchers, Relationship relationship, Weight numberWatchersWeight, Weight relationshipWeight) {
        if (numberWatchers != null) {
            Map<NumberPeople, Integer> numberPeopleMap = new HashMap<>();
            Arrays.stream(NumberPeople.values()).forEach(v -> numberPeopleMap.put(v, calculatePeopleFit(v, numberWatchers)));
            if (relationship != null) {
                Map<Concentration, Integer> concentrationMap = new HashMap<>();
                Arrays.stream(Concentration.values()).forEach(v -> concentrationMap.put(v, calculateConcentrationFit(v, numberWatchers, relationship, numberPeopleMap)));
                return (int) (factorFor(numberWatchersWeight, relationshipWeight) * bestFit(movie, concentrationMap));
            }
            return (int) (factorFor(numberWatchersWeight) * bestFit(movie, peopleToConcentrationMap(numberPeopleMap)));
        }
        //this case is when we have no answer for the number of people but one for the relationship
        return relationshipFit(movie, relationship, relationshipWeight);
    }

    /**
     * Step function for the fit of the number of watchers for the given {@link NumberPeople membership}
     * @return a fit for the number of people watching to a certain membership
     */
    int calculatePeopleFit(NumberPeople membership, int numberWatchers) {
        switch (membership){
            case FEW: return numberWatchers <= 2 ? 100 : numberWatchers < 4 ? (100*(4-numberWatchers))/2 : 0;
            case MEDIUM:
                if(numberWatchers <= 2 || numberWatchers >= 8) return 0;
                if(numberWatchers < 4) return (100*(numberWatchers - 2))/2;
                if(numberWatchers > 6) return (100*(8-numberWatchers)/2);
                return 100;
            case MANY: return numberWatchers < 6 ? 0 : numberWatchers <= 8 ? (100*(numberWatchers-6))/2 : 100;
            default: throw new IllegalArgumentException("No Calculation rule found for " + membership);
        }
    }

    /**
     * Convenience function to get the factor for a number of weights. Just gives the average over all {@link Weight weight} factors
     */
    private double factorFor(Weight... weights){
        if(weights.length == 0) return 1;
        double res = 1;
        for(Weight w: weights){
            res *= Weight.getFactor(w);
        }
        return res/weights.length;
    }

    /**
     * Calculates the fit for a {@link Genre genres} given. If a {@link Genre genre} isn't present in a {@link Movie movie} the fit is 0% otherwise the fit is as defined in the {@link Movie movie}
     * After receiving all fits we calculate the distance of all genres according to the {@link Distance distance function} and multiply them by the {@link Weight weight}
     */
    private Integer genreFit(Movie movie, Collection<Genre.GenreType> genres, Weight genreWeight, Distance distanceFunction) {
        int[] fits = genres.stream().mapToInt(movie::calculateGenreFit).toArray();
        return (int) (factorFor(genreWeight)* calculateDistance(fits, distanceFunction));
    }

    /**
     * Calculates the Fit for given negative {@link Keyword keywords} of a filter.
     * The fit is exactly opposite to the {@link #positiveKeywordFit(Movie, Collection, Weight, Distance)}
     */
    private Integer negativeKeywordFit(Movie movie, Collection<Keyword.KeywordValue> negativeKeywords, Weight weight, Distance distanceFunction) {
        return (int) (factorFor(weight) * (100 - positiveKeywordFit(movie, negativeKeywords, MovieFilter.Weight.NULL, distanceFunction)));
    }

    /**
     * Calculates the Fit for given positive {@link Keyword keywords} of a {@link MovieFilter filter}.
     * It tries to find each {@link Keyword keyword} in a movie, if it isn't present the fit is 0%, otherwise as specified in the {@link Movie movie}
     * After receiving all fits we calculate the distance of all {@link Keyword keywords} according to the {@link Distance distance function} and multiply them by the {@link Weight weight}
     */
    private Integer positiveKeywordFit(Movie movie, Collection<Keyword.KeywordValue> positiveKeywords, Weight weight, Distance distanceFunction) {
        int[] overlaps = new int[positiveKeywords.size()];
        Iterator<Keyword.KeywordValue> values = positiveKeywords.stream().iterator();
        int i = 0;
        while(values.hasNext()){
            Keyword movieValue = Keyword.findKeyword(values.next(), movie.getKeywords());
            overlaps[i++] = movieValue != null ? movieValue.getFit() : 0; //If no result found overlap is 0
        }
        return (int) (factorFor(weight) * calculateDistance(overlaps,  distanceFunction));
    }

    /**
     * Simply maps the {@link NumberPeople Number of People} to their {@link Concentration} counterpart
     * is only used for the {@link #concentrationFit(Movie, Integer, Relationship, Weight, Weight)} function when no {@link Relationship} is given
     */
    private Map<Concentration, Integer> peopleToConcentrationMap(Map<NumberPeople, Integer> numberPeopleMap) {
        Map<Concentration, Integer> concentrationMap = new HashMap<>();
        for(NumberPeople number: numberPeopleMap.keySet()){
            switch (number){
                case FEW: concentrationMap.put(Concentration.HARD, numberPeopleMap.get(NumberPeople.FEW));break;
                case MEDIUM: concentrationMap.put(Concentration.MODERATE, numberPeopleMap.get(NumberPeople.MEDIUM));break;
                case MANY: concentrationMap.put(Concentration.LOW, numberPeopleMap.get(NumberPeople.MANY));break;
            }
        }
        return concentrationMap;
    }

    /**
     * returns the fit with the smallest distance out of a Map
     */
    private Integer bestFit(Movie movie, Map<Concentration, Integer> concentrationMap) {
        int distance = Integer.MAX_VALUE;
        int bestFit = 0;
        for(Concentration c : concentrationMap.keySet()){
            int movieFit = movie.getConcentrationFit(c);
            int mapFit = concentrationMap.get(c);
            int d = Math.abs(movieFit - mapFit);
            if(d < distance){
                distance = d;
                bestFit = 100-d;
            }
        }
        return bestFit;
    }

    /**
     * Calculates the concentration fit for {@link #concentrationFit(Movie, Integer, Relationship, Weight, Weight)}  if the {@link Relationship} as well as the number of People is given by the filter
     */
    Integer calculateConcentrationFit(Concentration concentration, int numberWatchers, Relationship relationship, Map<NumberPeople, Integer> numberPeopleMap) {
        switch (concentration){
            case LOW: {
                if (relationship == Relationship.ROMANTIC && numberPeopleMap.get(NumberPeople.MANY) > 0) {
                    return numberPeopleMap.get(NumberPeople.MEDIUM) == 0 ? 100 : 25;
                }
                if (ObjUtil.isContained(relationship, Relationship.FAMILY, Relationship.PLATONIC) && numberPeopleMap.get(NumberPeople.MANY) > 0) {
                    return numberPeopleMap.get(NumberPeople.MEDIUM) == 0 ? 100 : 75;
                }
                return 0;
            }
            case MODERATE: {
                if(ObjUtil.isContained(relationship, Relationship.ROMANTIC, Relationship.FAMILY, Relationship.PLATONIC) && numberPeopleMap.get(NumberPeople.MEDIUM) > 0){
                    return numberPeopleMap.get(NumberPeople.FEW) > 0 || numberPeopleMap.get(NumberPeople.MANY) > 0 ? 50 : 100;
                }
                return 0;
            }
            case HARD: {
                if (numberWatchers == 1) {
                    return 100;
                }
                if (relationship == Relationship.ROMANTIC && numberPeopleMap.get(NumberPeople.FEW) > 0) {
                    return numberPeopleMap.get(NumberPeople.MEDIUM) == 0 ? 100 : 50;
                }
                if (ObjUtil.isContained(relationship, Relationship.FAMILY, Relationship.PLATONIC) && numberPeopleMap.get(NumberPeople.FEW) > 0) {
                    return 50;
                }
                return 0;
            }
            default: throw new IllegalArgumentException("No Calculation rule found for " + concentration);
        }
    }

    /**
     * Function to calculate the distance of a number of values according to the given {@link Distance distance function}
     * Is only used for calculating distances in single fits NOT by the final Fit
     */
    private int calculateDistance(int[] values, Settings.Distance distanceFunction){
        if(ObjUtil.isContained(distanceFunction, Distance.L2_ONLY_ON_FITS, Distance.L2_COMPLETE)){
            return (int) Math.sqrt(Arrays.stream(values).mapToDouble(MathUtil::sq).sum()/values.length);
        } else {
            return (Arrays.stream(values).sum()/values.length);
        }
    }

    /**
     * Calculates the fit multiplied by its {@link Weight weight} for the {@link Relationship}
     */
    private Integer relationshipFit(Movie movie, Relationship relationship, Weight relationshipWeight) {
        int movieFit = movie.getRelationshipFit(relationship);
        return (int) (factorFor(relationshipWeight) * movieFit);
    }

    /**
     * Calculates the fit for the emotionality, it uses the absolut distance between the {@link Movie movies} fit and the one given by the {@link MovieFilter filter}
     */
    private Integer emotionalityFit(Movie movie, Integer emotionalityFit, Weight weight){
        int movieFit = movie.getOptimalEmotionality();
        return (int) (factorFor(weight) * (100 - Math.abs(movieFit - emotionalityFit)));
    }

    /**
     * Calculates the fit for the investment a watcher should give, it uses the absolut distance between the {@link Movie movies} fit and the one given by the {@link MovieFilter filter}
     */
    private Integer investedFit(Movie movie, Integer invested, Weight investedWeight) {
        int movieFit = movie.getOptimalInvestment();
        return (int) (factorFor(investedWeight) * (100 - Math.abs(movieFit - invested)));
    }

    /**
     * Calculates the {@link Screen screen} fit of a {@link MovieFilter filter} through a step function
     */
    private Integer screenFit(Movie movie, Screen screen, Weight weight) {
        return (int)(factorFor(weight) * findScreenFit(movie, screen));
    }
    private Integer findScreenFit(Movie movie, Screen screen){
        switch (movie.getOptimalScreen()){
            case NOT_IMPORTANT: return 100; //all are equally valid
            case PHONE:
                switch (screen){
                    case PHONE: return 100;
                    case TV:    return 50;
                    case CINEMA:return 25;
                    case IMAX:  return 0;
                }
            case TV:
                switch (screen){
                    case PHONE: return 50;
                    case TV:    return 100;
                    case CINEMA:return 75;
                    case IMAX:  return 50;
                }
            case CINEMA:
                switch (screen){
                    case PHONE: return 25;
                    case TV:    return 50;
                    case CINEMA:return 100;
                    case IMAX:  return 75;
                }
            case IMAX:
                switch (screen){
                    case PHONE: return 0;
                    case TV:    return 25;
                    case CINEMA:return 75;
                    case IMAX:  return 100;
                }
            default: throw new IllegalArgumentException("No Fit defined for Screen size " + screen);
        }
    }


    public enum NumberPeople {
        FEW, MEDIUM, MANY
    }
    enum Concentration {
        LOW, MODERATE, HARD
    }
    /**
     * A list containing a {@link Tuple tuple} of two elements, can be used similar to a normal list
     * We do not implement list as most of the functionality isn't needed
     */
    private static class TupleList<T, K> {
        List<Tuple<T, K>> list;

        TupleList(){
            this.list = new ArrayList<>();
        }

        private boolean add(T t, K k){
            return list.add(new Tuple<>(t, k));
        }
        private boolean add(T t, K... k){return list.add(new Tuple<>(t, k));}

        private Tuple<T, K> remove(int i){
            return list.remove(i);
        }

        private boolean remove(Tuple<T, K> t){
            return list.remove(t);
        }

        private Tuple<T, K> get(int i){
            return list.get(i);
        }

        private int size(){
            return list.size();
        }

        private Stream<Tuple<T, K>> stream(){
            return list.stream();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("TupleList{\n");
            for(Tuple<T,K> t: list){
                sb.append(t.toString()).append("\n");
            }
            return sb.toString() + '}';
        }

        /**
         * Represents a Tuple with two different objects
         * Has currently a Hack where more than one of the second elements can be given to the Tuple, this however should be used with caution!!
         */
        private static class Tuple<T, K> {
            public final T first;
            public final K second;

            private final boolean hasMultipleSecond;
            private K[] multipleSeconds;

            Tuple(T first, K second) {
                this.first = first;
                this.second = second;
                hasMultipleSecond = false;
            }

            //hack: rbu 13.12.2021, needed for #ConcentrationFit(), should get a better solution when possible
            Tuple(T first, K... second) {
                this.first = first;
                this.second = null;
                this.hasMultipleSecond = true;
                this.multipleSeconds = second;
            }

            @Override
            public String toString() {
                       return "(" + first + ", " + second + ")";}

            public Stream<K> seconds() {
                if(hasMultipleSecond){
                    return Arrays.stream(multipleSeconds);
                } else {
                    List<K> list = new ArrayList<>();
                    list.add(second);
                    return list.stream();
                }
            }
        }
    }
}
