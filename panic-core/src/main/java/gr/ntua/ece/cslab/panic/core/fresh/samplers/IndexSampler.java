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
 * This class provides methods necessary to map the InputSpacePoint objects into one dimension (indexed by an int).
 * Created by Giannis Giannakopoulos on 3/2/16.
 */
public abstract class IndexSampler extends Sampler {
    protected String[] dimensionsOrder;
    protected final Random random;
    protected List<Integer> leftIndices;

    private List<InputSpacePoint> orderedPoints;


    public IndexSampler(DeploymentSpace deploymentSpace, int budget, String[] dimensionsOrder) {
        super(deploymentSpace, budget);

        this.dimensionsOrder = dimensionsOrder;
        this.random = new Random();
        this.leftIndices = new LinkedList<>();

        this.orderedPoints = new ArrayList<>(this.deploymentSpace.getPoints());
        this.orderedPoints.sort((p1, p2) -> {
            for(String s : dimensionsOrder) {
                if(p1.getValue(s)< p2.getValue(s))
                    return -1;
                else if(p1.getValue(s) > p2.getValue(s))
                    return 1;
            }
            return 0;
        });
        for(int i=0;i<this.orderedPoints.size();i++)
            this.leftIndices.add(i);
    }

    protected InputSpacePoint translate(int index) {
        return this.orderedPoints.get(index);
    }
}
