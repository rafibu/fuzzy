package ch.fuzzy.movie_suggester;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieRepository;
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
    public CommandLineRunner loadData(MovieRepository repository) {
        return (args) -> {
            // fetch all movies
            log.info("Movies found with findAll():");
            log.info("-------------------------------");
            for (Movie movie : repository.findAll()) {
                log.info(movie.toString());
            }
            log.info("");

            // fetch a movie customer by ID
            Movie movie = repository.findById(1L).get();
            log.info("Movie found with findOne(1L):");
            log.info("--------------------------------");
            log.info(movie.toString());
            log.info("");

            // fetch movie by title
            log.info("Movie found with findByLastNameStartsWithIgnoreCase('Titanic'):");
            log.info("--------------------------------------------");
            for (Movie titanic : repository
                    .findByTitleStartsWithIgnoreCase("Titanic")) {
                log.info(titanic.toString());
            }
            log.info("");
        };
    }

}
