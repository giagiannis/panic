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
import gr.ntua.ece.cslab.panic.core.fresh.samplers.Sampler;
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
 * Interface representating the algorith API
 * Created by Giannis Giannakopoulos on 2/19/16.
 */
public abstract class DTAlgorithm {

    protected final int deploymentBudget;
    protected final String budgetType;
    protected final Properties budgetProperties;
    protected DecisionTree tree;
    protected final String samplerType;
    protected final DeploymentSpace space;
    protected final MetricSource source;
    protected final String separatorType;
    protected DecisionTree bestTree;
//    protected Budget budgetStrategy;
    protected final String selectorType;
    protected final Properties selectorProperties;
    static public boolean DEBUG=false;
    protected boolean onlineTraining;


    public DTAlgorithm(int deploymentBudget, String samplerType, MetricSource source,
                       String separatorType,
                       String budgetType, Properties budgetProperties,
                       String selectorType, Properties selectorProperties) {
        this.deploymentBudget = deploymentBudget;
        this.samplerType = samplerType;
        this.source = source;
        this.space = source.getDeploymentSpace();
        this.tree = new DecisionTree(this.space);
        this.separatorType = separatorType;

        this.budgetType = budgetType;
        this.budgetProperties = budgetProperties;
//        BudgetFactory factory = new BudgetFactory();
//        this.budgetStrategy = factory.create(budgetType, this.tree, budgetProperties, deploymentBudget);
//        this.budgetStrategy.configure();

        this.selectorType = selectorType;
        this.selectorProperties = selectorProperties;
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

    public void setOnlineTraining(boolean onlineTraining) {
        this.onlineTraining = onlineTraining;
    }

    // heuristics

    public double meanSquareError() {
        double sum = 0.0;
        for (DecisionTreeLeafNode l : this.tree.getLeaves()) {
            sum += CrossValidation.meanSquareError(LinearRegression.class, l.getPoints()) * l.getPoints().size();
        }
        return sum / this.tree.getSamples().size();
    }

    public static double meanSquareError(DecisionTree tree) {
        double sum = 0.0;
        for (DecisionTreeLeafNode l : tree.getLeaves()) {
            sum += CrossValidation.meanSquareError(LinearRegression.class, l.getPoints()) * l.getPoints().size();
        }
        return sum / tree.getSamples().size();
    }

    public static double meanSquareError(DecisionTreeNode node) {
        double mse = 0.0;
        List<OutputSpacePoint> points = null;
        if (node.isLeaf()) {
            points = node.castToLeaf().getPoints();
        } else {
            points = node.castToTest().getSamples();
        }
        return CrossValidation.meanSquareError(LinearRegression.class, points);
    }


    public abstract void run();

    /**
     * This method creates new trees with different partitioning setups and gets the one that appears to be the best.
     */
    public DecisionTree getBestTree() {
        DecisionTree currentTree = new DecisionTree(this.space);
        currentTree.addPoint(this.tree.getSamples());
        DecisionTree bestTree = currentTree.clone();
        double bestScore = DTAlgorithm.meanSquareError(currentTree);

        boolean someoneReplaced = true;
        while (someoneReplaced) {
            ReplacementCouples couples = new ReplacementCouples();
            someoneReplaced = false;
            for (DecisionTreeLeafNode l : currentTree.getLeaves()) {
                SeparatorFactory factory1 = new SeparatorFactory();
                Separator sep = factory1.create(this.separatorType, l);
                sep.separate();

                if (sep.getResult() != null) {
                    couples.addCouple(l, sep.getResult());
                    someoneReplaced = true;
                }
            }
            for (DecisionTreeNode n : couples.getOriginalNodes()) {
                currentTree.replaceNode(n, couples.getNode(n));
            }

            double currentScore = DTAlgorithm.meanSquareError(currentTree);
            if (currentScore <= bestScore) {
                bestTree = currentTree;
                bestScore = currentScore;
            }
            currentTree = currentTree.clone();
        }
        this.bestTree = bestTree;
        return bestTree;
    }

    /**
     * Class used to hold the couples of nodes that can be replaced into a Decision Tree
     */
    protected static class ReplacementCouples {
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

//    protected void sampleLeaf(DecisionTreeLeafNode leaf) {
//        int budget = this.budgetStrategy.estimate(leaf);
//        SamplerFactory factory = new SamplerFactory();
//        List<InputSpacePoint> forbiddenPoints = new LinkedList<>(this.source.unavailablePoints());
//        for (OutputSpacePoint p : this.tree.getSamples()) {
//            forbiddenPoints.add(p.getInputSpacePoint());
//        }
//        Sampler sampler = factory.create(this.samplerType, leaf.getDeploymentSpace(), budget, forbiddenPoints);
//        while (sampler.hasMore() && !this.terminationCondition()) {
//            InputSpacePoint point = sampler.next();
//            OutputSpacePoint out = source.getPoint(point);
//            this.tree.addPoint(out);
//        }
//    }


    // terminationCondition is true if the algorithm should terminate
    protected boolean terminationCondition() {
        return this.tree.getSamples().size() >= this.deploymentBudget;
    }

    protected DecisionTree expandTree(DecisionTree original) {
        DecisionTree tree = original.clone();

        ReplacementCouples couples = new ReplacementCouples();
        for (DecisionTreeLeafNode leaf : tree.getLeaves()) {
            SeparatorFactory factory = new SeparatorFactory();
            Separator separator = factory.create(this.separatorType, leaf);
            separator.separate();
            if (separator.getResult() != null) {
                couples.addCouple(leaf, separator.getResult());
            }
        }

        for (DecisionTreeNode t : couples.getOriginalNodes()) {
            tree.replaceNode(t, couples.getNode(t));
        }
        return tree;
    }
}
