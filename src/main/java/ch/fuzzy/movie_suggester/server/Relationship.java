package ch.fuzzy.movie_suggester.server;

public enum Relationship implements IFilterElement {
    FAMILY("a Family"),
    ROMANTIC("romantical Partners"),
    PLATONIC("platonic Friends");

    private final String name;

    Relationship(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
}
