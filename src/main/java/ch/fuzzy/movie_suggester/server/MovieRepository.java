package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	List<Movie> findByTitleStartsWithIgnoreCase(String title);

	@Query(value = "SELECT m FROM Movie m WHERE ?1 in (SELECT g.type from m.genres g)")
	List<Movie> findByGenre(String genre);
}
