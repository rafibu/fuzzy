package util;


import java.util.List;
import java.util.Random;

public class MathUtil {

    public static boolean isBetween(int number, int bound1, int bound2){
        return isBetween((float)number, (float)bound1, (float) bound2);
    }

    public static boolean isBetween(float number, float bound1, float bound2){
        return (number <= bound1 && number >= bound2) || (number >= bound1 && number <= bound2);
    }

    public static boolean probability(double prob){
        assert prob <= 1;
        return prob >= Math.random();
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

    public static int random(int bound){
        return random(0, bound);
    }

    /**
     * gets Random number between lowerBound (inclusive) and upperBound (exclusive)
     */
    public static int random(int lowerBound, int upperBound) {
        Random r = new Random();
        assert lowerBound < upperBound;
        return r.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public static byte xor(byte a, byte b) {return (byte) ((int)a ^(int)b);}

    /**
     * returns xor of the two byte arrays without modifying them
     */
    public static byte[] xor(byte[] a, byte[] b){
        if(a.length !=  b.length) throw new AssertionError("byte arrays not the same size: " + a.length + ", " + b.length);
        byte[] res = new byte[a.length];
        for(int i = 0; i < a.length; i++){
            res[i] = xor(a[i], b[i]);
        }
        return res;
    }
}
