package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.MathUtil;
import ch.fuzzy.movie_suggester.util.ObjUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static ch.fuzzy.movie_suggester.server.MovieFilter.Weight;

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
        return movies.stream().map(m -> fittingMovie(m, filter)).sorted().collect(Collectors.toList());
    }

    /**
     * We calculate how much a {@code filter} fits into a {@code movie}
     * If possible all fuzzy variable (Genre, Watchers, Relationship, Keywords) are taken into account
     */
    private MovieResult fittingMovie(Movie movie, MovieFilter filter){
        List<Integer> fits = new ArrayList<>();
        if(filter.getGenres() != null && filter.getGenres().size() > 0){ fits.add(genreFit(movie, filter.getGenres(), filter.getGenreWeight())); }
        if(filter.getNumberWatchers() != null || filter.getRelationship() != null){fits.add(concentrationFit(movie, filter.getNumberWatchers(), filter.getRelationship(), filter.getNumberWatchersWeight(), filter.getRelationshipWeight())); }
        if(filter.getPositiveKeywords() != null){ fits.add(positiveKeywordFit(movie, filter.getPositiveKeywords(), filter.getPositiveKeywordsWeight())); }
        if(filter.getNegativeKeywords() != null){ fits.add(negativeKeywordFit(movie, filter.getNegativeKeywords(), filter.getNegativeKeywordsWeight())); }
        int fit = calculateFinalFit(fits);
        return new MovieResult(movie, fit);
    }

    private int calculateFinalFit(List<Integer> fits){
        if(fits.size() == 0){ return 100; } //return 100 for no filter
        Settings settings = ObjUtil.assertUniqueNotNull(settingsRepository.findAll());
        if(settings.getDistanceFunction()== Settings.Distance.L1){
            return (fits.stream().mapToInt(f -> f).sum())/fits.size();
        } else {
            //we assume L2 Distance if not otherwise specified
            return (int)(Math.sqrt(fits.stream().mapToInt(f -> (int)MathUtil.sq(f)).sum()))/fits.size();
        }
    }

    private Integer concentrationFit(Movie movie, Integer numberWatchers, Relationship relationship, Weight numberWatchersWeight, Weight relationshipWeight) {
        Map<Concentration, Integer> concentrationMap = new HashMap<>();
        if(numberWatchers != null) {
            Map<NumberPeople, Integer> numberPeopleMap = new HashMap<>();
            Arrays.stream(NumberPeople.values()).forEach(v -> numberPeopleMap.put(v, calculatePeopleFit(v, numberWatchers)));
            if(relationship != null) {
                Arrays.stream(Concentration.values()).forEach(v -> concentrationMap.put(v, calculateConcentrationFit(v, numberWatchers, relationship, numberPeopleMap)));
                return (int)(factorFor(numberWatchersWeight) * factorFor(relationshipWeight) * bestFit(movie, concentrationMap));
            }
            return (int)(factorFor(numberWatchersWeight) * bestFit(movie, peopleToConcentrationMap(numberPeopleMap)));
        }
        return (int)(factorFor(numberWatchersWeight) * factorFor(relationshipWeight) * movie.getModerateConcentrationFit()); //If we don't know how many people there are we just take one of the fits
    }

    private int calculatePeopleFit(NumberPeople membership, int numberWatchers) {
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

    private double factorFor(MovieFilter.Weight weight){ return MovieFilter.Weight.getFactor(weight); }

    private Integer genreFit(Movie movie, Collection<Genre.GenreType> genres, MovieFilter.Weight genreWeight) {
        int[] fits = genres.stream().mapToInt(movie::calculateGenreFit).toArray();
        return (int) factorFor(genreWeight)*Arrays.stream(fits).sum()/fits.length;
    }

    private Integer negativeKeywordFit(Movie movie, Collection<Keyword.KeywordValue> negativeKeywords, MovieFilter.Weight weight) {
        return (int) factorFor(weight) * 100 - positiveKeywordFit(movie, negativeKeywords, MovieFilter.Weight.NULL);
    }

    private Integer positiveKeywordFit(Movie movie, Collection<Keyword.KeywordValue> positiveKeywords, MovieFilter.Weight weight) {
        int[] overlaps = new int[positiveKeywords.size()];
        Iterator<Keyword.KeywordValue> values = positiveKeywords.stream().iterator();
        int i = 0;
        while(values.hasNext()){
            Keyword movieValue = Keyword.findKeyword(values.next(), movie.getKeywords());
            overlaps[i++] = movieValue != null ? movieValue.getFit() : 0; //If no result found overlap is null
        }
        return (int) factorFor(weight) *(Arrays.stream(overlaps).sum())/positiveKeywords.size();
    }

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

    private Integer calculateConcentrationFit(Concentration concentration, int numberWatchers, Relationship relationship, Map<NumberPeople, Integer> numberPeopleMap) {
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

    private enum NumberPeople {
        FEW, MEDIUM, MANY;
    }
    enum Concentration {
        LOW, MODERATE, HARD;
    }
}
