package ch.fuzzy.movie_suggester.server;

import java.util.Arrays;

/**
 * Screen Size (Phone, TV, Cinema)
 */
public enum Screen implements IFilterElement {
    PHONE("Phone"),
    TV("TV"),
    CINEMA("Cinema"),
    IMAX("IMAX"),
    NOT_IMPORTANT("Not Important");

    private final String name;

    Screen(String name){
        this.name = name;
    }

    @Override public String getName() {return name;}
    @Override public String toString() { return getName(); }

    /**
     * For the filter values we don't want the Not Important
     */
    public static Screen[] getFilterValues(){
        return Arrays.stream(Screen.values()).filter(v -> v != NOT_IMPORTANT).toArray(Screen[]::new);
    }
}
