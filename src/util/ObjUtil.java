package util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ObjUtil {

    public static <T> T assertNotNull(T element){
        return assertNotNull(element, "");
    }
    public static <T> T assertNotNull(T element, String errorString){
        if(element == null){
            throw new AssertionError(errorString);
        }
        return element;
    }

    public static <T> boolean isInList(T obj, List<T> list) { return isContained(obj, list.toArray()); }

    public static <T> boolean isContained(T obj, T... list) {
        if(list != null) {
            for (T ele : list) {
                if(obj == ele) return true;
            }
        }
        return false;
    }

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

    public static <T> void assertEquals(T a, T b){
        if(!equals(a, b)) throw new AssertionError("Not the same value: " + a + ", " + b);
    }

    public static <T> boolean equals(T a, T b){
        if(a == null){
            return b == null;
        }
        return a.equals(b);
    }
}
