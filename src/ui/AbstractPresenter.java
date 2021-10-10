package ui;

import engine.Genre;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.UiUtil;

import java.util.function.Consumer;

public abstract class AbstractPresenter<T> {

    private T object;
    private final Stage openStage;
    private final VBox box;

    public AbstractPresenter(Stage openStage, MenuBar standardMenu, T object) {
        this.openStage = openStage;
        this.object = object;
        this.box = emptyBox(standardMenu);
    }

    /**
     * used to render the empty box around everything
     */
    private VBox emptyBox(MenuBar menuBar){
        VBox box = new VBox();
        box.setPrefSize(400, 400);
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(10);
        box.getChildren().add(menuBar);
        return box;
    }

    /**
     * add an unspecified Element to this Presenter
     */
    protected void addElement(Pane element){
        box.getChildren().addAll(element);
    }

    /**
     * adds a Row to this Presenter
     */
    protected void addRow(Pane element){
        box.getChildren().addAll(new HBox(element));
    }

    /**
     * changes UI to this element
     */
    public void show(){
        openStage.setScene(new Scene(box));
    }

    public T getObject() {return object;}

    /**
     * Tells the UI Elements that the objects state has changed, they should act accordingly
     */
    protected void fireStateChanged(){ /*subclass responsibility*/}

    protected void addTextBox(String title, Consumer<String> setter){
        addElement(UiUtil.textBox(title, setter));
    }

    protected  <E extends Enum<E>> void addDropdown(String genre, Consumer<E> setter, E[] values) {
        addElement(UiUtil.dropdown(genre, setter, values));
    }

}
