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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Deployment space object, representing the deprecated "ranges" object
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DeploymentSpace {

    private HashMap<String, List<Double>> range;

    public DeploymentSpace() {
        this.range = new HashMap<>();
    }

    public void setRange(HashMap<String, List<Double>> range) {
        this.range = range;
    }

    public HashMap<String, List<Double>> getRange() {
        return range;
    }

    public DeploymentSpace clone() {
        HashMap<String, List<Double>> newRegion = new HashMap<>();
        for(String s: this.range.keySet()) {
            newRegion.put(s, new LinkedList<>());
            for(Double v:this.range.get(s)) {
                newRegion.get(s).add(v);
            }
        }

        DeploymentSpace newD = new DeploymentSpace();
        newD.setRange(newRegion);
        return newD;
    }

    @Override
    public String toString() {
        return this.range.toString();
    }
}
