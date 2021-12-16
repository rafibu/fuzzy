package ch.fuzzy.movie_suggester.util;

import ch.fuzzy.movie_suggester.server.IFilterElement;
import ch.fuzzy.movie_suggester.ui.ILayout;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Combines all implementations of inputfields for the {@link com.vaadin.flow.component.orderedlayout.VerticalLayout Vertical} and {@link com.vaadin.flow.component.orderedlayout.HorizontalLayout Horizontal} Layout
 * For more Details:
 * @see <a href="https://vaadin.com/components">Components</a>
 * @author rbu
 */
public class LayoutUtil {

    public static Span addTitle(String text, ILayout layout){
        Span title = new Span(text);
        title.getElement().getStyle().set("font-weight", "bold");
        layout.add(title);
        return title;
    }

    public static Span addSpan(String text, ILayout layout){
        Span title = new Span(text);
        layout.add(title);
        return title;
    }

    public static Text addText(String text, ILayout layout){
        Text t;
        layout.add(t = new Text(text));
        return t;
    }

    public static TextField addTextfield(String title, Consumer<String> setter, ILayout layout){
        return addTextfield(title, setter, null, layout);
    }
    public static TextField addTextfield(String title, Consumer<String> setter, String value, ILayout layout){
        final TextField textfield = new TextField(title);
        if(value != null) textfield.setValue(value);
        textfield.addValueChangeListener(e -> {
            setter.accept(textfield.getValue());
            layout.fireStateChanged();
        });
        layout.add(textfield);
        return textfield;
    }

    public static <T extends IFilterElement> ComboBox<T> addCombobox(String title, Consumer<T> setter, T[] choices, ILayout layout){
        return addCombobox(title, setter, null, choices, layout);
    }
    public static <T extends IFilterElement> ComboBox<T> addCombobox(String title, Consumer<T> setter, T value, T[] choices, ILayout layout){
        final ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setItems(choices);
        if(value != null) comboBox.setValue(value);
        comboBox.setLabel(title);
        comboBox.addValueChangeListener(event -> {
            setter.accept(comboBox.getValue());
            layout.fireStateChanged();
        });
//        if(includeNull) { FIXME
//            comboBox.setNullSelectionAllowed(true);
//            comboBox.setNullSelectionItemId("");
//        }
        comboBox.setItemLabelGenerator(T::getName);
        layout.add(comboBox);
        return comboBox;
    }

    public static <T extends IFilterElement> Select<T> addSelect(String title, Consumer<T> setter, T value, T[] choices, ILayout layout){
        return addSelect(title, setter, value, choices, true, layout);
    }
    public static <T extends IFilterElement> Select<T> addSelect(String title, Consumer<T> setter, T[] choices, ILayout layout){
        return addSelect(title, setter, null, choices, true, layout);
    }
    public static <T extends IFilterElement> Select<T> addSelect(String title, Consumer<T> setter, T[] choices, boolean includeNull, ILayout layout){
        return addSelect(title, setter, null, choices, includeNull, layout);
    }
    public static <T extends IFilterElement> Select<T> addSelect(String title, Consumer<T> setter, T value, T[] choices, boolean includeNull, ILayout layout){
        final Select<T> select = new Select<>();

        select.setLabel(title);

        select.setItems(choices);
        select.setValue(value);
        select.setItemLabelGenerator(T::getName);
        select.addValueChangeListener(event -> {
            setter.accept(select.getValue());
            layout.fireStateChanged();
        });
//        if(includeNull) { FIXME:
//            select.setEmptySelectionAllowed(true);
//            select.setEmptySelectionCaption("");
//        }
        layout.add(select);
        return select;
    }

    public static Anchor addAnchor(String title, String link, ILayout layout){
        final Anchor anchor = new Anchor(link, title);
        layout.add(anchor);
        return anchor;
    }

    public static <T extends IFilterElement> RadioButtonGroup<T>  addRadioButtons(String title, Consumer<T> setter, T[] choices, ILayout layout){
        return addRadioButtons(title, setter, null, choices, false, layout);
    }
    public static <T extends IFilterElement> RadioButtonGroup<T>  addRadioButtons(String title, Consumer<T> setter, T value, T[] choices, ILayout layout){
        return addRadioButtons(title, setter, value, choices, false, layout);
    }
    public static <T extends IFilterElement> RadioButtonGroup<T>  addRadioButtons(String title, Consumer<T> setter, T[] choices, boolean vertical, ILayout layout){
        return addRadioButtons(title, setter, null, choices, vertical, layout);
    }
    public static <T extends IFilterElement> RadioButtonGroup<T>  addRadioButtons(String title, Consumer<T> setter, T value, T[] choices, boolean vertical, ILayout layout){
        final RadioButtonGroup<T> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel(title);
        radioGroup.setItems(choices);
        radioGroup.setRenderer(new TextRenderer<>(T::getName));
        if(vertical) radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.addValueChangeListener(event -> {
            setter.accept(radioGroup.getValue());
            layout.fireStateChanged();
        });
        if(value != null) radioGroup.setValue(value);
        layout.add(radioGroup);
        return radioGroup;
    }

