package ch.fuzzy.movie_suggester.server;

public enum Relationship implements IFilterElement {
    FAMILY("Family"),
    ROMANTIC("Romantical Partners"),
    PLATONIC("Platonic Friends");

    private final String name;

    Relationship(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
    @Override public String toString() { return getName(); }
}
