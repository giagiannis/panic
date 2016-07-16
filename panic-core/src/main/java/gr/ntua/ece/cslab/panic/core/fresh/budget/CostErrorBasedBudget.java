
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

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.beans.Expression;

/**
 * Created by Giannis Giannakopoulos on 7/13/16.
 */
public class CostErrorBasedBudget extends ErrorBasedBudget {
    private String costFunction;
    private Double maxCost;
    private Double costCoefficient;

    public CostErrorBasedBudget(DecisionTree tree, Integer totalBudget, Integer coefficient, String costFunction) {
        super(tree, totalBudget, coefficient);
        this.costFunction = costFunction;

        // estimate maxCost here
//        this.cost(tree.getLeaves().get(0));
        this.maxCost=this.tree.getLeaves().stream().filter(b->!this.pathsToIgnore.contains(b.treePath())).mapToDouble(this::cost).max().getAsDouble();
    }

    @Override
    protected double normalizedScore(DecisionTreeLeafNode leaf) {
	    double score = super.normalizedScore(leaf);
	    double costScore = this.costCoefficient*this.getNormalizedCost(leaf);
//	return(score<=costScore?score:score-costScore);
        return score-costScore;
    }

    private double getNormalizedCost(DecisionTreeLeafNode leaf) {
        return this.cost(leaf)/this.maxCost;
    }

    private double cost(DecisionTreeLeafNode leaf) {
        return leaf.getDeploymentSpace().getPoints().stream().mapToDouble(this::cost).average().getAsDouble();
    }

    private double cost(InputSpacePoint point) {
        ExpressionBuilder builder = new ExpressionBuilder(this.costFunction);
        for(String s: point.getKeysAsCollection()) {
            builder.withVariable(s, point.getValue(s));
        }
        Calculable cal = null;
        try {
            cal = builder.build();
        } catch (UnknownFunctionException e) {
            e.printStackTrace();
        } catch (UnparsableExpressionException e) {
            e.printStackTrace();
        }
        return cal.calculate();
    }

    public void setCostCoefficient(Double costCoefficient) {
        this.costCoefficient = costCoefficient;
    }
}
