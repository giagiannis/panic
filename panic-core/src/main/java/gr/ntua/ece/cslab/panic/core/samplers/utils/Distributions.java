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
    public static int getUniformIndex(int fromIndex, int toIndex) {
        return random.nextInt(toIndex-fromIndex)+fromIndex;
    }
    
    
    public static int getGaussIndex(int fromIndex, int toIndex) {
    	double index=(random.nextGaussian()+3.0)/6.0;
    	return (int)Math.round(index*(toIndex-fromIndex)+fromIndex);
    	
    }
}
