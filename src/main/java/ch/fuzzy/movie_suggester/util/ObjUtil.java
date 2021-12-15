package ch.fuzzy.movie_suggester.util;

import ch.fuzzy.movie_suggester.server.IFilterElement;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Utility class for Objects
 *  @author rbu
 */
public class ObjUtil {

    /**
     * asserts that an element isn't null and returns it
     */
    public static <T> T assertNotNull(T element){
        return assertNotNull(element, "");
    }
    /**
     * asserts that an element isn't null and returns it or else throws an AssertionError with the given errorString
     */
    public static <T> T assertNotNull(T element, String errorString){
        if(element == null){
            throw new AssertionError(errorString);
        }
        return element;
    }

    /**
     * returns true if the object is in the list at least once
     */
    public static <T> boolean isInList(T obj, List<T> list) { return isContained(obj, list.toArray()); }

    /**
     * returns true if the object is in the array at least once
     */
    @SafeVarargs
    public static <T> boolean isContained(T obj, T... list) {
        if(list != null) {
            for (T ele : list) {
                if(obj == ele) return true;
            }
        }
        return false;
    }

    /**
     * asserts that the collection only has one element and returns this element
     * also allows this one element or the collection to be null
     */
    public static <T> T assertUnique(Iterable<T> collection) {
        T res;
        try {
            Iterator<T> it = collection.iterator();
            res = it.next();
            if(it.hasNext()){
                throw new AssertionError("not Unique");
            }
        } catch (NullPointerException|NoSuchElementException es){
            return null;
        }
        return res;
    }

    public static <T> boolean equals(T a, T b){
        if(a == null){
            return b == null;
        }
        return a.equals(b);
    }

    /**
     * Checks if two arrays have the same elements
     * !!! currently not working if an element is contained more than once !!!
     */
    //FIXME: rbu 14.11.2021, for Elements where multiple values are present they might slip through
    public static <T> boolean sameElements(T[] array1, T[] array2) {
        if(array1.length != array2.length){ return false;}
        for(T element: array1){
            if(!isContained(element, array2)){ return false; }
        }
        return true;
    }

    /**
     * Concat two lists but removes any duplicate elements
     */
    public static <T> List<T> concatDistinct(List<T> list1, List<T> list2) {
        return concat(list1, list2).stream().distinct().collect(Collectors.toList());
    }
    /**
     * Concat two lists
     */
    public static <T> List<T> concat(List<T> list1, List<T> list2) {
        list1.addAll(list2);
        return list1;
    }

    /**
     * adds the element if it isn't already in the list
     */
    public static <T> void addDistinct(List<T> list, T toAdd) {
        if(!list.contains(toAdd)){
            list.add(toAdd);
        }
    }

    /**
     * returns Object::toString or the defaultValue if the Object is null
     */
    public static String toString(Object o, String defaultValue){ return toString(o) != null ? toString(o) : defaultValue; }

    /**
     * returns Object::toString or null if the object is null
     */
    public static String toString(Object o){
        return o != null ? o instanceof IFilterElement ? ((IFilterElement) o).getName() : o.toString() : null;
    }

    /**
     * Concat all elements of the collection for a nice human-readable String
     */
    public static <T> String toString(Collection<T> collection) {
        if(collection == null) return "None";
        if(collection.stream().anyMatch(t -> t instanceof IFilterElement)){
            StringBuilder sb = new StringBuilder();
            for(T t: collection){
                sb.append(((IFilterElement)t).getName()).append(", ");
            }
            return sb.length() > 0 ? sb.substring(0, sb.length()-2) : null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (T t : collection) {
                sb.append(t.toString()).append(", ");
            }
            return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : null;
        }
    }

    /**
     * Asserts that a collection only has one element and that element isn't null and then returns it
     */
    public static <T> T assertUniqueNotNull(Collection<T> collection){
        return assertNotNull(assertUnique(collection));
    }
}
