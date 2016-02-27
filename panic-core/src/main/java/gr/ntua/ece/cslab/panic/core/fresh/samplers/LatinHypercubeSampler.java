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

package gr.ntua.ece.cslab.panic.core.fresh.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;

import java.util.*;

/**
 * Created by Giannis Giannakopoulos on 2/26/16.
 */
public class LatinHypercubeSampler extends Sampler {

    private final HashSet<InputSpacePoint> sampledPoints;
    private HashMap<String, List<List<Double>>> values;
    private Random random;
    private int maxCombinations;
    public LatinHypercubeSampler(DeploymentSpace deploymentSpace, int budget) {
        super(deploymentSpace, budget);
        this.values = new HashMap<>();
        this.random = new Random();
        for(String dim : this.deploymentSpace.getRange().keySet()) {
            this.values.put(dim, this.partitionDimension(dim));
        }

        this.sampledPoints = new HashSet<>();

        this.maxCombinations = 1;
        for(String s : this.deploymentSpace.getRange().keySet()) {
            this.maxCombinations*=this.deploymentSpace.getRange().get(s).size();
        }
    }

    @Override
    public InputSpacePoint next() {
        InputSpacePoint point;
        double sum = this.forbiddenPoints.size()+this.pickedPoints.size();
        if(sum >= this.maxCombinations) {
            this.noMorePoints = true;
            return null;
        }
        while(true) {
            point = new InputSpacePoint();
            HashMap<String, Integer> tempIndexes = new HashMap<>();
            for(String dim : this.deploymentSpace.getRange().keySet()) {
                tempIndexes.put(dim, this.random.nextInt(this.values.get(dim).size()));
            }
            for(Map.Entry<String, Integer> kv : tempIndexes.entrySet()) {
                point.addDimension(kv.getKey(), this.pickRandom(this.values.get(kv.getKey()).get(kv.getValue())));
            }
            if(!this.forbiddenPoints.contains(point) && !this.sampledPoints.contains(point)) {
                for(Map.Entry<String, Integer> kv : tempIndexes.entrySet()) {
                    this.values.get(kv.getKey()).remove(kv.getValue());
                }
                break;
            }
        }

        this.pickedPoints.add(point);
        this.sampledPoints.add(point);
        return point;
    }

    private List<List<Double>> partitionDimension(String dimension) {
        List<Double> dimensionValues = this.deploymentSpace.getRange().get(dimension);
        int dimensionValuesSize = dimensionValues.size();
        List<List<Double>> list = new LinkedList<>();
        double elementsPerList = (1.0*dimensionValuesSize)/budget;
        Collections.sort(dimensionValues);
        double count = 0.0;
        for(int i=0;i<this.budget;i++) {
            int from = (int) Math.round(count);
            int to = (int) Math.round(count+elementsPerList);
            if(from==to) {
                if(from>0)
                    from--;
                if(to<dimensionValuesSize)
                    to++;
            }
            count+=elementsPerList;
            list.add(dimensionValues.subList(from, to));
        }
        return list;
    }

    private Double pickRandom(List<Double> list) {
        return list.get(this.random.nextInt(list.size()));
    }
}
