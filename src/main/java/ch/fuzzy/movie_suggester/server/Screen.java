package ch.fuzzy.movie_suggester.server;

/**
 * Screen Size (Phone, TV, Cinema)
 */
public enum Screen implements IFilterElement {
    PHONE("Phone"),
    TV("TV"),
    CINEMA("Cinema");

    private final String name;

    Screen(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
    @Override public String toString() { return getName(); }
}
