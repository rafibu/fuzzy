package ch.fuzzy.movie_suggester.util;


import java.util.List;
import java.util.Random;
import java.util.Set;

public class MathUtil {

    public static boolean isBetween(double number, double bound1, double bound2){
        return isBetween((float)number, (float)bound1, (float) bound2);
    }

    public static boolean isBetween(float number, float bound1, float bound2){
        return (number <= bound1 && number >= bound2) || (number >= bound1 && number <= bound2);
    }

    public static boolean probability(double prob){
        assert prob <= 1;
        return prob >= Math.random();
    }

    public static <T> T getOneAtRandom(Set<T> objects){
        return (T)getOneAtRandom(objects.toArray());
    }
    public static <T> T getOneAtRandom(T... objects){
        Random random = new Random();
        if(objects.length > 0) {
            return objects[random.nextInt(objects.length)];
        } else {
            return null;
        }
    }

    public static <T> T getOneAtRandom(List<T> objects){
        Random random = new Random();
        if(objects.size() > 0) {
            return objects.get(random.nextInt(objects.size()));
        } else {
            return null;
        }
    }

    public static double sq(double i) {
        return Math.pow(i, 2);
    }

    public static double assertPositive(double number) {
        assert number >= 0;
        return number;
    }

    public static double euclidianDistance(double point1, double point2) {
        return Math.sqrt(sq(point1)  + sq(point2));
    }
}
