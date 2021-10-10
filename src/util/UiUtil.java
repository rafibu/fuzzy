package util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UiUtil {

    public static Text title(String title) {
        Text t = new Text(title);
        t.setFont(Font.font(t.getFont().getName(), FontWeight.BOLD, 13));
        return t;
    }

    public static Button button(String name, EventHandler<ActionEvent> event){
        Button button = new Button(name);
        button.setOnAction(event);
        return button;
    }

    public static MenuItem menuItem(String name, EventHandler<ActionEvent> event){
        MenuItem item = new MenuItem(name);
        item.setOnAction(event);
        return item;
    }

    public static HBox textBox(String title, Consumer<String> setter){
        Text t = new Text(title);
        TextField field = new TextField();
        field.textProperty().addListener((observable, oldValue, newValue) ->  {
            setter.accept(field.getText());
        });
        HBox box = new HBox(t, field);
        box.setSpacing(10);
        return box;
    }

    public static <E extends Enum<E>> Pane dropdown(String title, Consumer<E> setter, E[] values) {
        Text t = new Text(title);
        ComboBox<E> dropDown = new ComboBox<>();
        dropDown.getItems().addAll(values);
        dropDown.itemsProperty().addListener((observable, oldValue, newValue) -> setter.accept(dropDown.getValue()));
        return new HBox(t, dropDown);
    }
}
