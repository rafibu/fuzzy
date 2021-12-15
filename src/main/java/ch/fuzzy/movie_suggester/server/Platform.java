package ch.fuzzy.movie_suggester.server;

/**
 * Platform on which a certain {@link Movie} instance is available
 */
public enum Platform implements IFilterElement {
    NETFLIX("Netflix"),
    HBO("HBO Max"),
    DISNEY("Disney+");

    private final String name;

    Platform(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
    @Override public String toString() { return getName(); }
}
