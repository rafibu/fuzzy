package ch.fuzzy.movie_suggester.server;

/**
 * Represents the relationship between the people watching
 */
public enum Relationship implements IFilterElement {
    FAMILY("Family"),
    ROMANTIC("Romantical Partner(s)"),
    PLATONIC("Friend(s)");

    private final String name;

    Relationship(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
    @Override public String toString() { return getName(); }
}
