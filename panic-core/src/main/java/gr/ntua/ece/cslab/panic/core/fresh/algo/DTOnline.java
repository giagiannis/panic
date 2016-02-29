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

import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;

import java.util.*;

/**
 * DTOnline algorithm
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DTOnline extends DTAlgorithm {

    // fields of the class

    public DTOnline(int deploymentBudget, String samplerType, MetricSource source, String separatorType,
                    String budgetType, Properties budgetProperties, String selectorType, Properties selectorProperties) {
        super(deploymentBudget, samplerType, source, separatorType, budgetType, budgetProperties, selectorType, selectorProperties);

    }

    // Class' public API
    /**
     * Start the algorithm.
     */
    public void run() {
        while (!this.terminationCondition()) {
            this.runStep();
        }
    }

    private void runStep() {
        for(DecisionTreeLeafNode leaf : this.tree.getLeaves()) {
            this.sampleLeaf(leaf);
            if(this.terminationCondition()) {
                break;
            }
        }
        if(this.terminationCondition()) {
            return;
        }

        ReplacementCouples couples = new ReplacementCouples();
        for(DecisionTreeLeafNode leaf : this.tree.getLeaves()) {
            SeparatorFactory factory = new SeparatorFactory();
            Separator separator = factory.create(this.separatorType, leaf);
            separator.separate();
            if(separator.getResult()!=null) {
                couples.addCouple(leaf, separator.getResult());
            }
        }

        for(DecisionTreeNode t : couples.getOriginalNodes()) {
            this.tree.replaceNode(t, couples.getNode(t));
        }
    }

}
