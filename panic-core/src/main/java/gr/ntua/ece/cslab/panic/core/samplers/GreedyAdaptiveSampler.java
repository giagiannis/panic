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
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * GreedyAdaptiveSampler is a greedy algorithm executing adaptive sampling.
 * <br/>
 * The algorithm works in two phases:
 * <br/>
 * <ol>
 * <li> returns all the points where each dimension takes its lowest or maximum
 * values.</li>
 * <li>For each dimension, the median of the two points with the max difference
 * is picked and a new point is formed for all the dimensions.</li>
 * </ol>
 *
 * In the second case, if the specified point exists a random point is returned.
 *
 * <br/>
 * The values used for feeding the sampler are the values from the deployment,
 * and not the values as estimated from the model itself.
 *
 * @author Giannis Giannakopoulos
 */
public class GreedyAdaptiveSampler extends AbstractAdaptiveSampler {

    private Set<InputSpacePoint> picked;
    private RandomSampler randomSampler;
    
    public GreedyAdaptiveSampler() {
        super();
        this.picked = new HashSet<>();
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        randomSampler = new RandomSampler();
        randomSampler.setDimensionsWithRanges(this.ranges);
        randomSampler.setSamplingRate(this.samplingRate);
        randomSampler.configureSampler();
    }

    
    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sample;
        if (this.pointsPicked <= Math.pow(2, this.ranges.size())) {
            sample = this.getBorderPoint();
        } else {
            sample = this.getNextPoint();
        }
        this.picked.add(sample);
        return sample;
    }

    // method used to estimate the next border point
    protected InputSpacePoint getBorderPoint() {
        int bitsPadding = this.ranges.size();
        int pointIdex = this.pointsPicked - 1; // super.next() is already called
        Integer unpadded = new Integer(Integer.toBinaryString(pointIdex));
        char[] bitmask = String.format("%0" + bitsPadding + "d", unpadded).toCharArray();
        List<String> keys = new LinkedList<>(this.ranges.keySet());
        Collections.sort(keys);

        InputSpacePoint result = new InputSpacePoint();
        int index = 0;
        for (String key : keys) {
            if (bitmask[index++] == '0') {
                result.addDimension(key, this.ranges.get(key).get(0));
            } else {
                result.addDimension(key, this.ranges.get(key).get(this.ranges.get(key).size() - 1));
            }
        }
        return result;
    }

    // method used in the second step of the algorithm, to fetch 
    protected InputSpacePoint getNextPoint() {
        double maxDifference = 0.0;
        OutputSpacePoint a = null, b = null;
        for(OutputSpacePoint x1 : this.outputSpacePoints) {
            for(OutputSpacePoint x2 : this.outputSpacePoints) {
                InputSpacePoint median = this.getMedianPoint(x1.getInputSpacePoint(), x2.getInputSpacePoint());
                if(!this.picked.contains(median)) {
                    double current = Math.abs(x1.getValue() - x2.getValue());
                    if(current > maxDifference) {
                        a = x1;
                        b = x2;
                        maxDifference  = current;
                    }
                }
            }
        }
        if(a==null || b==null)
            return this.getRandomPoint();
        return this.getMedianPoint(a.getInputSpacePoint(), b.getInputSpacePoint());
    }
    
    protected InputSpacePoint getRandomPoint() {
        InputSpacePoint next = randomSampler.next();
        while(this.picked.contains(next))
            next = randomSampler.next();
        return next;
    }
    
    
    protected InputSpacePoint getMedianPoint(InputSpacePoint a, InputSpacePoint b) {
        InputSpacePoint result = new InputSpacePoint();
        for(String s : a.getKeysAsCollection()) {
            result.addDimension(s, this.getClosestAllowedValue(s, (a.getValue(s)+b.getValue(s)/2.0)));
        }
        return result;
    }
    
    protected double getClosestAllowedValue(String key, double value) {
        double candidate = this.ranges.get(key).get(0);
        for (double d : this.ranges.get(key)) {
            if (Math.abs(d - value) < Math.abs(candidate - value)) {
                candidate = d;
            }
        }
        return candidate;
    }
}