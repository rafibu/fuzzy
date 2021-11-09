package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Genre;
import ch.fuzzy.movie_suggester.server.Movie;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Arrays;

public class MovieGenrePanel extends VLayout{

    private final Movie movie;
    private final Button addBtn;

    public MovieGenrePanel(MovieEditorPanel parent){
        super(parent);
        movie = parent.getMovie();
        addBtn = new Button("Add Genre");
        addBtn.addClickListener(e -> {
            addGenreRow(movie.createGenre());
            fireStateChanged();
        });
        addBtn.setEnabled(movie.getGenres().stream().allMatch(g -> g.getType() != null));
        add(addBtn);
        movie.getGenres().forEach(this::addGenreRow);
    }

    private void addGenreRow(Genre genre){
        HLayout row = new HLayout(this);
        row.addCombobox("", genre::setType, genre.getType(), Arrays.stream(Genre.GenreType.values()).filter(g -> g==genre.getType() || !movie.hasGenre(g)).toArray(Genre.GenreType[]::new));
        row.addIntegerField("", genre::setFit, genre.getFit(), true, 0, 100, 1);
        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(e -> {
                    movie.removeGenre(genre);
                    remove(row);
                    fireStateChanged();

        });
        row.add(deleteBtn);
        add(row);
    }

    @Override
    public void fireStateChanged() {
        super.fireStateChanged();
        addBtn.setEnabled(movie.getGenres().stream().allMatch(g -> g.getType() != null));
    }
}
