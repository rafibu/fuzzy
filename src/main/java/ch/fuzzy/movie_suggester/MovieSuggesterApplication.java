package ch.fuzzy.movie_suggester;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieFinder;
import ch.fuzzy.movie_suggester.server.MovieRepository;
import ch.fuzzy.movie_suggester.server.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieSuggesterApplication {

    private static final Logger log = LoggerFactory.getLogger(MovieSuggesterApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MovieSuggesterApplication.class);
    }

    @Bean
    public CommandLineRunner loadData(MovieRepository repository, SettingsRepository settingsRepository) {
        MovieFinder.initialize(repository, settingsRepository);
        return (args) -> {
            // fetch all movies
            log.info("Movies found with findAll():");
            log.info("-------------------------------");
            for (Movie movie : repository.findAll()) {
                log.info(movie.toString());
            }
        };
    }

}
