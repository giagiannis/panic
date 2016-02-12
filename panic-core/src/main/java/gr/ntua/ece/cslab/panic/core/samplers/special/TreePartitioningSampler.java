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

package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.analyzers.RegressionAnalyzer;
import gr.ntua.ece.cslab.panic.core.budget.AbstractBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.budget.ConstantBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.budget.StandardTreeBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.partitioners.AbstractPartitioner;
import gr.ntua.ece.cslab.panic.core.partitioners.SingleDimensionPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.GridSampler;
import gr.ntua.ece.cslab.panic.core.samplers.TotalOrderingSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Giannis Giannakopoulos on 12/15/15.
 */
public class TreePartitioningSampler extends AbstractAdaptiveSampler {

    // default conf options
    public static String DEFAULT_PARTITIONER_CLASS = "gr.ntua.ece.cslab.panic.core.partitioners.SingleDimensionPartitioner";
    public static String DEFAULT_BUDGET_CLASS = "gr.ntua.ece.cslab.panic.core.budget.StandardTreeBudgetStrategy";
    public static String DEFAULT_SAMPLER_BIASED_CLASS = "gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler";
    public static String DEFAULT_SAMPLER_UNBIASED_CLASS = "gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler";
    public static Integer DEFAULT_BUDGET_TREE_LENGTH = 3;
    public static Double DEFAULT_BUDGET_TREE_COEFFICIENT = 1.0;
    public static Integer DEFAULT_BUDGET_CONSTANT_COEFFICIENT = 10;


    // objects of the sampler
    private RegionTree regionTree;
    protected AbstractSampler sampler;
    private AbstractBudgetStrategy budgetStrategy;

    public TreePartitioningSampler() {
        super();
        this.regionTree = new RegionTree();

        this.configurationsParameters.put("budget.class", "define the budget class to be used");
        this.configurationsParameters.put("budget.tree.coefficient", "define the coefficient of the regionTree budget strategies");
        this.configurationsParameters.put("budget.tree.length", "define the length of the regionTree budget strategies");
        this.configurationsParameters.put("budget.constant.coefficient", "define the coefficient of the constant budget strategy");
        this.configurationsParameters.put("partitioner.class", "define the partitioner class");
        this.configurationsParameters.put("sampler.biased.class", "define the class of the biased sampler");
        this.configurationsParameters.put("sampler.unbiased.class", "define the class of the unbiasedsampler");
    }

    public RegionTree getRegionTree() {
        return regionTree;
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        // configure regionTree object
        this.regionTree.addChild(new SpecificRegionTreeNode(this.ranges));
        this.regionTree.next();
        this.configureBudgetStrategy();
        this.configureUnbiasedSampler();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        if (!this.sampler.hasMore()) {  // reconfiguration is needed
            this.partitionSpace();
            this.regionTree.next();
            this.configureBiasedSampler();
        }
        return this.sampler.next();
    }


    /**
     * Class that extends the RegionTreeNode and add all the necessary info needed for this algorithm.
     */
    public static class SpecificRegionTreeNode extends RegionTreeNode {
        private RegressionAnalyzer analyzer;

        public SpecificRegionTreeNode(HashMap<String, List<Double>> region) {
            super(region);
        }

        public RegressionAnalyzer getAnalyzer() {
            return analyzer;
        }

        public void setAnalyzer(RegressionAnalyzer analyzer) {
            this.analyzer = analyzer;
        }
    }


