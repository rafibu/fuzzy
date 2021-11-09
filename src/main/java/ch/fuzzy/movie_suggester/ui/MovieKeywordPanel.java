package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Keyword;
import ch.fuzzy.movie_suggester.server.Movie;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Arrays;

//TODO: rbu 04.11.2021, maybe create abstract Superclass with GenrePanel
public class MovieKeywordPanel extends VLayout{

    private final Movie movie;
    private final Button addBtn;

    public MovieKeywordPanel(MovieEditorPanel parent){
        super(parent);
        movie = parent.getMovie();
        addBtn = new Button("Add Keyword");
        addBtn.addClickListener(e -> {
            addKeywordRow(movie.createKeyword());
            fireStateChanged();
        });
        addBtn.setEnabled(movie.getKeywords().stream().allMatch(g -> g.getKeyword() != null));
        add(addBtn);
        movie.getKeywords().forEach(this::addKeywordRow);
    }

    private void addKeywordRow(Keyword keyword){
        HLayout row = new HLayout(this);
        row.addCombobox("", keyword::setKeyword, keyword.getKeyword(), Arrays.stream(Keyword.KeywordValue.values()).filter(k -> k==keyword.getKeyword() || !movie.hasKeyword(k)).toArray(Keyword.KeywordValue[]::new));
        row.addIntegerField("", keyword::setFit, keyword.getFit(), true, 0, 100, 1);
        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(e -> {
                    movie.removeKeyword(keyword);
                    remove(row);
                    fireStateChanged();

        });
        row.add(deleteBtn);
        add(row);
    }

    @Override
    public void fireStateChanged() {
        super.fireStateChanged();
        addBtn.setEnabled(movie.getKeywords().stream().allMatch(g -> g.getKeyword() != null));
    }
}
