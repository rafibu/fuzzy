package ch.fuzzy.movie_suggester.server;

public class MovieFilter {

    private String keywords;
    private Genre.GenreType genre;

    public String getKeywords() {return keywords;}
    public void setKeywords(String keywords) {this.keywords = keywords;}

    public Genre.GenreType getGenre() {return genre;}
    public void setGenre(Genre.GenreType genre) {this.genre = genre;}

    public boolean hasValues() {
        return getGenre() != null || getKeywords() != null;
    }
}
