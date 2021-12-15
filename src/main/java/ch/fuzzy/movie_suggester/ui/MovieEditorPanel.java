package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.*;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.UI;
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

/**
 * Panel for a Single {@link Movie} instance. Here all Data of a {@link Movie} can be changed
 * @author rbu
 */
@SpringComponent
@UIScope
public class MovieEditorPanel extends VLayout implements KeyNotifier {

    private final MovieRepository repository;

    /**
     * The currently edited Movie
     */
    private Movie movie;

    private VLayout panel;

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

        add(panel = new VLayout(this));
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

    private VLayout renderMovie() {
        VLayout panel = new VLayout(this);
        HLayout firstRow = new HLayout(panel);

        VLayout firstColumn = new VLayout(firstRow);
        firstColumn.add(actions);
        firstColumn.addTextfield("Title", movie::setTitle, movie.getTitle());
        firstColumn.addTextArea("Description", movie::setDescription, movie.getDescription());
        Button startBotBtn = new Button("Fill with Bot");
        startBotBtn.addClickListener(c -> gotoBot(movie));
        firstColumn.add(startBotBtn);

        VLayout secondColumn = new VLayout(firstRow);
        secondColumn.addSelect("Age Restriction", movie::setAgeRestriction, movie.getAgeRestriction(), AgeRestriction.values());
        secondColumn.addMultiSelect(movie::setLanguages, movie.getLanguages(), Language.values());
        secondColumn.addMultiSelect(movie::setPlatforms, movie.getPlatforms(), Platform.values());

        VLayout thirdColumn = new VLayout(firstRow);
        thirdColumn.addIntegerField("Low Concentration Fit", movie::setLowConcentrationFit, movie.getLowConcentrationFit(), true, 0, 100, 1);
        thirdColumn.addIntegerField("Moderate Concentration Fit", movie::setModerateConcentrationFit, movie.getModerateConcentrationFit(), true, 0, 100, 1);
        thirdColumn.addIntegerField("Hard Concentration Fit", movie::setHardConcentrationFit, movie.getHardConcentrationFit(), true, 0, 100, 1);

        VLayout fourthColumn = new VLayout(firstRow);
        fourthColumn.addIntegerField("Romantic Fit", movie::setRomanticFit, movie.getRomanticFit(), true, 0, 100, 1);
        fourthColumn.addIntegerField("Family Fit", movie::setFamilyFit, movie.getFamilyFit(), true, 0, 100, 1);
        fourthColumn.addIntegerField("Friends Fit", movie::setFriendsFit, movie.getFriendsFit(), true, 0, 100, 1);


        VLayout fifthColumn = new VLayout(firstRow);
        fifthColumn.addIntegerField("Optimal Emotionality Fit", movie::setOptimalEmotionality, movie.getOptimalEmotionality(), true, 0, 100, 1);
        fifthColumn.addIntegerField("Optimal Investment Fit", movie::setOptimalInvestment, movie.getOptimalInvestment(), true, 0, 100, 1);
        fifthColumn.addSelect("Optimal Screen Fit", movie::setOptimalScreen, movie.getOptimalScreen(), Screen.values());



        VLayout sixthColumn = new VLayout(firstRow);
        initUploaderImage(sixthColumn);

        firstRow.add(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn, sixthColumn);

        HLayout secondRow = new HLayout(panel);

        VLayout columnOne = new VLayout(secondRow);
        columnOne.add(new MovieGenrePanel(this));

        VLayout columnTwo = new VLayout(secondRow);
        columnTwo.add(new MovieKeywordPanel(this));

        secondRow.add(columnOne, columnTwo);

        panel.add(firstRow, secondRow);
        return panel;
    }

    private void gotoBot(Movie movie){
            ComponentUtil.setData(UI.getCurrent(), Movie.class, movie);
            UI.getCurrent().navigate(MovieEditBotPresenter.class);
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
    }

    private void showImage(ILayout layout) {
        Image image = Movie.generateImage(movie, false);
        if(image != null) {
            image.setHeight("100%");
            layout.add(image);
        }
    }
}