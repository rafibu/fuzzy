package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Hibernate Class to load {@link Settings}s from Repository
 * There should always only be one {@link Settings} per Database
 * @author rbu
 */
public interface SettingsRepository extends JpaRepository<Settings, Long> {

    @Override
    List<Settings> findAll();
}
