package util;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class UIElement extends Application {
    protected Stage openStage;

    protected abstract VBox startBox();

    @Override
    public void start(Stage primaryStage){
        initialize();
        startScreen(primaryStage);
    }

    protected void startScreen(Stage primaryStage){
        openStage = primaryStage;
        primaryStage.setScene(new Scene(startBox()));
        primaryStage.setTitle(getTitle());
        primaryStage.show();
    }

    public void startScreen(){
        startScreen(openStage);
    }

    protected MenuBar standardMenu(){
        return standardMenu(new MenuItem[0]);
    }

    protected MenuBar standardMenu(MenuItem... items){
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Datei");

        MenuItem itemNew = new MenuItem("Neu");
        itemNew.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        itemNew.setOnAction(event -> startScreen(openStage));

        menuFile.getItems().addAll(itemNew);
        menuFile.getItems().addAll(items);

        menuBar.getMenus().addAll(menuFile);
        return menuBar;
    }

    protected abstract String getTitle();

    protected void initialize(){}
}
