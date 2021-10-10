package engine;

public enum Genre {
    ROMANCE("Romance"),
    HORROR("Horror"),
    THRILLER("Thriller"),
    COMEDY("Comedy");

    private final String name;

    Genre(String name){
        this.name = name;
    }

    public String getName() {return name;}

    @Override public String toString() {return name;}
}
