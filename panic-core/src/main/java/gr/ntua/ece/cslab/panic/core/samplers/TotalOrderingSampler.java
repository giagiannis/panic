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
import gr.ntua.ece.cslab.panic.core.samplers.utils.Distributions;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This samplers utilizes a space-covering sampling technique to find
 * relationships between the dimensions of the input space and then orders the
 * points by importance.
 *
 * @author Giannis Giannakopoulos
 */
public class TotalOrderingSampler extends AbstractAdaptiveSampler {
    private String[] dimensionOrdering;
    private List<InputSpacePoint> futureSamples;

    public TotalOrderingSampler() {
        super();
        Distributions.getGaussIndex(0,0);
    }
    
    public void setDimensionOrdering(String[] dimensions) {
        this.dimensionOrdering = dimensions;
    }

    public String[] getDimensionOrdering() {
        return dimensionOrdering;
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.futureSamples = new LinkedList<>();
        this.analyzeData();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        return this.futureSamples.remove(0);
    }

    private void analyzeData() {
        int numberOfSamplesToPick = (int) Math.ceil(this.samplingRate * this.maxChoices);
        double pivot = ((double) this.maxChoices) / numberOfSamplesToPick;
        for (double i = 0.0; i <= this.maxChoices - 1; i += pivot) {
            int fromIndex = (int) Math.round(i), toIndex = (int) Math.round(i + pivot);
//            InputSpacePoint point = this.getPointById(Distributions.getUniformIndex(fromIndex, toIndex));
            InputSpacePoint point = this.getPointById(Distributions.getGaussIndex(fromIndex, toIndex));
            this.futureSamples.add(point);
        }
        Collections.shuffle(this.futureSamples);
    }

    /**
     * The points are reordered according to the dimensions ordering returned by
     * the principal components.
     *
     * @param id
     * @return
     */
    @Override
    protected InputSpacePoint getPointById(int id) {
        InputSpacePoint point = new InputSpacePoint();
        for (int index = this.dimensionOrdering.length - 1; index >= 0; index--) {
            String key = this.dimensionOrdering[index];
            int modulo = id % this.ranges.get(key).size();
            modulo=(modulo<0?0:modulo);
            point.addDimension(key, this.ranges.get(key).get(modulo));
            id = id / this.ranges.get(key).size();
        }
        return point;
    }
}
