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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Giannis Giannakopoulos on 3/7/16.
 */
public class SystematicSampler extends IndexSampler {
    private double pivot;
    private double currentIndex;
    public SystematicSampler(DeploymentSpace deploymentSpace, int budget, String[] dimensionsOrder) {
        super(deploymentSpace, budget, dimensionsOrder);
        pivot = (this.leftIndices.size()*1.0)/(this.budget);
        pivot = (pivot<1.0?1.0:pivot);
        currentIndex = this.random.nextInt((int) pivot);
    }

    @Override
    public void setForbiddenPoints(List<InputSpacePoint> forbiddenPoints) {
        super.setForbiddenPoints(forbiddenPoints);

        Set<Integer> indicesToRemove = new HashSet<>();
        for(int i=0;i<this.leftIndices.size();i++) {
            InputSpacePoint point = this.translate(this.leftIndices.get(i));
            if(this.forbiddenPoints.contains(point)) {
                indicesToRemove.add(this.leftIndices.get(i));
            }
        }

        this.leftIndices = this.leftIndices.stream().filter(index -> !indicesToRemove.contains(index)).collect(Collectors.toList());

        pivot = (this.leftIndices.size()*1.0)/(this.budget);
        pivot = (pivot<1.0?1.0:pivot);
        currentIndex = this.random.nextInt((int) Math.ceil(pivot));
    }

    @Override
    public InputSpacePoint next() {
        int index  = (int) Math.round(this.currentIndex);
        if(index>=this.leftIndices.size()) {
            this.noMorePoints = true;
            return null;
        }
//        System.out.println("Chosen index: "+this.currentIndex);
        InputSpacePoint point = this.translate(this.leftIndices.get(index));
        this.currentIndex+=this.pivot;
        this.pickedPoints.add(point);
        return point;
    }
}
