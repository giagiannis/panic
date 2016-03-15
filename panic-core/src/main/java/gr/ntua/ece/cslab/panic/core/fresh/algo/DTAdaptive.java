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
import gr.ntua.ece.cslab.panic.core.fresh.analyzers.Analyzer;
import gr.ntua.ece.cslab.panic.core.fresh.analyzers.AnalyzerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.budget.Budget;
import gr.ntua.ece.cslab.panic.core.fresh.budget.BudgetFactory;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Algorithm that trains the
 * Created by Giannis Giannakopoulos on 3/1/16.
 */
public class DTAdaptive extends DTAlgorithm {
    private int steps;
    private Budget budgetStrategy;
    private Set<String> treePathsToIgnore;

    public DTAdaptive(int deploymentBudget,
                      String samplerType,
                      MetricSource source,
                      String separatorType,
                      String budgetType, Properties budgetProperties,
                      String selectorType, Properties selectorProperties,
                      String analyzerType, Properties analyzerProperties) {
        super(deploymentBudget, samplerType, source, separatorType, budgetType, budgetProperties, selectorType, selectorProperties,
                analyzerType, analyzerProperties);
        this.treePathsToIgnore = new HashSet<>();
    }

    @Override
    public void run() {
        while (!this.terminationCondition()) {
            this.step();
        }
    }

    private void step() {
        this.steps += 1;
        long start = System.currentTimeMillis();

        int numberOfLeaves;
        debugPrint(String.format("Step %d: Expanding tree (sample size: %d)...\t", this.steps, this.tree.getSamples().size()));
        DecisionTree tree = this.expandAll(this.tree);
        numberOfLeaves = tree.getLeaves().size();
//        System.out.println("DTAdaptive.step");
//        System.out.println("Number of leaves:\t"+numberOfLeaves);
        debugPrint(String.format("Done! [ %d ms ]\n", System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        debugPrint(String.format("Step %d: Creating budget object...\t\t", this.steps));
        BudgetFactory factory = new BudgetFactory();
        this.budgetStrategy = factory.create(this.budgetType, tree, this.budgetProperties, this.deploymentBudget, this.treePathsToIgnore);
        debugPrint(String.format("Done! [ %d ms ]\n", System.currentTimeMillis() - start));


        start = System.currentTimeMillis();
        debugPrint(String.format("Step %d: Iterating through leaves...\t\t", this.steps));
        for (DecisionTreeLeafNode l : tree.getLeaves()) {
            if (this.terminationCondition())
                break;
            int budget = this.budgetStrategy.estimate(l);
            this.sampleLeaf(l, budget);
        }
        debugPrint(String.format("Done! [ %d ms ]\n", System.currentTimeMillis() - start));

        if (this.onlineTraining) {
            start = System.currentTimeMillis();
            debugPrint(String.format("Step %d: Expanding the main tree...\t\t", this.steps));
            this.tree = this.expandTree(this.tree);
            debugPrint(String.format("Done! [ %d ms ]\n", System.currentTimeMillis() - start));
        }
    }

    private void sampleLeaf(DecisionTreeLeafNode leaf, int budget) {
        SamplerFactory factory = new SamplerFactory();
        List<InputSpacePoint> forbiddenPoints = new LinkedList<>(this.source.unavailablePoints());
//        System.err.println("DTAdaptive.sampleLeaf");
//        System.err.println("Unavailable points:\t" + this.source.unavailablePoints().size());

        forbiddenPoints.addAll(this.tree.getSamples().stream().map(OutputSpacePoint::getInputSpacePoint).collect(Collectors.toList()));

        Properties samplerProperties = new Properties();
        if(!this.analyzerType.equals("none") && leaf.getPoints().size()>0) {
            AnalyzerFactory factory1 = new AnalyzerFactory();
            Analyzer analyzer = factory1.create(this.analyzerType, leaf.getPoints());
            analyzer.analyze();
            String keyName = leaf.getPoints().get(0).getKey();
            String dimensionOrder = analyzer.getDistances().get(keyName).entrySet().
                    parallelStream().
                    filter(a->a.getKey()!="constant").
                    sorted((a,b)-> -a.getValue().compareTo(b.getValue())).
                    map(a->a.getKey()).reduce((s, s2) -> s+","+s2).get();
            samplerProperties.setProperty("dimensions", dimensionOrder);
        }

        Sampler sampler = factory.create(this.samplerType, leaf.getDeploymentSpace(), budget, forbiddenPoints, samplerProperties);
        HashSet<InputSpacePoint> forbiddenPointsSet = new HashSet<>(forbiddenPoints);
        int pointsReturned = 0;
        while (sampler.hasMore()) {
            InputSpacePoint point = sampler.next();
            if (point != null) {
                pointsReturned += 1;
                OutputSpacePoint outputSpacePoint = this.source.getPoint(point);
//                System.err.format("Source returned %s for point %s (belongs in forbidden: %s)\n", outputSpacePoint, point, forbiddenPointsSet.contains(point));
                this.tree.addPoint(outputSpacePoint);
            }
        }
        if (pointsReturned < budget) {
            this.treePathsToIgnore.add(leaf.treePath());
//            System.out.println("DTAdaptive.sampleLeaf");
//            System.out.println(this.treePathsToIgnore);
        }
    }


}
