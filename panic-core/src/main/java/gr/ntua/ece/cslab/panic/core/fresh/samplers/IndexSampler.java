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

import java.util.List;

/**
 * This class provides methods necessary to map the InputSpacePoint objects into one dimension (indexed by an int).
 * Created by Giannis Giannakopoulos on 3/2/16.
 */
public abstract class IndexSampler extends Sampler {
    protected String[] dimensionsOrder;
    public IndexSampler(DeploymentSpace deploymentSpace, int budget, String[] dimensionsOrder) {
        super(deploymentSpace, budget);
        this.dimensionsOrder = dimensionsOrder;
    }

    protected InputSpacePoint translate(int index) {
        int identifier = index;
        InputSpacePoint point = new InputSpacePoint();
//        for(String key: this.dimensionsOrder) {
        for(int i=this.dimensionsOrder.length-1;i>=0;i--) {
            String key = this.dimensionsOrder[i];
            List<Double> dimValues = this.deploymentSpace.getRange().get(key);
            int mod = identifier % dimValues.size();
            identifier /= dimValues.size();
            point.addDimension(key, dimValues.get(mod));
        }
        return point;
    }
}
