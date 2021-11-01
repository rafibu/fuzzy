package ch.fuzzy.movie_suggester.server;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;

    @Enumerated(EnumType.STRING)
    private final KeywordValue keyword;
    private int fit;

    public Keyword(Movie movie, KeywordValue keyword){
        this.movie = movie;
        this.keyword = keyword;
    }

    //Used by framework
    protected Keyword() {movie = null;keyword = null;}

    public static Keyword findKeyword(KeywordValue value, Set<Keyword> keywords) {
        Keyword found = keywords.parallelStream().findAny().filter(k -> k.keyword == value).orElse(null);
        //TODO: log if found == null
        return found;
    }

    public int getFit() {return fit;}
    public void setFit(int fit) {this.fit = fit;}

    public enum KeywordValue implements IFilterElement{
        SEX("Sex"),
        DRUGS("Drugs"),
        CARS("Cars");

        private final String name;

        KeywordValue(String name){
            this.name = name;
        }

        @Override public String getName() {return name;}

        public static KeywordValue find(String value) {
            try{ return KeywordValue.valueOf(value); } catch (IllegalArgumentException e) { return null; }
        }
    }
}
