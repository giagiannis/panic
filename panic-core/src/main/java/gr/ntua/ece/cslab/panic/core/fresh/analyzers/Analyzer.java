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

package gr.ntua.ece.cslab.panic.core.fresh.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.HashMap;
import java.util.List;

/**
 * Analyzer class dictates the analyzer API.
 *
 * Created by Giannis Giannakopoulos on 2/24/16.
 */
public abstract class Analyzer {
    protected final List<OutputSpacePoint> samples;
    protected final HashMap<String, HashMap<String, Double>> distances;

    public Analyzer(List<OutputSpacePoint> samples) {
        this.samples = samples;
        this.distances = new HashMap<>();
        for(String s:this.samples.get(0).getInputSpacePoint().getKeysAsCollection()) {
            this.distances.put(s, new HashMap<>());
        }
        this.distances.put(this.samples.get(0).getKey(), new HashMap<>());
    }

    public abstract void analyze();

    /**
     * Returns the distances between the dimensions
     * @return
     */
    public HashMap<String, HashMap<String, Double>> getDistances() {
        return this.distances;
    }

}
