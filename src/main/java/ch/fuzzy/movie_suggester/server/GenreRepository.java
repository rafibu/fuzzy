package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

	Genre findByType(Genre.GenreType type);
}
