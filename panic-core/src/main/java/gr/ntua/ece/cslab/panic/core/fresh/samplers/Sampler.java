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
 * Sampler abstract class.
 * Created by Giannis Giannakopoulos on 2/26/16.
 */
public abstract class Sampler {

    protected final DeploymentSpace deploymentSpace;
    protected final int budget;
    protected final List<InputSpacePoint> pickedPoints;
    protected Set<InputSpacePoint> forbiddenPoints;
    protected boolean noMorePoints = false;

    public Sampler(DeploymentSpace deploymentSpace, int budget) {
        this.deploymentSpace = deploymentSpace;
        this.budget = budget;
        this.pickedPoints = new LinkedList<>();
        this.forbiddenPoints = new HashSet<>();
    }

    public void setForbiddenPoints(List<InputSpacePoint> forbiddenPoints) {
        this.forbiddenPoints = new HashSet<>();
        this.forbiddenPoints.addAll(forbiddenPoints.stream().filter(p -> this.deploymentSpace.contains(p)).collect(Collectors.toList()));
    }

    public abstract InputSpacePoint next();
    public boolean hasMore() {
        return (this.pickedPoints.size()<this.budget) && (!noMorePoints);
    }
}
