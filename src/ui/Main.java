package ui;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import util.UIElement;
import util.UiUtil;

public class Main extends UIElement {

    @Override
    protected VBox startBox() {
        VBox box = new VBox();
        box.setPrefSize(420, 420);
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(10);
        box.getChildren().add(standardMenu());
        box.getChildren().addAll(UiUtil.button("Search", e -> new SearchFilterPresenter(openStage, standardMenu()).show()));
        return box;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }


    @Override
    protected String getTitle() {return "Movie Suggester";}
}
