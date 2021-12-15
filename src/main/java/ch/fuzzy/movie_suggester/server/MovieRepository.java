package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Hibernate Class to load {@link Movie movies} from Repository
 * @author rbu
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {

	List<Movie> findByTitleStartsWithIgnoreCase(String title);

	List<Movie> findByLanguagesContaining(Language language);
}
