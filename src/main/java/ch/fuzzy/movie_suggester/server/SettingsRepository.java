package ch.fuzzy.movie_suggester.server;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingsRepository extends JpaRepository<Settings, Long> {

    @Override
    List<Settings> findAll();
}
