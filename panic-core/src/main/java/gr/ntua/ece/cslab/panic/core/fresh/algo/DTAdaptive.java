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
import gr.ntua.ece.cslab.panic.core.fresh.budget.Budget;
import gr.ntua.ece.cslab.panic.core.fresh.budget.BudgetFactory;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeTestNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;

import java.util.*;

/**
 * DTAdaptive algorithm
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DTAdaptive {

    // fields of the class
    protected final List<OutputSpacePoint> samples;
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

        this.samples = new LinkedList<>();
        this.tree = new DecisionTree(this.space);

        BudgetFactory factory = new BudgetFactory();
        this.budgetStrategy = factory.create(budgetType, this.tree, budgetProperties);
        this.budgetStrategy.configure();

        this.samplerType = samplerType;
        this.separatorType = separatorType;
    }

    // getters and setters
    public List<OutputSpacePoint> getSamples() {
        return samples;
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
            this.runStep();
        }
        System.out.println(this.tree.toString());
    }

    // terminationCondition is true if the algorithm should terminate
    private boolean terminationCondition() {
        return this.tree.getSamples().size()>=this.deploymentBudget;
    }

    private void runStep() {

        for(DecisionTreeLeafNode leaf : this.tree.getLeaves()) {
            int budget = this.budgetStrategy.estimate(leaf);
            SamplerFactory factory = new SamplerFactory();
            Sampler sampler = factory.create(this.samplerType);
            sampler.setDimensionsWithRanges(leaf.getDeploymentSpace().getRange());
            sampler.setPointsToPick(budget);
            sampler.configureSampler();
            while(sampler.hasMore()) {
                InputSpacePoint point = sampler.next();
                OutputSpacePoint out = source.getPoint(point);
                this.tree.addPoint(out);
            }
        }

        Map<DecisionTreeLeafNode, DecisionTreeTestNode> separationMapping = new HashMap<>();
        for(DecisionTreeLeafNode leaf : this.tree.getLeaves()) {
            SeparatorFactory factory = new SeparatorFactory();
            Separator separator = factory.create(this.separatorType, leaf);
            separator.separate();
            separationMapping.put(leaf, separator.getResult());
        }

        for(Map.Entry<DecisionTreeLeafNode, DecisionTreeTestNode> kv:separationMapping.entrySet()) {
            this.tree.replaceNode(kv.getKey(), kv.getValue());
        }


    }
}
