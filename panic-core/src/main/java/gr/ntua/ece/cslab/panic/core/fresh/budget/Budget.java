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

import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;

import java.util.Properties;

/**
 * Base class for all the deploymentBudget strategies.
 * Created by Giannis Giannakopoulos on 2/17/16.
 */
public abstract class Budget {
    protected final DecisionTree tree;
    protected final Properties properties;
    protected final Integer totalBudget;

    public Budget(DecisionTree tree, Properties properties, Integer totalBudget) {
        this.tree = tree;
        this.properties = properties;
        this.totalBudget = totalBudget;
    }

    public abstract void configure();
    public abstract int estimate(DecisionTreeNode node);
    public abstract int estimate(DecisionTreeNode node, DecisionTree tree);
}
