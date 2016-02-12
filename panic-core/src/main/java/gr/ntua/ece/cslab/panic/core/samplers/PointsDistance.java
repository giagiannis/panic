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

package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class PointsDistance {
    
    /**
     * Returns the eucleidian distance between two input space points.
     * @param a
     * @param b
     * @return 
     */
    public static double eucleidian(InputSpacePoint a, InputSpacePoint b) {
        double sum =0.0;
        for(String s : a.getKeysAsCollection()) {
            sum += Math.pow(a.getValue(s)-b.getValue(s), 2);
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Returns the manhattan distance between two different points.
     * @param a
     * @param b
     * @return 
     */
    public static double mahnattan(InputSpacePoint a , InputSpacePoint b) {
        double sum = 0.0;
        for(String s : a.getKeysAsCollection()) {
            sum += Math.abs(a.getValue(s) - b.getValue(s));
        }
        return sum;
    }
    
}
