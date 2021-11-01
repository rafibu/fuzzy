package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieFilter;
import ch.fuzzy.movie_suggester.server.MovieFinder;
import ch.fuzzy.movie_suggester.server.MovieResult;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("movie_results")
public class MovieResultPresenter extends VerticalLayout{

    private static MovieFilter lastFilter;

    public MovieResultPresenter(){
        MovieFilter filter = ComponentUtil.getData(UI.getCurrent(), MovieFilter.class);
        if(filter != null) lastFilter = filter;
        List<MovieResult> movies = MovieFinder.get().findMovies(lastFilter);
        final Grid<MovieResult> grid = new Grid<>(MovieResult.class);
        grid.setHeight("300px");
        grid.setColumns("movie.id", "movie.title", "movie.description", "movie.genres", "movie.platforms", "movie.languages", "fit");
        grid.getColumnByKey("movie.id").setWidth("50px").setFlexGrow(0);
        grid.setItems(movies);
        add(grid);
    }
}
