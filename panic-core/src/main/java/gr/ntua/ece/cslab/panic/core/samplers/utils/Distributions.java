package gr.ntua.ece.cslab.panic.core.samplers.utils;

import java.util.Random;

/**
 * Utility class targeting to provide methods for random access to lists and other 
 * structures.
 * @author Giannis Giannakopoulos
 */
public class Distributions {
    
    public static Random random = new Random();


    /**
     * Returns a random index with a Uniform distribution.
     * @param fromIndex
     * @param toIndex
     * @return 
     */
    public static int getRandomIndex(int fromIndex, int toIndex) {
        return random.nextInt(toIndex-fromIndex)+fromIndex;
    }
}
