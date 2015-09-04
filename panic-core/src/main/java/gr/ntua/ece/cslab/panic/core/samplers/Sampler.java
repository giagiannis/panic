/*
 * Copyright 2014 Giannis Giannakopoulos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import java.util.HashMap;
import java.util.List;

/**
 * Describes the API with which the objects will contact with the Samplers.
 * @author Giannis Giannakopoulos
 */
public interface Sampler {
    
    /**
     * This method is used to return the next point of the sampling space
     * @return 
     */
    public InputSpacePoint next();
    
    /**
     * Informs the sampler about the applicable values per dimension.
     * @param ranges 
     */
    public void setDimensionsWithRanges(HashMap<String, List<Double>> ranges);
    
    /**
     * Method used to set the sampling rate which will be used for sampling.
     * @param samplingRate a number from (0.0 - 1.0]
     */
    public void setSamplingRate(double samplingRate);
    
    /**
     * Dual method to the setSamplingRate method, as it sets the number of points to
     * be returned by the sampler. Call this after the configureSampler method.
     * @param numberOfPoints 
     */
    public void setPointsToPick(Integer numberOfPoints);
    
    /**
     * Method used to configure the sampler. This method must be executed last,
     * after all the initialization steps and before start picking the first
     * samples.
     */
    public void configureSampler();
    
    
    /**
     * Method used to inform whether more points can be chosen or not.
     * @return 
     */
    public boolean hasMore();
    
    
    public HashMap<String, String> getConfiguration();

    public void setConfiguration(HashMap<String, String> configuration);
    
    public void setConfiguration(String configuration);

}
