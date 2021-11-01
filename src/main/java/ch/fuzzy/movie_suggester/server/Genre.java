package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.MathUtil;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Genre and how much a movie overlaps with it
 */
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;

    @Enumerated(EnumType.STRING)
    private final GenreType type;

    private Integer fit;

    public Genre(Movie movie, GenreType type){
        this.movie = movie;
        this.type = type;
        this.fit = 80; //NOTE: rbu 31.10.2021, fit assumed high in the beginning, can still be set lower later
    }

    protected Genre() {type = null; movie = null;}

    public String getName() {return type.name;}

    @Override public String toString() {return type.name;}

    public GenreType getType() { return type; }

    public int getFit() {return fit;}
    public void setFit(int fit) {assert MathUtil.isBetween(fit, 0, 100); this.fit = fit;}

    public enum GenreType implements IFilterElement{
        ROMANCE("Romance"),
        HORROR("Horror"),
        THRILLER("Thriller"),
        COMEDY("Comedy");

        private final String name;

        GenreType(String name){
            this.name = name;
        }

        @Override public String getName() {return name;}
    }
}



