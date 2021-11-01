package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;

public class MovieResult implements Comparable<MovieResult>{

    private final Movie movie; //NOTE: rbu 31.10.2021, maybe only add Name and Picture to reduce load
    private final int fit;

    public MovieResult(Movie movie, int fit){
        this.movie = ObjUtil.assertNotNull(movie);
        this.fit = fit;
    }

    public Movie getMovie() {return movie;}

    public int getFit() {return fit;}

    @Override public int compareTo(MovieResult o) {return Integer.compare(getFit(), o.getFit());}
}