    public static TextArea addTextArea(String title, Consumer<String> setter, String value, ILayout layout){
        return addTextArea(title, setter, value, null, layout);
    }
    public static TextArea addTextArea(String title, Consumer<String> setter, ILayout layout){
        return addTextArea(title, setter, null, null, layout);
    }
    public static TextArea addTextArea(String title, Consumer<String> setter, String value, String placeholder, ILayout layout){
        final TextArea textarea = new TextArea(title);
        if(value != null) textarea.setValue(value);
        textarea.addValueChangeListener(e -> {
            setter.accept(textarea.getValue());
            layout.fireStateChanged();
        });
        textarea.setPlaceholder(placeholder);
        layout.add(textarea);
        return textarea;
    }

    public static NumberField addNumberField(String title, Consumer<Double> setter, Double value, ILayout layout){
        return addNumberField(title, setter, value,false, null, null, null, layout);
    }
    public static NumberField addNumberField(String title, Consumer<Double> setter, ILayout layout){
        return addNumberField(title, setter, false, null, null, null, layout);
    }
    public static NumberField addNumberField(String title, Consumer<Double> setter, boolean hasControls, ILayout layout){
        return addNumberField(title, setter, hasControls, null, null, null, layout);
    }
    public static NumberField addNumberField(String title, Consumer<Double> setter, boolean hasControls, Double max, ILayout layout){
        return addNumberField(title, setter, hasControls, null, max, null, layout);
    }
    public static NumberField addNumberField(String title, Consumer<Double> setter, boolean hasControls, Double min, Double max, Double step, ILayout layout) {
        return addNumberField(title, setter, null, hasControls, min, max, step, layout);
    }
    public static NumberField addNumberField(String title, Consumer<Double> setter, Double value, boolean hasControls, Double min, Double max, Double step, ILayout layout){
        final NumberField numberField = new NumberField(title);
        setter.accept(value); //setter should have same state as NumberField
        numberField.setHasControls(hasControls);
        if(value != null) numberField.setValue(value);
        if(min != null) numberField.setMin(min);
        if(max != null) numberField.setMax(max);
        if(step != null) numberField.setStep(step);
        numberField.addValueChangeListener(e -> {
            setter.accept(numberField.getValue());
            layout.fireStateChanged();
        });
        layout.add(numberField);
        return numberField;
    }

    public static IntegerField addIntegerField(String title, Consumer<Integer> setter, ILayout layout){
        return addIntegerField(title, setter, false, null, null, null, layout);
    }
    public static IntegerField addIntegerField(String title, Consumer<Integer> setter, Integer value, ILayout layout){
        return addIntegerField(title, setter, value, false, null, null, null, layout);
    }
    public static IntegerField addIntegerField(String title, Consumer<Integer> setter, boolean hasControls, ILayout layout){
        return addIntegerField(title, setter, hasControls, null, null, null, layout);
    }
    public static IntegerField addIntegerField(String title, Consumer<Integer> setter, boolean hasControls, Integer max, ILayout layout){
        return addIntegerField(title, setter, hasControls, null, max, null, layout);
    }
    public static IntegerField addIntegerField(String title, Consumer<Integer> setter, boolean hasControls, Integer min, Integer max, Integer step, ILayout layout) {
        return addIntegerField(title, setter, null, hasControls, min, max, step, layout);
    }
    public static IntegerField addIntegerField(String title, Consumer<Integer> setter, Integer value, boolean hasControls, Integer min, Integer max, Integer step, ILayout layout){
        final IntegerField numberField = new IntegerField(title);
        if(value != null) numberField.setValue(value);
        setter.accept(value); //setter should have the same state as the IntegerField
        numberField.setHasControls(hasControls);
        if(min != null) numberField.setMin(min);
        if(max != null) numberField.setMax(max);
        if(step != null) numberField.setStep(step);
        numberField.addValueChangeListener(e -> {
            setter.accept(numberField.getValue());
            layout.fireStateChanged();
        });
        layout.add(numberField);
        return numberField;
    }

    public static Checkbox addCheckbox(String title, Consumer<Boolean> setter, ILayout layout){
        return addCheckbox(title, setter, false, layout);
    }
    public static Checkbox addCheckbox(String title, Consumer<Boolean> setter, Boolean value, ILayout layout){
        final Checkbox checkbox = new Checkbox();
        if(value != null) checkbox.setValue(value);
        checkbox.setLabel(title);
        layout.add(checkbox);
        checkbox.addValueChangeListener(event -> {
            setter.accept(checkbox.getValue());
            layout.fireStateChanged();
        });
        return checkbox;
    }

