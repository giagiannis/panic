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
import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface representating the algorith API
 * Created by Giannis Giannakopoulos on 2/19/16.
 */
public abstract class DTAlgorithm {

    protected final int deploymentBudget;
    protected DecisionTree tree;
    protected final String samplerType;
    protected final DeploymentSpace space;
    protected final MetricSource source;
    protected final String separatorType;
    protected DecisionTree bestTree;

    public DTAlgorithm(int deploymentBudget, String samplerType, MetricSource source, String separatorType) {
        this.deploymentBudget = deploymentBudget;
        this.samplerType = samplerType;
        this.source = source;
        this.space = source.getDeploymentSpace();
        this.tree = new DecisionTree(this.space);
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



    public double meanSquareError() {
        double sum = 0.0;
        for(DecisionTreeLeafNode l : this.tree.getLeaves()) {
            sum += CrossValidation.meanSquareError(LinearRegression.class, l.getPoints()) * l.getPoints().size();
        }
        return sum/this.tree.getSamples().size();
    }

    public static double meanSquareError(DecisionTree tree) {
        double sum = 0.0;
        for(DecisionTreeLeafNode l : tree.getLeaves()) {
            sum += CrossValidation.meanSquareError(LinearRegression.class, l.getPoints()) * l.getPoints().size();
        }
        return sum/tree.getSamples().size();
    }


    public abstract void run();

    /**
     * This method creates new trees with different partitioning setups and gets the one that appears to be the best.
     * This method creates new trees with different partitioning setups and gets the one that appears to be the best.
     */
    public DecisionTree getBestTree() {
        if (bestTree == null) {
            DecisionTree currentTree = this.tree.clone();
            DecisionTree bestTree = this.tree;
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
                if(currentScore<=bestScore) {
                    bestTree = currentTree;
                    bestScore = currentScore;
                }
                currentTree = currentTree.clone();
            }
            this.bestTree = bestTree;
        }
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


}
