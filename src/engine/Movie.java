package engine;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name="Movies")
public class Movie {

    private final String title;
    private final String description;
    private final Set<String> keywords;
    private final Set<Genre> genres;

    public Movie(String title, String description, Set<String> keywords, Set<Genre> genres) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.genres = genres;
    }

    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public Set<String> getKeywords() {return keywords;}
    public Set<Genre> getGenres() {return genres;}
}
