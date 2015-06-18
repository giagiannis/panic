/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.samplers.utils;

import java.util.Random;

/**
 * Util class targeting to provide methods for random access to lists and other 
 * structs.
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
