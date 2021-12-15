package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieRepository;
import ch.fuzzy.movie_suggester.server.Settings;
import ch.fuzzy.movie_suggester.server.SettingsRepository;
import ch.fuzzy.movie_suggester.util.ObjUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

/**
 * shows all {@link Movie}s and allows us to modify them
 * Also allows us to change the global {@link Settings}
 */
@Route("movie_editor")
public class MovieEditorPresenter extends VLayout {

    private final Grid<Movie> grid;
    private final MovieRepository repo;
    private final SettingsRepository settingsRepository;
    Settings settings;

    public MovieEditorPresenter(MovieRepository repo, SettingsRepository settingsRepo) {
        this.repo = repo;
        MovieEditorPanel editor = new MovieEditorPanel(repo);

        final TextField filter =new TextField();

        final Button addNewBtn = new Button("New movie");
        this.settingsRepository = settingsRepo;
        grid = new Grid<>(Movie.class);
        // build layout
        this.settings = ObjUtil.assertUniqueNotNull(settingsRepo.findAll());
        VLayout settingsLayout = new VLayout(this);
        settingsLayout.addRadioButtons("Distance function:", settings::setDistanceFunction, settings.getDistanceFunction(), Settings.Distance.values());
        settingsLayout.addCheckbox("Concentration Fit allowed", settings::setConcentrationFit, settings.isConcentrationFit());
        add(settingsLayout);
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        add(actions, grid, editor);

        grid.setHeight("300px");
        grid.setColumns("id", "title", "description");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        filter.setPlaceholder("Filter by title");

        grid.addColumn(Movie::showGenres);
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

    @Override
    public void fireStateChanged() {
        super.fireStateChanged();
        settingsRepository.save(settings);
    }
}
