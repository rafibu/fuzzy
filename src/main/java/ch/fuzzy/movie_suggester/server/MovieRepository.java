package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	List<Movie> findByTitleStartsWithIgnoreCase(String title);

	List<Movie> findByLanguagesContaining(Language language);
}
