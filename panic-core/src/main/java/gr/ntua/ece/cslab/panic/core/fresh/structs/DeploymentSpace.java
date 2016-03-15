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

package gr.ntua.ece.cslab.panic.core.fresh.structs;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;

import java.util.*;

/**
 * Deployment space object, representing the deprecated "ranges" object
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DeploymentSpace {
//    private HashMap<String, List<Double>> range;
    private final Set<InputSpacePoint> points;
    private final String[] dimensionLabels;
    private final int dimensionality;

    public DeploymentSpace(Set<InputSpacePoint> points) {
        this.points = points;
        Collection<String> keys = this.points.iterator().next().getKeysAsCollection();
        this.dimensionLabels = keys.toArray(new String[keys.size()]);
        this.dimensionality = this.dimensionLabels.length;
    }

    public String[] getDimensionLabels() {
        return dimensionLabels;
    }

    public int getDimensionality() {
        return dimensionality;
    }

    public int getSize() {
        return this.points.size();
    }

    public Set<InputSpacePoint> getPoints() {
        return points;
    }

    public DeploymentSpace clone() {
        Set<InputSpacePoint> points = new HashSet<>();
        for(InputSpacePoint i : this.points)
            points.add(i.getClone());
        return new DeploymentSpace(points);
    }

    public boolean contains(InputSpacePoint point) {
        return this.points.contains(point);
    }
}
