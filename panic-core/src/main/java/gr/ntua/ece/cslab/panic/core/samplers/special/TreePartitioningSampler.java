package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.analyzers.RegressionAnalyzer;
import gr.ntua.ece.cslab.panic.core.budget.AbstractBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.partitioners.AbstractPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

/**
 * Created by Giannis Giannakopoulos on 12/15/15.
 */
public class TreePartitioningSampler extends AbstractAdaptiveSampler {

    // default conf options
    public static String DEFAULT_PARTITIONER_CLASS = "gr.ntua.ece.cslab.panic.core.partitioners.SingleDimensionPartitioner";
    public static String DEFAULT_BUDGET_CLASS = "gr.ntua.ece.cslab.panic.core.budget.StandardTreeBudgetStrategy";
    public static Integer DEFAULT_BUDGET_TREE_LENGTH=2;
    public static Double DEFAULT_BUDGET_TREE_COEFFICIENT=1.0;


    // objects of the sampler
    private RegionTree tree;
    private AbstractSampler sampler;
    private AbstractPartitioner partitioner;
    private AbstractBudgetStrategy budgetStrategy;

    public TreePartitioningSampler() {
        super();
        this.tree = new RegionTree();

        this.configurationsParameters.put("budget.class", "define the budget class to be used");
        this.configurationsParameters.put("budget.tree.coefficient", "define the coefficient of the tree budget strategies");
        this.configurationsParameters.put("budget.tree.length", "define the length of the tree budget strategies");
        this.configurationsParameters.put("budget.constant.coefficient", "define the coefficient of the constant budget strategy");
        this.configurationsParameters.put("partitioner.class", "define the partitioner class");
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        // add root tree node
        this.tree.addChild(null, this.ranges);
        // external properties



        // sampler initial configuration
        this.sampler = new LatinHypercubeSampler();
        this.tree.next();
        this.sampler.setDimensionsWithRanges(this.tree.getCurrent().getRegion());
        this.sampler.setSamplingRate(this.samplingRate);
        this.sampler.configureSampler();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        if(!this.sampler.hasMore()) {// reconfiguration is needed

        }

        return this.sampler.next();
    }



    public static class SpecificRegionTreeNode extends RegionTreeNode {
        private RegressionAnalyzer analyzer;

        public RegressionAnalyzer getAnalyzer() {
            return analyzer;
        }

        public void setAnalyzer(RegressionAnalyzer analyzer) {
            this.analyzer = analyzer;
        }
    }
}