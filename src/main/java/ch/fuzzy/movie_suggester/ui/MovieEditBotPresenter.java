package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieFiller;
import ch.fuzzy.movie_suggester.server.MovieRepository;
import ch.fuzzy.movie_suggester.server.SettingsRepository;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;

/**
 * Creates a Bot to Edit a {@link Movie movie}.
 * Helps to fill fits more efficiently and accurately
 * @author rbu
 */
@Route("edit_bot")
public class MovieEditBotPresenter extends MovieFilterBotPresenter{

    private final MovieRepository repository;
    private final Movie movie;

    public MovieEditBotPresenter(SettingsRepository repo, MovieRepository movieRepository) {
        super(repo, true);
        this.repository = movieRepository;
        this.movie = ComponentUtil.getData(UI.getCurrent(), Movie.class);
    }

    @Override
    protected void gotoResult(){
        MovieFiller.fillMovie(movie, filter);
        repository.save(movie);
        UI.getCurrent().navigate(MovieEditorPresenter.class);
    }
}
