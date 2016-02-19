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
import gr.ntua.ece.cslab.panic.core.fresh.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;

import java.util.*;

/**
 * DTOnline algorithm
 * Created by Giannis Giannakopoulos on 2/15/16.
 */
public class DTOnline extends DTAlgorithm {

    // fields of the class
    protected final Budget budgetStrategy;
    private final String separatorType;

    public DTOnline(int deploymentBudget, String samplerType, MetricSource source,
                    String budgetType, Properties budgetProperties, String separatorType) {
        super(deploymentBudget, samplerType, source);

        BudgetFactory factory = new BudgetFactory();
        this.budgetStrategy = factory.create(budgetType, this.tree, budgetProperties, deploymentBudget);
        this.budgetStrategy.configure();

        this.separatorType = separatorType;
    }

    // Class' public API
    /**
     * Start the algorithm.
     */
    public void run() {
        while(!this.terminationCondition()) {
            this.runStep();
        }
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
}
