package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MovieFinder {

    private static final Logger log = LoggerFactory.getLogger(MovieFinder.class);

    private static MovieFinder INSTANCE;
    private final MovieRepository repo;

    public static void initialize(MovieRepository repo){
        INSTANCE = new MovieFinder(repo);
    }

    public static MovieFinder get(){ return INSTANCE; }

    public MovieFinder(MovieRepository repo){
        assert INSTANCE == null;
        this.repo = repo;
    }

    public List<MovieResult> findMovies(MovieFilter filter){
        if(filter == null) return new ArrayList<>(); //NOTE: rbu 29.10.2021, should only be the case if we go directly to the corresponding URL
        List<Movie> foundByGenre = repo.findByLanguagesContaining(filter.getLanguage());
        List<Movie> foundByPlatform = foundByGenre.stream().filter(m -> m.getPlatforms().stream().anyMatch(p -> filter.getPlatforms().contains(p))).collect(Collectors.toList()); //TODO: rbu 01.11.2021, add to Query for SQL
        return foundByPlatform.stream().map(m -> fittingMovie(m, filter)).sorted().collect(Collectors.toList());
    }

    /**
     * We calculate how much a {@code filter} fits into a {@code movie}
     * If possible all fuzzy variable (Genre, Watchers, Relationship, Keywords) are taken into account
     */
    private MovieResult fittingMovie(Movie movie, MovieFilter filter){
        List<Integer> fits = new ArrayList<>();
        if(filter.getGenres().size() > 0){ fits.add(genreFit(movie, filter.getGenres())); }
        if(filter.getNumberWatchers() != null || filter.getRelationship() != null){fits.add(concentrationFit(movie, filter.getNumberWatchers(), filter.getRelationship())); }
        if(filter.getPositiveKeywords() != null){ fits.add(positiveKeywordFit(movie, filter.getPositiveKeywords())); }
        if(filter.getNegativeKeywords() != null){ fits.add(negativeKeywordFit(movie, filter.getNegativeKeywords())); }
        int fit = (fits.stream().mapToInt(f -> f).sum())/fits.size();
        return new MovieResult(movie, fit);
    }

    private Integer concentrationFit(Movie movie, Integer numberWatchers, Relationship relationship) {
        Map<Concentration, Integer> concentrationMap = new HashMap<>();
        if(numberWatchers != null) {
            Map<NumberPeople, Integer> numberPeopleMap = new HashMap<>();
            Arrays.stream(NumberPeople.values()).forEach(v -> numberPeopleMap.put(v, calculatePeopleFit(v, numberWatchers)));
            if(relationship != null) {
                Arrays.stream(Concentration.values()).forEach(v -> concentrationMap.put(v, calculateConcentrationFit(v, numberWatchers, relationship, numberPeopleMap)));
                return bestFit(movie, concentrationMap);
            }
            return bestFit(movie, peopleToConcentrationMap(numberPeopleMap));
        }
        return movie.getModerateConcentrationFit(); //If we don't know how many people there are we just take one of the fits
    }

    private int calculatePeopleFit(NumberPeople membership, int numberWatchers) {
        switch (membership){
            case FEW: return numberWatchers <= 3 ? 100 : numberWatchers < 7 ? (100*(7-numberWatchers))/4 : 0;
            case MEDIUM:
                if(numberWatchers <= 2 || numberWatchers >= 8) return 0;
                if(numberWatchers < 4) return (100*(numberWatchers - 2))/2;
                if(numberWatchers > 6) return (100*(8-numberWatchers)/2);
                return 100;
            case MANY: return numberWatchers < 6 ? 0 : numberWatchers <= 8 ? (100*(numberWatchers-6))/2 : 100;
            default: throw new IllegalArgumentException("No Calculation rule found for " + membership);
        }
    }

    private Integer genreFit(Movie movie, Collection<Genre.GenreType> genres) {
        int[] fits = genres.stream().mapToInt(movie::calculateGenreFit).toArray();
        return Arrays.stream(fits).sum()/fits.length;
    }

    private Integer negativeKeywordFit(Movie movie, String negativeKeywords) {
        return 100 - positiveKeywordFit(movie, negativeKeywords);
    }

    private Integer positiveKeywordFit(Movie movie, String positiveKeywords) {
        List<String> keywords = List.of(positiveKeywords.split(","));
        keywords.parallelStream().forEach(k -> k = k.toLowerCase(Locale.ROOT).trim());
        List<Keyword.KeywordValue> keywordList = keywords.parallelStream().map(Keyword.KeywordValue::find).distinct().collect(Collectors.toList());
        int[] overlaps = new int[keywordList.size()];
        for(int i = 0; i < keywordList.size(); i++){
            Keyword movieValue = Keyword.findKeyword(keywordList.remove(0), movie.getKeywords());
            overlaps[i] = movieValue != null ? movieValue.getFit() : 0; //If no result found overlap is null
        }
        return (Arrays.stream(overlaps).sum())/keywordList.size(); //NOTE: rbu 31.10.2021, should we take found keywords or all written ones? -> Maybe change to Checkboxes anyway
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
                    return numberPeopleMap.get(NumberPeople.MEDIUM) == 0 ? 25 : 50;
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
