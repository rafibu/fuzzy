package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Language;
import ch.fuzzy.movie_suggester.server.Movie;
import ch.fuzzy.movie_suggester.server.MovieRepository;
import ch.fuzzy.movie_suggester.server.Platform;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class MovieEditor extends VLayout implements KeyNotifier {

    private final MovieRepository repository;

    /**
     * The currently edited Movie
     */
    private Movie movie;

    private HLayout panel;

    /* Action buttons */
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<Movie> binder = new Binder<>(Movie.class);
    private ChangeHandler changeHandler;

    @Autowired
    public MovieEditor(MovieRepository repository) {
        this.repository = repository;

        add(panel = new HLayout());
        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editMovie(movie));
        setVisible(false);
    }

    private HLayout renderMovie() {
        HLayout panel = new HLayout();

        VLayout firstRow = new VLayout();
        firstRow.add(actions);
        firstRow.addTextfield("Title", movie::setTitle, movie.getTitle());
        firstRow.addTextArea("Description", movie::setDescription, movie.getDescription());

        VLayout secondRow = new VLayout();
        secondRow.addMultiSelect(movie::setLanguages, movie.getLanguages(), Language.values());
        secondRow.addMultiSelect(movie::setPlatforms, movie.getPlatforms(), Platform.values());

        VLayout thirdRow = new VLayout();
        thirdRow.addIntegerField("Low Concentration Fit", movie::setLowConcentrationFit, movie.getLowConcentrationFit(), true, 0, 100, 1);
        thirdRow.addIntegerField("Moderate Concentration Fit", movie::setModerateConcentrationFit, movie.getModerateConcentrationFit(), true, 0, 100, 1);
        thirdRow.addIntegerField("Hard Concentration Fit", movie::setHardConcentrationFit, movie.getHardConcentrationFit(), true, 0, 100, 1);

        //TODO: add Keywords
        //TODO add Genres

        panel.add(firstRow, secondRow, thirdRow);
        return panel;
    }

    void delete() {
        repository.delete(movie);
        changeHandler.onChange();
    }

    void save() {
        repository.save(movie);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editMovie(Movie movie) {
        if (movie == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = movie.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            this.movie = repository.findById(movie.getId()).get();
        }
        else {
            this.movie = movie;
        }
        panel.removeAll();
        remove(panel);
        panel = renderMovie();
        add(panel);
        cancel.setVisible(persisted);

        // Bind Movie properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.movie);

        setVisible(true);
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

}