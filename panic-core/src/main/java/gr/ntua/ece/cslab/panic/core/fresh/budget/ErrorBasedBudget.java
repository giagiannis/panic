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
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Giannis Giannakopoulos on 2/25/16.
 */
public class ErrorBasedBudget extends Budget {

    private final Double minError, maxError, minRegion, maxRegion;
    public double errorCoefficient=1.0;
    public double regionCoefficient=1.0;
    private Integer coefficient;
    private Map<String, Integer> budgetMap = null;

    public ErrorBasedBudget(DecisionTree tree, Integer totalBudget, Integer coefficient) {
        super(tree, totalBudget);

        //
//        this.minError = this.tree.getLeaves().stream().filter(b->!this.pathsToIgnore.contains(b.treePath())).mapToDouble(this::error).min().getAsDouble();
        this.minError = 0.0;
        this.maxError = this.tree.getLeaves().stream().filter(b->!this.pathsToIgnore.contains(b.treePath())).mapToDouble(this::error).max().getAsDouble();
        this.minRegion= this.tree.getLeaves().stream().filter(b->!this.pathsToIgnore.contains(b.treePath())).mapToDouble(this::region).min().getAsDouble();
        this.maxRegion= this.tree.getLeaves().stream().filter(b->!this.pathsToIgnore.contains(b.treePath())).mapToDouble(this::region).max().getAsDouble();
        this.coefficient = coefficient;
    }

    public double getErrorCoefficient() {
        return errorCoefficient;
    }

    public void setErrorCoefficient(double errorCoefficient) {
        this.errorCoefficient = errorCoefficient;
    }

    public double getRegionCoefficient() {
        return regionCoefficient;
    }

    public void setRegionCoefficient(double regionCoefficient) {
        this.regionCoefficient = regionCoefficient;
    }

    @Override
    public int estimate(DecisionTreeNode node) {
        if(this.budgetMap == null) {
            this.budgetMap = this.getScores();
        }
        return this.budgetMap.get(node.getId());
    }


    private double error(DecisionTreeLeafNode leaf) {
        return CrossValidation.meanSquareError(LinearRegression.class, leaf.getPoints());
    }

    private double region(DecisionTreeLeafNode leaf) {
        double mul = 1.0;
        for(String s:leaf.getDeploymentSpace().getDimensionLabels()) {
            mul*=leaf.getDeploymentSpace().getSize();
        }
        return mul;
    }

    protected double normalizedScore(DecisionTreeLeafNode leaf) {
        return this.errorCoefficient*this.normalizedError(leaf) + this.regionCoefficient*this.normalizedRegion(leaf);
    }

    private double normalizedError(DecisionTreeLeafNode leaf) {
        if(this.maxError.equals(this.minError)|| this.maxError.isNaN() || this.minError.isNaN()) {
            return 0.0;
        }
        //return (this.error(leaf)-this.minError)/(this.maxError - this.minError);
        return (this.error(leaf))/(this.maxError);
    }

    private double normalizedRegion(DecisionTreeLeafNode leaf) {
        if(this.maxRegion.equals(this.minRegion)|| this.maxRegion.isNaN() || this.minRegion.isNaN()) {
            return 0.0;
        }
        return (this.region(leaf))/(this.maxRegion);
        //return (this.region(leaf) - this.minRegion)/(this.maxRegion-this.minRegion);
    }

    private Map<String, Integer> getScores() {
        Map<String, Integer> map = new HashMap<>();
        Double min = this.tree.getLeaves().stream().mapToDouble(this::normalizedScore).min().getAsDouble();
        double pivotToAdd = 0.0;
        if(min<0) {
            pivotToAdd=Math.abs(min);
        }
        Double sum = this.tree.getLeaves().stream().mapToDouble(b->normalizedScore(b)).sum() + this.tree.getLeaves().size()*pivotToAdd;
        if(sum==0 ) { // we need to do shit here
//            if(this.tree.getLeaves().size()>1) {
//                System.err.println("ErrorBasedBudget.getScores: sum is NaN but we have more leaves than 1! Exiting..");
//                System.err.println("Specifically we have:");
//                System.err.format("min error: %.5f\tmax error: %.5f\tmin region: %.5f\tmax region:%.5f\n",
//                        this.minError, this.maxError, this.minRegion, this.maxRegion);
//                for(DecisionTreeLeafNode leaf : this.tree.getLeaves()) {
//                    System.err.format("%s:\tscore: %.5f\terror: %.5f\tregion: %.5f\n", leaf.getId(), this.normalizedScore(leaf), this.normalizedError(leaf), this.normalizedRegion(leaf));
//                }
//                System.exit(1);
//            } else {
//                map.put(this.tree.getLeaves().get(0).getId(), this.coefficient);
//            }
        } else {
//            double leftOver = (this.coefficient)
            for(DecisionTreeLeafNode  l : this.tree.getLeaves()) {
                double budget = ((this.normalizedScore(l)+pivotToAdd)/sum) * (this.coefficient);
                map.put(l.getId(), ((int) Math.ceil(budget)));
            }
            int actual = map.values().stream().mapToInt(Integer::intValue).sum();
            int difference = actual - this.coefficient;
            while(difference>0) {
                for (String leafId : map.keySet().stream().filter(kv -> map.get(kv) > 0).collect(Collectors.toList())) {
                    Integer oldValue = map.get(leafId);
                    map.put(leafId, oldValue - 1);
                    difference--;
                    if (difference == 0)
                        break;
                }
            }
        }
        return map;
    }
}
