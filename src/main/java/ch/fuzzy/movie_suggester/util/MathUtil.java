package ch.fuzzy.movie_suggester.util;


import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Utility class for simple recurring mathematical functions
 */
public class MathUtil {

    /**
     * checks if a number is between two bounds (inclusive)
     */
    public static boolean isBetween(double number, double bound1, double bound2){
        return isBetween((float)number, (float)bound1, (float) bound2);
    }

    /**
     * checks if a number is between two bounds (inclusive)
     */
    public static boolean isBetween(float number, float bound1, float bound2){
        return (number <= bound1 && number >= bound2) || (number >= bound1 && number <= bound2);
    }

    /**
     * can be used for simple non-deterministic functions
     * @return true with probability {@param prob}
     */
    public static boolean probability(double prob){
        assert prob <= 1;
        return prob >= Math.random();
    }

    /**
     * Chooses a random element from the given set and returns it or null if the Set is empty
     */
    public static <T> T getOneAtRandom(Set<T> objects){
        return (T)getOneAtRandom(objects.toArray());
    }
    /**
     * Chooses a random element from the given set and returns it or null if the Set is empty
     */
    public static <T> T getOneAtRandom(T... objects){
        Random random = new Random();
        if(objects.length > 0) {
            return objects[random.nextInt(objects.length)];
        } else {
            return null;
        }
    }

    /**
     * Chooses a Random element from the given list and returns it or null if the Set is empty
     */
    public static <T> T getOneAtRandom(List<T> objects){
        Random random = new Random();
        if(objects.size() > 0) {
            return objects.get(random.nextInt(objects.size()));
        } else {
            return null;
        }
    }

    /**
     * convenience function to square a number
     */
    public static double sq(double i) {
        return Math.pow(i, 2);
    }

    /**
     * convenience function to square a number
     */
    public static int sq(int i) {
        return (int) sq((double) i);
    }

    /**
     * throws an AssertionError when the number is strictly smaller then 0
     */
    public static double assertPositive(double number) {
        if(number < 0){ throw new AssertionError("number not positive: " + number); }
        return number;
    }

    /**
     * returns the euclidean Distance between two numbers
     */
    public static double euclideanDistance(double point1, double point2) {
        return Math.sqrt(sq(point1)  + sq(point2));
    }
}
