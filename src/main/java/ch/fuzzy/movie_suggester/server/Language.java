package ch.fuzzy.movie_suggester.server;

/**
 * Language a {@link Movie} can be watched in
 * @author rbu
 */
public enum Language implements IFilterElement {
    EN("English"),
    DE("German"),
    FR("French"),
    IT("Italian");

    private final String name;

    Language(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
    @Override public String toString() { return getName(); }
}