    // configuration methods
    private void configureUnbiasedSampler() {
        String className = (this.configuration.containsKey("sampler.sampler.class") ?
                this.configuration.get("sampler.sampler.class") : DEFAULT_SAMPLER_UNBIASED_CLASS);
        try {
            this.sampler = (AbstractSampler) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.sampler.setDimensionsWithRanges(this.regionTree.getCurrent().getRegion());
        this.sampler.setPointsToPick(this.budgetStrategy.estimateBudget(this.regionTree.getCurrent()));
        this.sampler.configureSampler();
    }

    private void configureBiasedSampler() {
        String className = (this.configuration.containsKey("sampler.biased.class") ?
                this.configuration.get("sampler.biased.class") : DEFAULT_SAMPLER_BIASED_CLASS);
        try {
            this.sampler = (AbstractSampler) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.sampler.setDimensionsWithRanges(this.regionTree.getCurrent().getRegion());
        this.sampler.setPointsToPick(this.budgetStrategy.estimateBudget(this.regionTree.getCurrent()));

        if(this.sampler instanceof GridSampler) {
            if(((SpecificRegionTreeNode)this.regionTree.getCurrent()).getAnalyzer()==null) {
                RegressionAnalyzer analyzer = new RegressionAnalyzer();
                analyzer.setPointsToAnalyze(AbstractPartitioner.filterPoints(this.outputSpacePoints, this.regionTree.getCurrent().getRegion()));
                analyzer.analyze();
                ((SpecificRegionTreeNode) this.regionTree.getCurrent()).setAnalyzer(analyzer);
            }
            Map<String, Double> coefficients = ((SpecificRegionTreeNode)this.regionTree.getCurrent()).getAnalyzer().getCoefficients();


            double sum = 0.0;
            Map<String, Double> res = new HashMap<>();
            for(String s:coefficients.keySet()) {
                if(!s.equals("c"))
                    sum+=Math.abs(coefficients.get(s));
            }
            for(String s:coefficients.keySet()) {
                if(!s.equals("c"))
                    res.put(s,Math.abs(coefficients.get(s))/sum);
            }
            ((GridSampler) this.sampler).setWeights(res);
//            ((GridSampler) this.sampler).setForbiddenPoints;
        } else if(this.sampler instanceof TotalOrderingSampler) {
            if(((SpecificRegionTreeNode)this.regionTree.getCurrent()).getAnalyzer()==null) {
                RegressionAnalyzer analyzer  = new RegressionAnalyzer();
                analyzer.setPointsToAnalyze(AbstractPartitioner.filterPoints(this.outputSpacePoints, this.regionTree.getCurrent().getRegion()));
                analyzer.analyze();
                ((SpecificRegionTreeNode) this.regionTree.getCurrent()).setAnalyzer(analyzer);
            }
            ((TotalOrderingSampler) this.sampler).setDimensionOrdering(((SpecificRegionTreeNode) this.regionTree.getCurrent()).getAnalyzer().getDimensionOrdering());
        }
        this.sampler.configureSampler();
    }

    private void configureBudgetStrategy()  {
        String className = (this.configuration.containsKey("budget.class") ?
                this.configuration.get("budget.class") : DEFAULT_BUDGET_CLASS);
        try {
            this.budgetStrategy = (AbstractBudgetStrategy) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.budgetStrategy.setRegionTree(this.regionTree);
        this.budgetStrategy.setDeploymentBudget(this.pointsToPick);
        if(this.budgetStrategy instanceof ConstantBudgetStrategy) {
            ((ConstantBudgetStrategy)this.budgetStrategy).setConstantBudget(
                    (this.configuration.containsKey("budget.constant.coefficient")?
                    new Integer(this.configuration.get("budget.constant.coefficient")):DEFAULT_BUDGET_CONSTANT_COEFFICIENT));
        } else if(this.budgetStrategy instanceof StandardTreeBudgetStrategy) {      // this also implies GreedyBudgetStrategy
            ((StandardTreeBudgetStrategy) this.budgetStrategy).setTreeCoefficient(
                    this.configuration.containsKey("budget.tree.coefficient")?
                            new Double(this.configuration.get("budget.tree.coefficient")):DEFAULT_BUDGET_TREE_COEFFICIENT);
            ((StandardTreeBudgetStrategy) this.budgetStrategy).setTreeLength(
                    this.configuration.containsKey("budget.tree.length")?
                            new Integer(this.configuration.get("budget.tree.length")):DEFAULT_BUDGET_TREE_LENGTH);
        }

        this.budgetStrategy.configure();

    }

    private void partitionSpace() {
        RegressionAnalyzer analyzer = new RegressionAnalyzer();
        List<OutputSpacePoint> pointsToAnalyze = AbstractPartitioner.filterPoints(this.outputSpacePoints, this.regionTree.getCurrent().getRegion());
//        System.out.println("Points to analyze: "+pointsToAnalyze.size()+" out of "+this.outputSpacePoints.size());
        analyzer.setPointsToAnalyze(pointsToAnalyze);
        analyzer.analyze();
        String dimensionsToSplit = analyzer.getDimensionOrdering()[0];
        ((SpecificRegionTreeNode)this.regionTree.getCurrent()).setAnalyzer(analyzer);

        // configure partitioner
        AbstractPartitioner partitioner = null;
        try {
            String className = (this.configuration.containsKey("partitioner.class") ?
                    this.configuration.get("partitioner.class") : DEFAULT_PARTITIONER_CLASS);
            partitioner = (AbstractPartitioner) Class.forName(className).newInstance();
        } catch (InstantiationException |IllegalAccessException | ClassNotFoundException e ) {
            e.printStackTrace();
        }
        partitioner.setRanges(this.regionTree.getCurrent().getRegion());
        if(partitioner instanceof SingleDimensionPartitioner) {
            ((SingleDimensionPartitioner) partitioner).setDimensionKey(dimensionsToSplit);
        }
        partitioner.configurePartitioner();
        HashMap<String, List<Double>> high = partitioner.getHigherRegion(), low = partitioner.getLowerRegion();
        if(high !=null && low !=null) {
            this.regionTree.addChild(new SpecificRegionTreeNode(high));
            this.regionTree.addChild(new SpecificRegionTreeNode(low));
        } else {
            System.out.println("One of the two is null");
        }
    }
}