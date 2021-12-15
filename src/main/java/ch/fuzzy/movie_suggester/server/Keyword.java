package ch.fuzzy.movie_suggester.server;

import javax.persistence.*;
import java.util.Set;

/**
 * Keywords which a {@link Movie} describe including how well they fit the movie
 * @author rbu
 */
@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;

    @Enumerated(EnumType.STRING)
    private KeywordValue keyword;
    private int fit;

    public Keyword(Movie movie, KeywordValue keyword){
        this.movie = movie;
        this.keyword = keyword;
    }

    public Keyword(Movie movie, KeywordValue keyword, int fit){
        this(movie, keyword);
        this.fit = fit;
    }

    //Used by framework
    protected Keyword() {movie = null;keyword = null;}

    /**
     * finds the value in the Set keywords
     */
    public static Keyword findKeyword(KeywordValue value, Set<Keyword> keywords) {
        for(Keyword k: keywords){ if(k.keyword == value) return k; }
        return null;
    }

    public int getFit() {return fit;}
    public void setFit(int fit) {this.fit = fit;}

    public KeywordValue getKeyword() {return keyword;}
    public void setKeyword(KeywordValue keyword) {this.keyword = keyword;}

    public enum KeywordValue implements IFilterElement{
        SEX("Sex"),
        DRUGS("Drugs"),
        CARS("Cars"),
        MILITARY("Military"),
        SPY("Spy"),
        SUPERHERO("Superhero"),
        PARODY("Parody"),
        SATIRE("Satire"),
        ABSURDIST("Absurdist"),
        COMIC("Comic"),
        SCIENCE("Science"),
        CRIME("Crime"),
        MURDER("Murder"),
        MAGIC("Magic"),
        DETECTIVE("Detective"),
        CONTEMPORARY("Contemporary"),
        DARK_HUMOR("Dark Humor"),
        BIOGRAPHY("Biography"),
        AUTOBIOGRAPHY("Autobiography"),
        GHOST("Ghost"),
        MONSTER("Monster"),
        PARANORMAL("Paranormal"),
        MEDICINE("Medicine"),
        DOCTOR("Doctor"),
        NURSE("Nurse"),
        HOSPITAL("Hospital"),
        WAR("War"),
        KILLING("Killing"),
        REAL_LIFE("Real Life"),
        FUTURE("Future"),
        MEDIEVAL("Medieval"),
        HORSES("Horses"),
        ANIMALS("Animals"),
        PIGS("Pigs"),
        DOGS("Dogs"),
        CATS("Cats"),
        FAIRY_TALE("Fairy Tale"),
        PIRATES("Pirates"),
        WALL_STREET("Wall Street"),
        BANKS("Banks"),
        ROBBERY("Robbery"),
        HEIST("Heist"),
        ROBOTS("Robots"),
        PRISON("Prison"),
        RAPE("Rape"),
        ZOMBIE("Zombie"),
        CRASH("Crash"),
        ACCIDENT("Accident"),
        BIKER("Biker"),
        TRUCKER("Trucker"),
        DYSTOPIA("Dystopia"),
        KUNG_FU("Kung Fu"),
        HIGH_SCHOOL("High School"),
        NINJA("Ninja"),
        SERIAL_KILLER("Serial Killer"),
        TIME_TRAVEL("Time Travel"),
        BOLLYWOOD("Bollywood"),
        BETRAYAL("Betrayal"),
        DREAMS("Dreams"),
        CHILDREN("Children"),
        DYING("Dying"),
        PARENTHOOD("Parenthood"),
        KIDNAPPING("Kidnapping"),
        REVENGE("Revenge");

        private final String name;

        KeywordValue(String name){
            this.name = name;
        }

        @Override public String getName() {return name;}

        @Override public String toString() { return getName(); }

        public static KeywordValue find(String value) {
            try{ return KeywordValue.valueOf(value); } catch (IllegalArgumentException e) { return null; }
        }
    }
}
