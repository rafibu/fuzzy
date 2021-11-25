package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.*;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SpringComponent
@UIScope
public class MovieEditorPanel extends VLayout implements KeyNotifier {

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
    public MovieEditorPanel(MovieRepository repository) {
        super();
        this.repository = repository;

        add(panel = new HLayout(this));
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
        HLayout panel = new HLayout(this);

        VLayout firstRow = new VLayout(this);
        firstRow.add(actions);
        firstRow.addTextfield("Title", movie::setTitle, movie.getTitle());
        firstRow.addTextArea("Description", movie::setDescription, movie.getDescription());

        VLayout secondRow = new VLayout(this);
        secondRow.addSelect("Age Restriction", movie::setAgeRestriction, movie.getAgeRestriction(), AgeRestriction.values());
        secondRow.addMultiSelect(movie::setLanguages, movie.getLanguages(), Language.values());
        secondRow.addMultiSelect(movie::setPlatforms, movie.getPlatforms(), Platform.values());

        VLayout thirdRow = new VLayout(this);
        thirdRow.addIntegerField("Low Concentration Fit", movie::setLowConcentrationFit, movie.getLowConcentrationFit(), true, 0, 100, 1);
        thirdRow.addIntegerField("Moderate Concentration Fit", movie::setModerateConcentrationFit, movie.getModerateConcentrationFit(), true, 0, 100, 1);
        thirdRow.addIntegerField("Hard Concentration Fit", movie::setHardConcentrationFit, movie.getHardConcentrationFit(), true, 0, 100, 1);

        VLayout fourthRow = new VLayout(this);
        fourthRow.add(new MovieGenrePanel(this));

        VLayout fifthRow = new VLayout(this);
        fifthRow.add(new MovieKeywordPanel(this));

        VLayout sixthRow = new VLayout(this);
        initUploaderImage(sixthRow);

        panel.add(firstRow, secondRow, thirdRow, fourthRow, fifthRow, sixthRow);
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
        if(this.movie != null) {
            repository.save(movie);
        }
        final boolean persisted = movie.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            this.movie = repository.findById(movie.getId()).get();
        } else {
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

    public Movie getMovie() {return movie;}

    private void initUploaderImage(ILayout layout) {
        save();
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg","image/jpg", "image/png", "image/gif");

        upload.addSucceededListener(event -> {
            String attachmentName = event.getFileName();
            try {
                boolean hadImage = movie.getMoviePicture() != null;
                // The image can be jpg png or gif, but we store it always as png file in this example
                BufferedImage inputImage = ImageIO.read(buffer.getInputStream(attachmentName));
                ByteArrayOutputStream pngContent = new ByteArrayOutputStream();
                ImageIO.write(inputImage, "png", pngContent);
                saveProfilePicture(pngContent.toByteArray());
                if(!hadImage) showImage(layout);
                layout.fireStateChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        layout.add(upload);
        showImage(layout);
        Button delete = new Button("Delete Picture");
        delete.addClickListener(e -> {
            movie.setMoviePicture(null);
            layout.fireStateChanged();
        });
        delete.setEnabled(movie.getMoviePicture() != null);
        layout.add(delete);
    }

    private void saveProfilePicture(byte[] imageBytes) {
        getMovie().setMoviePicture(imageBytes);
        repository.save(movie);
    }

    private void showImage(ILayout layout) {
        Image image = Movie.generateImage(movie);
        if(image != null) {
            image.setHeight("100%");
            layout.add(image);
        }
    }
}