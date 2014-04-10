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

package gr.ntua.ece.cslab.panic.server.samplers;

import gr.ntua.ece.cslab.panic.server.containers.beans.InputSpacePoint;
import java.util.Map;
import java.util.Set;

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
    public void setDimensionsWithRanges(Map<String, Set<Double>> ranges);
}
