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


import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;

import java.util.List;

/**
 * Factory for samplers
 * Created by Giannis Giannakopoulos on 2/17/16.
 */
public class SamplerFactory {

    public Sampler create(String type, DeploymentSpace deploymentSpace, int budget, List<OutputSpacePoint> existingSamples) {
        Sampler sampler = null;
        switch (type) {
            case "random":
                sampler = new RandomSampler(deploymentSpace, budget);
                break;
            case "lhs":
                sampler = new LatinHypercubeSampler(deploymentSpace, budget);
                break;
        }
        return sampler;
    }
}
