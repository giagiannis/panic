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

import java.util.Properties;
import java.util.Set;

/**
 * Factor for Budget instances
 * Created by Giannis Giannakopoulos on 2/17/16.
 */
public class BudgetFactory {

    public Budget create(String type, DecisionTree tree, Properties properties, Integer totalBudget, Set<String> treePathsToIgnore) {
        Budget budget = null;
        switch (type) {
            case "constant":
                Integer constant = new Integer(properties.getProperty("coefficient"));
                budget = new ConstantBudget(tree, totalBudget, constant);
                break;
            case "tree":
                budget = new TreeBudget(tree, totalBudget, null, null);
                break;
            case "error":
                Integer coefficient = new Integer(properties.getProperty("coefficient"));
                budget = new ErrorBasedBudget(tree, totalBudget, coefficient);
                if(properties.containsKey("error.coefficient")) {
                    ((ErrorBasedBudget)budget).setErrorCoefficient(new Double(properties.getProperty("error.coefficient")));
                }
                if(properties.containsKey("region.coefficient")) {
                    ((ErrorBasedBudget)budget).setRegionCoefficient(new Double(properties.getProperty("region.coefficient")));
                }
                break;
            case "costerror":
                coefficient = new Integer(properties.getProperty("coefficient"));
                budget = new CostErrorBasedBudget(tree, totalBudget, coefficient, properties.getProperty("cost.function"));
                if(properties.containsKey("error.coefficient")) {
                    ((CostErrorBasedBudget)budget).setErrorCoefficient(new Double(properties.getProperty("error.coefficient")));
                }
                if(properties.containsKey("region.coefficient")) {
                    ((CostErrorBasedBudget)budget).setRegionCoefficient(new Double(properties.getProperty("region.coefficient")));
                }
                if(properties.containsKey("cost.coefficient")) {
                    ((CostErrorBasedBudget)budget).setCostCoefficient(new Double(properties.getProperty("cost.coefficient")));
                }
                break;
        }
        if(treePathsToIgnore!=null)
            budget.setPathsToIgnore(treePathsToIgnore);
        return budget;
    }
}
