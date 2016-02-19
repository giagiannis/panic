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

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.budget.Budget;
import gr.ntua.ece.cslab.panic.core.fresh.budget.BudgetFactory;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.*;

/**
 * DTAdaptive algorithm
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DTAdaptive {

    // fields of the class
    protected final int deploymentBudget;
    protected final MetricSource source;
    protected final DeploymentSpace space;
    protected final DecisionTree tree;
    protected final Budget budgetStrategy;
    private final String samplerType;
    private final String separatorType;

    public DTAdaptive(int deploymentBudget, MetricSource source,
                      String budgetType, Properties budgetProperties,
                      String samplerType, String separatorType) {
        this.deploymentBudget = deploymentBudget;
        this.source = source;
        this.space = source.getDeploymentSpace();
        this.tree = new DecisionTree(this.space);

        BudgetFactory factory = new BudgetFactory();
        this.budgetStrategy = factory.create(budgetType, this.tree, budgetProperties, deploymentBudget);
        this.budgetStrategy.configure();

        this.samplerType = samplerType;
        this.separatorType = separatorType;
    }

    // getters and setters
    public List<OutputSpacePoint> getSamples() {
        return this.tree.getSamples();
    }

    public double getDeploymentBudget() {
        return deploymentBudget;
    }


    public DeploymentSpace getSpace() {
        return space;
    }


    public MetricSource getSource() {
        return source;
    }


    public DecisionTree getTree() {
        return tree;
    }


    // Class' public API
    /**
     * Start the algorithm.
     */
    public void run() {
        while(!this.terminationCondition()) {
//            System.out.println(this.tree.toString());
            this.runStep();
//            System.out.println("\n\n");
        }
//        System.out.println(this.tree.toString());
//        System.out.println("\n\n");
//        System.out.println("MSE: "+this.getMSE());;
    }

    // terminationCondition is true if the algorithm should terminate
    private boolean terminationCondition() {
        return this.tree.getSamples().size()>=this.deploymentBudget;
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

    private void sampleLeaf(DecisionTreeLeafNode leaf) {
        int budget = this.budgetStrategy.estimate(leaf);
        SamplerFactory factory = new SamplerFactory();
        AbstractSampler sampler = factory.create(this.samplerType);
        sampler.setDimensionsWithRanges(leaf.getDeploymentSpace().getRange());
        sampler.setPointsToPick(budget);
        sampler.configureSampler();
        while(sampler.hasMore() && !this.terminationCondition()) {
            InputSpacePoint point = sampler.next();
            OutputSpacePoint out = source.getPoint(point);
            this.tree.addPoint(out);
        }
    }

    public double getMSE() {
        double sum = 0.0;
        for(DecisionTreeLeafNode l : this.tree.getLeaves()) {
//            System.out.println(l.getPoints().size());
            sum += CrossValidation.meanSquareError(LinearRegression.class, l.getPoints()) * l.getPoints().size();
        }
        return sum/this.tree.getSamples().size();
    }

    /**
     * Class used to hold the couples of nodes that can be replaced into a Decision Tree
     */
    private static class ReplacementCouples {
        private Map<DecisionTreeNode, DecisionTreeNode> mapping;

        public ReplacementCouples() {
            this.mapping = new HashMap<>();
        }

        public void addCouple(DecisionTreeNode oldNode, DecisionTreeNode newNode) {
            this.mapping.put(oldNode, newNode);
        }

        public DecisionTreeNode getNode(DecisionTreeNode oldNode) {
            return this.mapping.get(oldNode);
        }

        public Set<DecisionTreeNode> getOriginalNodes() {
            return this.mapping.keySet();
        }
    }
}
