package ch.fuzzy.movie_suggester.server;

import java.util.List;
import java.util.Locale;

public class MovieFinder {

    private static MovieFinder INSTANCE;
    private final MovieRepository repo;

    public static void initialize(MovieRepository repo){
        INSTANCE = new MovieFinder(repo);
    }

    public static MovieFinder get(){ return INSTANCE; }

    public MovieFinder(MovieRepository repo){
        assert INSTANCE == null;
        this.repo = repo;
    }

    public List<Movie> findMovies(MovieFilter filter){
        if(filter.getKeywords() != null) {
            List<String> keywords = List.of(filter.getKeywords().split(","));
            keywords.forEach(k -> k = k.toLowerCase(Locale.ROOT).trim());
        }
        return repo.findByGenre(filter.getGenre().toString());
    }
}
