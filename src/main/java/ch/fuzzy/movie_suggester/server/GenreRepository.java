package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Hibernate Class to load {@link Genre}s from Repository
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {

	Genre findByType(Genre.GenreType type);
}
