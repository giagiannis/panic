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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Random sampler class
 * Created by Giannis Giannakopoulos on 2/26/16.
 */
public class RandomSampler extends Sampler {
    private Random random;
    private List<Integer> leftIndices;
    private List<String> dimensionOrdering;

    public RandomSampler(DeploymentSpace deploymentSpace, int budget) {
        super(deploymentSpace, budget);
        this.random = new Random();
        this.leftIndices = new LinkedList<>();

        this.dimensionOrdering = new LinkedList<>(this.deploymentSpace.getRange().keySet());
        int mul = 1;
        for(String s:dimensionOrdering) {
            mul*=this.deploymentSpace.getRange().get(s).size();
        }
        for(int i=0;i<mul;i++) {
            this.leftIndices.add(i);
        }
    }

    @Override
    public InputSpacePoint next() {
        InputSpacePoint point;
        while(true) {
            if(this.leftIndices.size()==0) {
                this.noMorePoints = true;
                return null;
            }
            int listIndex=this.random.nextInt(this.leftIndices.size());
            point = this.translate(this.leftIndices.remove(listIndex));
            if(!this.forbiddenPoints.contains(point)) {
                break;
            }
        }
        this.pickedPoints.add(point);
        return point;
    }

    InputSpacePoint translate(int index) {
        int identifier = index;
        InputSpacePoint point = new InputSpacePoint();
        for(String key: this.dimensionOrdering) {
            List<Double> dimValues = this.deploymentSpace.getRange().get(key);
            int mod = identifier % dimValues.size();
            identifier /= dimValues.size();
            point.addDimension(key, dimValues.get(mod));
        }
        return point;
    }
}
