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
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.Separator;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Algorithm that trains a decision train iteratively.
 * Created by Giannis Giannakopoulos on 2/25/16.
 */
public class DTRT extends DTAlgorithm{

    private Integer steps;
    private Set<String> treePathsToIgnore;

    private DecisionTree expandedCachedTree = null;
    private boolean onlineTraining;

    public DTRT(int deploymentBudget, String samplerType, MetricSource source, String separatorType, String budgetType, Properties budgetProperties) {
        super(deploymentBudget, samplerType, source, separatorType, budgetType, budgetProperties);
        this.steps=0;
        this.treePathsToIgnore = new HashSet<>();
    }

    public void setOnlineTraining(boolean onlineTraining) {
        this.onlineTraining = onlineTraining;
    }

    @Override
    public void run() {
        while(!terminationCondition()) {
            this.step();
        }
    }

    private void step() {
        this.steps+=1;
        long start = System.currentTimeMillis();
        System.err.format("Step %d: Expanding tree (sample size: %d)... ", this.steps, this.tree.getSamples().size());
        DecisionTree tree = this.expandAll(this.tree);
        System.err.format("Done! [ %d ms ]\n", System.currentTimeMillis()-start);
        System.err.format("Step %d: Selecting most erroneous leaf... ", this.steps);
        start = System.currentTimeMillis();
        DecisionTreeLeafNode leaf = this.getLeafWithHighestError(tree);
        System.err.format("Done! [ %d ms ]\n", System.currentTimeMillis()-start);
        System.err.format("Step %d: Sampling leaf... ", this.steps);
        start = System.currentTimeMillis();
        this.sampleLeaf(leaf, tree);
        System.err.format("Done! [ %d ms ]\n", System.currentTimeMillis()-start);

        if(this.onlineTraining) {
            this.tree = this.expandTree(this.tree);
        }

    }

    protected void sampleLeaf(DecisionTreeLeafNode leaf, DecisionTree tree) {
        int budget = this.budgetStrategy.estimate(leaf, tree);
        SamplerFactory factory = new SamplerFactory();
        List<InputSpacePoint> forbiddenPoints = new LinkedList<>(this.source.unavailablePoints());
        for(OutputSpacePoint p : this.tree.getSamples()) {
            forbiddenPoints.add(p.getInputSpacePoint());
        }
        Sampler sampler = factory.create(this.samplerType, leaf.getDeploymentSpace(), budget, forbiddenPoints);

        boolean pointPicked = false;
        while (sampler.hasMore() && !this.terminationCondition()) {
            InputSpacePoint point = sampler.next();
            if(point!=null) {
                OutputSpacePoint out = source.getPoint(point);
                this.tree.addPoint(out);
                pointPicked=true;
                this.expandedCachedTree = null;
            }
        }
        // what happens if all the leaves are blacklisted?
        if(!pointPicked) {
            this.treePathsToIgnore.add(leaf.treePath());
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
        if(expandedCachedTree !=null)
            return expandedCachedTree;
        DecisionTree tree = original.clone();
        int numberOfLeaves = 0;
        while(numberOfLeaves!=tree.getLeaves().size()) {
            numberOfLeaves = tree.getLeaves().size();
            tree = this.expandTree(tree);
        }
        this.expandedCachedTree=tree;
        return tree;
    }

    private DecisionTreeLeafNode getLeafWithHighestError(DecisionTree tree) {
        double minError = Double.POSITIVE_INFINITY;
        DecisionTreeLeafNode leaf = null;
        for(DecisionTreeLeafNode l : tree.getLeaves()) {
            double currentError = DTAlgorithm.meanSquareError(l);
            if((!this.treePathsToIgnore.contains(l.treePath())) && (currentError<minError || leaf==null)) {
                leaf = l;
                minError = currentError;
            }
        }
//        System.out.println(tree.toString());
        if(leaf==null) {
            System.err.println("DTRT.step");
            System.err.println("Could not find a leaf. Let's see what happens now:");
            System.err.println(tree.toString());
            List<InputSpacePoint> sampled = this.getSamples().stream().map(OutputSpacePoint::getInputSpacePoint).collect(Collectors.toCollection(LinkedList::new));
            for(DecisionTreeLeafNode l : tree.getLeaves()) {
                Sampler sampler = new SamplerFactory().create(this.samplerType, l.getDeploymentSpace(), 135, sampled);
                int countWithForbiddenPoints = 0;
                while (sampler.hasMore()) {
                    sampler.next();
                    countWithForbiddenPoints +=1;
                }

                sampler = new SamplerFactory().create(this.samplerType, l.getDeploymentSpace(), 135, null);
                int countWithoutForbiddenPoints = 0;
                while (sampler.hasMore()) {
                    sampler.next();
                    countWithoutForbiddenPoints +=1;
                }
                System.err.format("Leaf id: %s, into paths to ignore: %s, mse %.5f, ranges: %s, points yet (with forb): %d, points yet (without forb): %d\n",
                        l.getId(),
                        this.treePathsToIgnore.contains(l.treePath()),
                        DTAlgorithm.meanSquareError(l),
                        l.getDeploymentSpace().getRange(),
                        countWithForbiddenPoints,
                        countWithoutForbiddenPoints);
            }
            System.err.println(this.treePathsToIgnore);
            System.exit(1);
        }

        return leaf;
    }
}
