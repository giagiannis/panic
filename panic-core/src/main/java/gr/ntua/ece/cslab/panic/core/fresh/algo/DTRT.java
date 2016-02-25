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
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.*;

/**
 * Algorithm that trains a decision train iteratively.
 * Created by Giannis Giannakopoulos on 2/25/16.
 */
public class DTRT extends DTAlgorithm{


    public DTRT(int deploymentBudget, String samplerType, MetricSource source, String separatorType, String budgetType, Properties budgetProperties) {
        super(deploymentBudget, samplerType, source, separatorType, budgetType, budgetProperties);
    }

    @Override
    public void run() {
        while(!terminationCondition()) {
            this.step();
        }
    }

    private void step() {
        // expands the tree
        DecisionTree tree = this.expandAll(this.tree);

        // order the leaves according to their errors (most erroneous leaves go first)
        List<DecisionTreeLeafNode> leaves = tree.getLeaves();
        Collections.sort(leaves, (node1, node2) -> {
            double mse1=CrossValidation.meanSquareError(LinearRegression.class, node1.getPoints());
            double mse2=CrossValidation.meanSquareError(LinearRegression.class, node2.getPoints());
            return -Double.compare(mse1,mse2);
        });

        // for each leaf sample
        DecisionTreeLeafNode mostErroneousLeaf = leaves.remove(0);
        this.sampleLeaf(mostErroneousLeaf, tree);
    }

    protected void sampleLeaf(DecisionTreeLeafNode leaf, DecisionTree tree) {
        int budget = this.budgetStrategy.estimate(leaf, tree);
        SamplerFactory factory = new SamplerFactory();
        AbstractSampler sampler = factory.create(this.samplerType, leaf.getDeploymentSpace(), budget);
        while (sampler.hasMore() && !this.terminationCondition()) {
            InputSpacePoint point = sampler.next();
            OutputSpacePoint out = source.getPoint(point);
            this.tree.addPoint(out);
        }
    }

        // expands the tree by one level
    private DecisionTree expandTree(DecisionTree original) {
        DecisionTree tree = original.clone();

        ReplacementCouples couples = new ReplacementCouples();
        for(DecisionTreeLeafNode leaf : tree.getLeaves()) {
            SeparatorFactory factory = new SeparatorFactory();
            Separator separator = factory.create(this.separatorType, leaf);
            separator.separate();
            if(separator.getResult()!=null) {
                couples.addCouple(leaf, separator.getResult());
            }
        }

        for(DecisionTreeNode t : couples.getOriginalNodes()) {
            tree.replaceNode(t, couples.getNode(t));
        }
        return tree;
    }

    // expands the tree until no new expansions can be made
    private DecisionTree expandAll(DecisionTree original) {
        DecisionTree tree = original.clone();
        int numberOfLeaves = 0;
        while(numberOfLeaves!=tree.getLeaves().size()) {
            numberOfLeaves = tree.getLeaves().size();
            tree = this.expandTree(tree);
        }
        return tree;
    }
}
