package ch.fuzzy.movie_suggester.server;

public enum Platform implements IFilterElement {
    NETFLIX("Netflix"),
    HBO("HBO Max"),
    DISNEY("Disney+");

    private final String name;

    Platform(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
}
