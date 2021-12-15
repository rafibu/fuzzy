package ch.fuzzy.movie_suggester.server;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a Filter which can be used to find matching {@link Movie} objects
 */
public class MovieFilter {

    private Collection<Genre.GenreType> genres;
    private Weight genreWeight;

    private Language language;

    //-	Number of people watching
    private Integer numberWatchers;
    private Weight numberWatchersWeight;

    //-	Platform availability (Netflix, HBO, Disney+ etc.)
    private Collection<Platform> platforms;

    //-	How emotional should the movie get?
    private Integer emotionality;
    private Weight emotionalityWeight;

    //-	How invested should one get in the movie?
    private Integer invested;
    private Weight investedWeight;


    //-	Relationship of people watching (i.e. family, romantical, platonic)
    private Relationship relationship;
    private Weight relationshipWeight;

    private AgeRestriction ageRestriction;

    private Screen screen;
    private Weight screenWeight;

    private Collection<Keyword.KeywordValue> positiveKeywords;
    private Weight positiveKeywordsWeight;
    private Collection<Keyword.KeywordValue>  negativeKeywords;
    private Weight negativeKeywordsWeight;

    public MovieFilter(){
        this.platforms = new HashSet<>();
        this.genres = new HashSet<>();
//        numberWatchers = 1; //Default
    }

    public Collection<Genre.GenreType> getGenres() {return genres;}
    public void setGenres(Collection<Genre.GenreType>  genres) {this.genres = genres;}

    public Language getLanguage() {return language;}
    public void setLanguage(Language language) {this.language = language;}

    public Integer getNumberWatchers() {return numberWatchers;}
    public void setNumberWatchers(int numberWatchers) { if(numberWatchers > 0){this.numberWatchers = numberWatchers;} else {this.numberWatchers = null;}}

    public Collection<Platform> getPlatforms() {return platforms;}

    public AgeRestriction getAgeRestriction() {return ageRestriction;}
    public void setAgeRestriction(AgeRestriction ageRestriction) {this.ageRestriction = ageRestriction;}

    public void setPlatforms(Collection<Platform> platforms) {this.platforms = platforms;}

    public Integer getEmotionality() {return emotionality;}
    public void setEmotionality(Integer emotionality) {this.emotionality = emotionality;}

    public Integer getInvested() {return invested;}
    public void setInvested(Integer invested) {this.invested = invested;}

    public Relationship getRelationship() {return relationship;}
    public void setRelationship(Relationship relationship) {this.relationship = relationship;}

    public Screen getScreen() {return screen;}
    public void setScreen(Screen screen) {this.screen = screen;}

    public Collection<Keyword.KeywordValue> getPositiveKeywords() {return positiveKeywords;}
    public void setPositiveKeywords(Collection<Keyword.KeywordValue>  positiveKeywords) {this.positiveKeywords = positiveKeywords;}

    public Collection<Keyword.KeywordValue> getNegativeKeywords() {return negativeKeywords;}
    public void setNegativeKeywords(Collection<Keyword.KeywordValue> negativeKeywords) {this.negativeKeywords = negativeKeywords;}

    public Weight getGenreWeight() {return genreWeight;}
    public void setGenreWeight(Weight genreWeight) {this.genreWeight = genreWeight;}

    public Weight getNumberWatchersWeight() {return numberWatchersWeight;}
    public void setNumberWatchersWeight(Weight numberWatchersWeight) {this.numberWatchersWeight = numberWatchersWeight;}

    public Weight getEmotionalityWeight() {return emotionalityWeight;}
    public void setEmotionalityWeight(Weight emotionalityWeight) {this.emotionalityWeight = emotionalityWeight;}

    public Weight getInvestedWeight() {return investedWeight;}
    public void setInvestedWeight(Weight investedWeight) {this.investedWeight = investedWeight;}

    public Weight getRelationshipWeight() {return relationshipWeight;}
    public void setRelationshipWeight(Weight relationshipWeight) {this.relationshipWeight = relationshipWeight;}

    public Weight getPositiveKeywordsWeight() {return positiveKeywordsWeight;}
    public void setPositiveKeywordsWeight(Weight positiveKeywordsWeight) {this.positiveKeywordsWeight = positiveKeywordsWeight;}

    public Weight getNegativeKeywordsWeight() {return negativeKeywordsWeight;}
    public void setNegativeKeywordsWeight(Weight negativeKeywordsWeight) {this.negativeKeywordsWeight = negativeKeywordsWeight;}

    public void setScreenWeight(Weight weight) {this.screenWeight = weight;}
    public Weight getScreenWeight() {return screenWeight;}

    /**
     * Weights used for fuzzy filter variables
     */
    public enum Weight implements IFilterElement {
        DNEGATIVE("--", 0.25),
        NEGATIVE("-", 0.5),
        NULL("0", 1),
        POSITIVE("+", 2),
        DPOSITIVE("++", 4);

        private final String name;
        private final double factor;

        Weight(String name, double factor) {
            this.name = name;
            this.factor = factor;
        }

        @Override public String getName() {return name;}

        /**
         * convenience function to get the factor from a given {@link Weight weight} or 1 if weight is null
         */
        public static double getFactor(Weight weight) {return weight != null ? weight.factor : 1;}
    }
}
