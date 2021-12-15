package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieFilter;
import ch.fuzzy.movie_suggester.server.MovieFinder;
import ch.fuzzy.movie_suggester.server.MovieResult;
import ch.fuzzy.movie_suggester.util.ObjUtil;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * The Result Panel a user sees after filling out the Bot
 */
@Route("movie_results")
public class MovieResultPresenter extends VLayout{

    private static MovieFilter lastFilter;

    public MovieResultPresenter(){
        MovieFilter filter = ComponentUtil.getData(UI.getCurrent(), MovieFilter.class);
        if(filter != null) lastFilter = filter;
        List<MovieResult> movies = MovieFinder.get().findMovies(lastFilter);
        if(movies.size() > 0) {
            add(resultsLayout(movies));
            //Grid for testing purposes -> shows more information
//        final Grid<MovieResult> grid = new Grid<>(MovieResult.class);
//        grid.setHeight("300px");
//        grid.setColumns("movie.id", "movie.title", "movie.description", "movie.genres", "movie.platforms", "movie.languages", "fit");
//        grid.getColumnByKey("movie.id").setWidth("50px").setFlexGrow(0);
//        grid.setItems(movies);
//        add(grid);
        } else {
            addText("I'm Sorry, we don't have any Movie matching your description");
        }
    }

    public VLayout resultsLayout(List<MovieResult> results){
        VLayout resultLayout = new VLayout(this);
        HLayout layout = new HLayout(resultLayout);
        int i = 0;
        for(MovieResult res : results){
            layout.add(movieLayout(res));
            if(++i%6 == 0 || results.indexOf(res) == results.size()-1){
                resultLayout.add(layout);
                layout = new HLayout(resultLayout);
            }
        }
        return resultLayout;
    }
    public VLayout movieLayout(MovieResult result){
        Movie movie = result.getMovie();
        VLayout layout = new VLayout(this);
        layout.getElement().getStyle().set("width", "220px");
        Image image = Movie.generateImage(movie, true);
        image.addClickListener(event -> popUp(result.getMovie()).open());
        layout.add(image);
        layout.addTitle(movie.getTitle());
        layout.addText("Overlap: " + result.getFit() + "%");
        return layout;
    }

    public Dialog popUp(Movie movie){
        VLayout layout = new VLayout();
        layout.add(Movie.generateImage(movie, true));
        layout.addTitle(movie.getTitle());
        layout.addSpan(movie.getDescription());
        layout.addSpan("Genres: " + ObjUtil.toString(movie.getGenres()));
        layout.addSpan("Age Restriction: " + ObjUtil.toString(movie.getAgeRestriction()));
        layout.addSpan("Available on " + ObjUtil.toString(movie.getPlatforms()));
        Dialog dialog = new Dialog(layout);
        dialog.setMaxHeight("700px");
        dialog.setMaxWidth("500px");
        return dialog;
    }

}
