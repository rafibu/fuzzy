package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The MovieFiller can be used to edit a new {@link Movie}, it'll fill all data according to a filter
 */
public class MovieFiller {

    private MovieFiller(){ /*should not be initialized*/ }

    /**
     * Fills the {@link Movie movie} in such a way that the given {@link MovieFilter filter} will result in a 100% overlap
     * !!! Caution: this method overrides MOST data saved on the {@link Movie movie} !!!
     */
    public static void fillMovie(Movie movie, MovieFilter filter){
        movie.setAgeRestriction(filter.getAgeRestriction() != null ? filter.getAgeRestriction() : AgeRestriction.NONE);
        set(movie::setPlatforms, filter.getPlatforms());
        set(movie::setGenres, filter.getGenres().stream().map(g -> new Genre(movie, g, 100)).collect(Collectors.toList()));
        setKeywords(movie, filter.getNegativeKeywords(), filter.getPositiveKeywords());
        set(movie::setLanguages, filter.getLanguage());
        setConcentrationFit(movie, filter.getRelationship(), filter.getNumberWatchers());
        movie.setOptimalEmotionality(filter.getEmotionality());
        movie.setOptimalInvestment(filter.getInvested());
        movie.setOptimalScreen(filter.getScreen());
        setRelationship(movie, filter);
    }

    private static void setRelationship(Movie movie, MovieFilter filter) {
        Relationship relationship = filter.getRelationship();
        boolean forKids = !AgeRestriction.TWELVE.restricts(filter.getAgeRestriction());
        if(relationship == null){ return; }
        switch (relationship){
            case FAMILY: movie.setFamilyFit(100); movie.setFriendsFit(25); movie.setRomanticFit(50); return;
            case ROMANTIC: movie.setFamilyFit(forKids ? 25: 0); movie.setFriendsFit(25); movie.setRomanticFit(100); return;
            case PLATONIC: movie.setFamilyFit(forKids ? 75 : 0); movie.setFriendsFit(100); movie.setRomanticFit(50); return;
            default: throw new IllegalArgumentException("Filler not implemented for Relationship: " + relationship);
        }
    }

    private static void setConcentrationFit(Movie movie, Relationship relationship, Integer numberWatchers) {
        if(relationship == null || numberWatchers == null || numberWatchers < 1){ return; }
        Map<MovieFinder.NumberPeople, Integer> numberPeopleMap = new HashMap<>();
        Arrays.stream(MovieFinder.NumberPeople.values()).forEach(v -> numberPeopleMap.put(v, MovieFinder.get().calculatePeopleFit(v, numberWatchers)));
        movie.setLowConcentrationFit(MovieFinder.get().calculateConcentrationFit(MovieFinder.Concentration.LOW, numberWatchers, relationship, numberPeopleMap));
        movie.setModerateConcentrationFit(MovieFinder.get().calculateConcentrationFit(MovieFinder.Concentration.MODERATE, numberWatchers, relationship, numberPeopleMap));
        movie.setHardConcentrationFit(MovieFinder.get().calculateConcentrationFit(MovieFinder.Concentration.HARD, numberWatchers, relationship, numberPeopleMap));
    }

    private static <T> void set(Consumer<Set<T>> setter, Collection<T> collection){
        if(collection != null){
            setter.accept(new HashSet<>(collection));
        }
    }

    private static <T> void set(Consumer<Set<T>> setter, T value){
        HashSet<T> set = new HashSet<>();
        set.add(value);
        setter.accept(set);
    }

    private static void setKeywords(Movie movie, Collection<Keyword.KeywordValue> negative, Collection<Keyword.KeywordValue> positive){
        List<Keyword> negKey = negative.stream().map(k -> new Keyword(movie, k, 0)).collect(Collectors.toList());
        List<Keyword> posKey = positive.stream().map(k -> new Keyword(movie, k, 100)).collect(Collectors.toList());
        movie.setKeywords(new HashSet<>(ObjUtil.concat(negKey, posKey)));
    }
}
