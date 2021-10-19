package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Genre;
import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieFilter;
import ch.fuzzy.movie_suggester.server.MovieFinder;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Map;

@Route("movie_results")
public class MovieResultPresenter extends VerticalLayout{

    public MovieResultPresenter(){
        MovieFilter filter = ComponentUtil.getData(UI.getCurrent(), MovieFilter.class);
        List<Movie> movies = MovieFinder.get().findMovies(filter);
        final Grid<Movie> grid = new Grid<>(Movie.class);
        grid.setHeight("300px");
        grid.setColumns("id", "title", "description", "genres");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        grid.addColumn(Movie::showGenres);
        grid.setItems(movies);
        add(grid);
    }
}