    public static <T extends IFilterElement> CheckboxGroup<T> addCheckboxGroup(String title, Consumer<Set<T>> setter, T[] choices, ILayout layout){
        return addCheckboxGroup(title, setter, choices, false, layout);
    }
    public static <T extends IFilterElement> CheckboxGroup<T> addCheckboxGroup(String title, Consumer<Set<T>> setter, Set<T> value, T[] choices, ILayout layout){
        return addCheckboxGroup(title, setter, value, choices, false, layout);
    }
    public static <T extends IFilterElement> CheckboxGroup<T>  addCheckboxGroup(String title, Consumer<Set<T>> setter, T[] choices, boolean vertical, ILayout layout) {
        return addCheckboxGroup(title, setter, null, choices, vertical, layout);
    }
    public static <T extends IFilterElement> CheckboxGroup<T>  addCheckboxGroup(String title, Consumer<Set<T>> setter, Set<T> value, T[] choices, boolean vertical, ILayout layout){
        final CheckboxGroup<T> radioGroup = new CheckboxGroup<>();
        radioGroup.setLabel(title);
        radioGroup.setItems(choices);
//        radioGroup.setRenderer(new TextRenderer<>(T::getName)); FIXME: rbu 28.10.2021, add renderer if possible
        if(value != null){
            radioGroup.setValue(value);
        }
        if(vertical) radioGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        radioGroup.addValueChangeListener(event -> {
            setter.accept(radioGroup.getValue());
            layout.fireStateChanged();
        });
        layout.add(radioGroup);
        return radioGroup;
    }

    public static <T> MultiSelectListBox<T> addMultiSelect(Consumer<Set<T>> setter, T[] choices, ILayout layout){
        return addMultiSelect(setter, null, choices, layout);
    }
    public static <T> MultiSelectListBox<T> addMultiSelect(Consumer<Set<T>> setter, Set<T> value, T[] choices, ILayout layout){
        final MultiSelectListBox<T> multiSelect = new MultiSelectListBox<>();
        multiSelect.setItems(choices);
        if(value != null){
            multiSelect.setValue(value);
        }
        multiSelect.addValueChangeListener(event -> {
            setter.accept(multiSelect.getSelectedItems());
            layout.fireStateChanged();
        });
        layout.add(multiSelect);
        return multiSelect;
    }

    public static <T> MultiselectComboBox<T> addMultiSelectComboBox(Consumer<Set<T>> setter, T[] choices, ILayout layout){
        return addMultiSelectComboBox(setter, null, choices, layout);
    }
    public static <T> MultiselectComboBox<T> addMultiSelectComboBox(Consumer<Set<T>> setter, Set<T> value, T[] choices, ILayout layout){
        final MultiselectComboBox<T> multiSelect = new MultiselectComboBox<>();
        multiSelect.setItems(choices);
        if(value != null){
            multiSelect.setValue(value);
        }
        multiSelect.addValueChangeListener(event -> {
            setter.accept(multiSelect.getSelectedItems());
            layout.fireStateChanged();
        });
        layout.add(multiSelect);
        return multiSelect;
    }

    public static Image addImage(String dataSource, ILayout layout){
        return addImage(dataSource, dataSource.contains("/") ? dataSource.substring(dataSource.lastIndexOf("/") + 1): dataSource, layout);
    }
    public static Image addImage(String dataSource, String resourceName, ILayout layout){
        byte[] imageBytes;
        try {
            imageBytes = new FileInputStream(dataSource).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StreamResource resource = new StreamResource(resourceName, () -> new ByteArrayInputStream(imageBytes));
        Image image = new Image(resource, "dummy image");
        layout.add(image);
        return image;
    }

    public static Slider addSlider(Consumer<Integer> setter, Integer value, ILayout layout){
        return addSlider("", setter, value, 0, 100, layout);
    }
    public static Slider addSlider(Consumer<Integer> setter, ILayout layout){
        return addSlider("", setter, 0, 0, 100, layout);
    }
    public static Slider addSlider(String title, Consumer<Integer> setter, Integer value, Integer min, Integer max, ILayout layout){
        Slider slider = new Slider(value);
        slider.setValue(value);
        slider.setLabel(title);
        slider.setMin(min);
        slider.setMax(max);
        slider.addValueChangeListener(event -> {
            setter.accept(slider.getValue());
            layout.fireStateChanged();
        });
        layout.add(slider);
        return slider;
    }

    /**
     * Used to get a Simple Slider for Number
     */
    @Tag("paper-slider")
    @NpmPackage(value = "@polymer/paper-slider", version = "3.0.1")
    @JsModule("@polymer/paper-slider/paper-slider.js")
    public static class Slider extends AbstractSinglePropertyField<Slider, Integer> {
        public Slider(Integer value) {
            super("value", value, false);
        }

        public void setMin(Integer min){ if(min!= null) getElement().setAttribute("min", min.toString());}
        public void setMax(Integer max){ if(max!= null) getElement().setAttribute("max", max.toString());}
        public void setLabel(String label){ if(label!= null) getElement().setAttribute("label", label);}
    }

}
