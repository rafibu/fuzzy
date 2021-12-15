package ch.fuzzy.movie_suggester.server;

/**
 * Interface representing an Element which can be used to classify a {@link Movie}
 * @author rbu
 * TODO rbu 15.12.2021, they should probably be dynamically generated and saved in the database -> change enums to classes
 */
public interface IFilterElement {
    String getName();
}
