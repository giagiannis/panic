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
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Giannis Giannakopoulos
 */
public abstract class AbstractStaticSampler implements Sampler {

    protected HashMap<String, List<Double>> ranges;
    protected Double samplingRate;
    protected int pointsPicked;
    protected int maxChoices;
    
    public AbstractStaticSampler() {
        this.ranges = new HashMap<>();
        this.samplingRate = Double.MAX_VALUE;
        this.pointsPicked = 0;
    }

    @Override
    public void setDimensionsWithRanges(HashMap<String, List<Double>> ranges) {
        this.ranges =  ranges;
    }

    @Override
    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    @Override
    public boolean hasMore() {
        return this.pointsPicked<this.maxChoices*this.samplingRate;
    }

    @Override
    public InputSpacePoint next() {
        this.pointsPicked++;
        return null;
    }

    @Override
    public void configureSampler() {
        this.maxChoices = 1;
        for(String s:this.ranges.keySet())
            this.maxChoices *= this.ranges.get(s).size();
    }
    
    /**
     * Method which maps every multidimensional point to a single dimension. This
     * dimension is references by an Integer id. The objective of the function is
     * to convert ids to InputSpacePoints.
     * @param id
     * @return 
     */
    protected InputSpacePoint getPointById(int  id) {
        int identifier = id;
        InputSpacePoint point = new InputSpacePoint();
        for(String s : this.ranges.keySet()) {
            int index = identifier%this.ranges.get(s).size();
            identifier /= this.ranges.get(s).size();
            point.addDimension(s, this.ranges.get(s).get(index));
        }
        return point;
    }    

}
