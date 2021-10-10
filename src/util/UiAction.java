package util;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

public class UiAction {

    private final MenuItem menuItem;
    private final Button button;

    public UiAction(String name, EventHandler<ActionEvent> handler){
        this.button = UiUtil.button(name, handler);
        this.menuItem = UiUtil.menuItem(name, handler);
    }

    public MenuItem getMenuItem() {return menuItem;}
    public Button getButton() {return button;}
}
