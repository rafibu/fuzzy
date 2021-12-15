package ch.fuzzy.movie_suggester.ui;

/**
 * Interface for Base Layouts
 */
public interface ILayout {

    /**
     * Should define which elements have to be re-rendered if some value changes
     */
    void fireStateChanged();

    void add(com.vaadin.flow.component.Component... components);
}
