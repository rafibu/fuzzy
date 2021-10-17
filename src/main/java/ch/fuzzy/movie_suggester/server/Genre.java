package ch.fuzzy.movie_suggester.server;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Class as indirection to Enum as Enums cannot be persisted for Many to many mapping
 */
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

    private final GenreType type;

    public Genre(GenreType type){
        this.type = type;
    }

    public Genre() {type = null;}

    public String getName() {return type.name;}

    @Override public String toString() {return type.name;}

    public GenreType getType() { return type; }

    public enum GenreType{
        ROMANCE("Romance"),
        HORROR("Horror"),
        THRILLER("Thriller"),
        COMEDY("Comedy");

        private final String name;

        GenreType(String name){
            this.name = name;
        }
    }
}



