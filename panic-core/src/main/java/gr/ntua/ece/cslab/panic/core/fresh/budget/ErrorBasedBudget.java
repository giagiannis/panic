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

package gr.ntua.ece.cslab.panic.core.fresh.budget;

import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithm;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Giannis Giannakopoulos on 2/25/16.
 */
public class ErrorBasedBudget extends Budget {
    private Integer maxBudget;
    public ErrorBasedBudget(DecisionTree tree, Properties properties, Integer totalBudget) {
        super(tree, properties, totalBudget);
        this.maxBudget = new Integer(properties.getProperty("max"));
    }

    @Override
    public int estimate(DecisionTreeNode node) {
        return Double.MAX_EXPONENT;
    }
}
