package ch.fuzzy.movie_suggester.ui;

import ch.fuzzy.movie_suggester.server.*;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MovieFilterPresenter extends VLayout {

    private final Button searchBtn;
    private final MovieFilter filter;


    public MovieFilterPresenter(){
        filter = new MovieFilter();
        add(initialTextFilter());
        addText("------------------additional Filter------------------");
        addTextArea("Positive Keywords", filter::setPositiveKeywords);

        searchBtn = new Button("Search");
        searchBtn.addClickListener(e -> {
            ComponentUtil.setData(UI.getCurrent(), MovieFilter.class, filter);
            UI.getCurrent().navigate(MovieResultPresenter.class);
        });
        searchBtn.setEnabled(filter.hasValues());
        searchBtn.addThemeName("normal-button");
        add(searchBtn);
    }

    private HorizontalLayout initialTextFilter() {
        HLayout layout = new HLayout(){
            @Override
            public void fireStateChanged() {
                super.fireStateChanged();
                MovieFilterPresenter.this.fireStateChanged();
            }
        };
        layout.addText("We are ");
        layout.addSelect("", filter::setRelationship, Relationship.values());
        layout.addText(" of ");
        layout.addIntegerField("", filter::setNumberWatchers, 1, true, 1, 9999, 1);
        layout.addText(" and we want to watch a ");
        layout.addMultiSelect(filter::setGenres, Genre.GenreType.values());
        layout.addText(" in ");
        layout.addSelect("", filter::setLanguage, Language.values());
        layout.addText(" on ");
        layout.addMultiSelect(filter::setPlatforms, Platform.values());
        layout.addText(" on a ");
        layout.addSelect("", filter::setScreen, Screen.values());
        layout.addText(" Screen");
        return layout;
    }

    @Override
    public void fireStateChanged(){
        searchBtn.setEnabled(filter.hasValues());
    }
}
