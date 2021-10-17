package ch.fuzzy.movie_suggester;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieEditor;
import ch.fuzzy.movie_suggester.server.MovieRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

@Route
public class MainView extends VerticalLayout {

	private final MovieRepository repo;

	private final MovieEditor editor;

	final Grid<Movie> grid;

	final TextField filter;

	private final Button addNewBtn;

	public MainView(MovieRepository repo, MovieEditor editor) {
		this.repo = repo;
		this.editor = editor;
		this.grid = new Grid<>(Movie.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New movie");

		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
		add(actions, grid, editor);

		grid.setHeight("300px");
		grid.setColumns("id", "title", "description", "genres");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

		filter.setPlaceholder("Filter by title");

		Grid.Column<Movie> genreColumn = grid.addColumn(Movie::showGenres);
		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> listMovies(e.getValue()));

		// Connect selected Movie to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editMovie(e.getValue());
		});

		// Instantiate and edit new Movie the new button is clicked
		addNewBtn.addClickListener(e -> editor.editMovie(new Movie("", "")));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			listMovies(filter.getValue());
		});

		// Initialize listing
		listMovies(null);
	}

	void listMovies(String filterText) {
		if (StringUtils.isEmpty(filterText)) {
			grid.setItems(repo.findAll());
		}
		else {
			grid.setItems(repo.findByTitleStartsWithIgnoreCase(filterText));
		}
	}

}