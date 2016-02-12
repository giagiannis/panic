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

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements various method from the Sampler interface, used as a base class
 * for the implementation of various adaptive sampling methods.
 * @author Giannis Giannakopoulos
 */
public abstract class AbstractAdaptiveSampler extends AbstractSampler{
    
    protected List<OutputSpacePoint> outputSpacePoints;
    
    /**
     * Default constructor.
     */
    public AbstractAdaptiveSampler() {
        this.outputSpacePoints = new LinkedList<>();
    }

    public List<OutputSpacePoint> getOutputSpacePoints() {
        return outputSpacePoints;
    }

    public void setOutputSpacePoints(List<OutputSpacePoint> outputSpacePoints) {
        this.outputSpacePoints = outputSpacePoints;
    }
    
    public void addOutputSpacePoint(OutputSpacePoint outputSpacePoint) {
        if(this.outputSpacePoints!=null) {
            this.outputSpacePoints.add(outputSpacePoint);
        }
    }

}
