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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Sampler implementing the Latin Hypercube Sampling methodology.
 * @author Giannis Giannakopoulos
 */
public class LatinHypercubeSampler  extends AbstractSampler {

    private Map<String, List<Integer>> hyperCubeIndices;
    private Random random;
    private Integer slots;
    
    @Override
    public void configureSampler() {
        super.configureSampler();
        this.hyperCubeIndices = this.createHyperCubeIndices();
        this.random = new Random();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sample = new InputSpacePoint();
        for(String s:this.hyperCubeIndices.keySet()) {
            List<Integer> indices = this.hyperCubeIndices.get(s);
            int index = indices.remove(this.random.nextInt(indices.size()));
            double value = this.pickPointUniformly(this.translateIndex(s, index));
            sample.addDimension(s, value);
        }
        return sample;
    }
    
    private Map<String, List<Integer>> createHyperCubeIndices() {
        this.slots = (int) Math.floor(this.pointsToPick);
        HashMap<String, List<Integer>> result = new HashMap<>();
        for(String s:this.ranges.keySet()) {
            result.put(s, new ArrayList<Integer>(slots));
            for(int i=0;i<slots;i++)
                result.get(s).add(i);
        }
        return result;
    }
    
    private List<Double> translateIndex(String dimensionKey, Integer index) {
        List<Double> wholeList = this.ranges.get(dimensionKey);
        int startPoint = (int) Math.floor(((double)index/(double)slots)*wholeList.size()),
            endPoint = (int) Math.floor(((double)(index+1.0)/(double)slots)*wholeList.size());
        if(startPoint==endPoint)
            endPoint++;
        return wholeList.subList(startPoint, endPoint);
    }
    
    private Double pickPointUniformly(List<Double> list) {
        return list.get(this.random.nextInt(list.size()));
    }
    
}
