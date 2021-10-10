package ui;

import engine.Genre;
import engine.SearchFilter;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

public class SearchFilterPresenter extends AbstractPresenter<SearchFilter>{

    public SearchFilterPresenter(Stage openStage, MenuBar standardMenu) {
        super(openStage, standardMenu, new SearchFilter());
        addTextBox("Keywords", getObject()::setKeywords);
        addDropdown("Genre", getObject()::setGenre, Genre.values());
    }
}
