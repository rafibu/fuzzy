package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.Genre;
import ch.fuzzy.movie_suggester.server.MovieFilter;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.HashMap;
import java.util.Map;

public class MovieFilterPresenter extends VerticalLayout {

    private final Button searchBtn;
    private final MovieFilter filter;

    public MovieFilterPresenter(){
        filter = new MovieFilter();

        final TextField keywordsFilter = new TextField("Keywords");
        keywordsFilter.addKeyPressListener(e -> {
            filter.setKeywords(keywordsFilter.getValue());
            fireStateChanged();
        });
        final ComboBox<Genre.GenreType> genreComboBox = new ComboBox<>();

        genreComboBox.setItems(Genre.GenreType.values());
        genreComboBox.setLabel("Genre");
        genreComboBox.addValueChangeListener(event -> {
            filter.setGenre(genreComboBox.getValue());
            fireStateChanged();
        });
        add(keywordsFilter, genreComboBox);
        searchBtn = new Button("Search");
        searchBtn.addClickListener(e -> {
            Map<String, String> map = new HashMap<>();
            map.put("genre", filter.getGenre().name());
            map.put("keywords", filter.getKeywords());
            QueryParameters params = QueryParameters.simple(map);
            ComponentUtil.setData(UI.getCurrent(), MovieFilter.class, filter);
            UI.getCurrent().navigate(MovieResultPresenter.class);
        });
        searchBtn.setEnabled(filter.hasValues());
        add(searchBtn);
    }
    private void fireStateChanged(){
        searchBtn.setEnabled(filter.hasValues());
    }
}
