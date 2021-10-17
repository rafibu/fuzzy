package ch.fuzzy.movie_suggester.server;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A simple example to introduce building forms. As your real application is probably much
 * more complicated than this example, you could re-use this form in multiple places. This
 * example component is only used in MainView.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX.
 */
@SpringComponent
@UIScope
public class MovieEditor extends VerticalLayout implements KeyNotifier {

    private final MovieRepository repository;

    /**
     * The currently edited Movie
     */
    private Movie movie;

    /* Fields to edit properties in Movie entity */
    TextField title = new TextField("Title");
    TextField description = new TextField("Description");

    /* Action buttons */
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    ComboBox<Genre.GenreType> genreComboBox = new ComboBox<>(); //TODO: Change to MultiSelect

    Binder<Movie> binder = new Binder<>(Movie.class);
    private ChangeHandler changeHandler;

    @Autowired
    public MovieEditor(MovieRepository repository) {
        this.repository = repository;

        genreComboBox.setItems(Genre.GenreType.values());
        genreComboBox.setLabel("Genre");
        genreComboBox.addCustomValueSetListener(comboBoxCustomValueSetEvent -> movie.addGenre(genreComboBox.getValue()));

        add(title, description, genreComboBox, actions);

        // bind using naming convention
        binder.bindInstanceFields(this);

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
        cancel.setVisible(persisted);

        // Bind Movie properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.movie);

        setVisible(true);

        // Focus first name initially
        title.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }

}