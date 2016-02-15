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

package gr.ntua.ece.cslab.panic.core.fresh.algo;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;

import java.util.List;

/**
 * DTAdaptive algorithm
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DTAdaptive {

    // fields of the class
    protected List<OutputSpacePoint> samples;
    protected double budget;
    protected DeploymentSpace space;

    protected DecisionTree tree;

    public DTAdaptive() {
//        this.tree = new DecisionTree(space);
    }

    // getters and setters
    public List<OutputSpacePoint> getSamples() {
        return samples;
    }

    public void setSamples(List<OutputSpacePoint> samples) {
        this.samples = samples;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    // Class' public API

    /**
     * To be extended by the subclass.
     */
    public void run() {
        while(!this.terminationCondition()) {
            this.runStep();
        }
    }

    private boolean terminationCondition() {
        // TODO: implement terminationCondition
        // no breaks have been made
        return false;
    }

    private void runStep() {
        // TODO: implement runStep

        // estimate budget and sample points
        // break points
    }
}
