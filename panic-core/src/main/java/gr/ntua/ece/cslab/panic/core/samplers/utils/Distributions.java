/*
 * Copyright 2016 Giannis Giannakopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
