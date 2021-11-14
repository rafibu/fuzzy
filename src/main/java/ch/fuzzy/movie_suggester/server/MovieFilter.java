package ch.fuzzy.movie_suggester.server;

import java.security.Key;
import java.util.Collection;
import java.util.HashSet;

public class MovieFilter {

    private Collection<Genre.GenreType> genres;
    private Language language;

    //-	Number of people watching
    private Integer numberWatchers;

    //-	Platform availability (Netflix, HBO, Disney+ etc.)
    private Collection<Platform> platforms;

    //-	How emotional should the movie get?
    private Integer emotionality;

    //-	How invested should one get in the movie?
    private Integer invested;

    //-	Relationship of people watching (i.e. family, romantical, platonic)
    private Relationship relationship;

    private AgeRestriction ageRestriction;

    private Screen screen;

    private Collection<Keyword.KeywordValue> positiveKeywords;
    private Collection<Keyword.KeywordValue>  negativeKeywords;

    public MovieFilter(){
        this.platforms = new HashSet<>();
        this.genres = new HashSet<>();
        numberWatchers = 1; //Default
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

    public boolean hasValues() {
        return getGenres().size() > 0 ||
                getLanguage() != null ||
                getNumberWatchers() != null ||
                getPlatforms().size() > 0 ||
                getEmotionality() != null ||
                getInvested() != null ||
                getRelationship() != null ||
                getScreen() != null ||
                getPositiveKeywords() != null ||
                getNegativeKeywords() != null;
    }
}
