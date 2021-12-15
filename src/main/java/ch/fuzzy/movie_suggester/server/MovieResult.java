package ch.fuzzy.movie_suggester.server;

import ch.fuzzy.movie_suggester.util.ObjUtil;

/**
 * Represents a result of a {@link Movie movie} with a fit after a certain {@link MovieFilter filter} has been used
 * @author rbu
 */
public class MovieResult implements Comparable<MovieResult>{

    private final Movie movie;
    private final int fit;

    public MovieResult(Movie movie, int fit){
        this.movie = ObjUtil.assertNotNull(movie);
        this.fit = fit;
    }

    public Movie getMovie() {return movie;}

    public int getFit() {return fit;}

    @Override public int compareTo(MovieResult o) {
        if(getFit() != o.getFit()) {
            return Integer.compare(o.getFit(), getFit());
        }
        return String.CASE_INSENSITIVE_ORDER.compare(getMovie().getTitle(), o.getMovie().getTitle());
    }
}
