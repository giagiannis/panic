package gr.ntua.ece.cslab.panic.core.samplers.special;

import com.mysql.jdbc.NotImplemented;
import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.analyzers.RegressionAnalyzer;
import gr.ntua.ece.cslab.panic.core.budget.AbstractBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.budget.ConstantBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.budget.GreedyBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.budget.StandardTreeBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.partitioners.AbstractPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 12/15/15.
 */
public class TreePartitioningSampler extends AbstractAdaptiveSampler {

    // default conf options
    public static String DEFAULT_PARTITIONER_CLASS = "gr.ntua.ece.cslab.panic.core.partitioners.SingleDimensionPartitioner";
    public static String DEFAULT_BUDGET_CLASS = "gr.ntua.ece.cslab.panic.core.budget.StandardTreeBudgetStrategy";
    public static String DEFAULT_SAMPLER_BIASED_CLASS = "gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler";
    public static String DEFAULT_SAMPLER_UNBIASED_CLASS = "gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler";
    public static Integer DEFAULT_BUDGET_TREE_LENGTH = 2;
    public static Double DEFAULT_BUDGET_TREE_COEFFICIENT = 1.0;
    public static Integer DEFAULT_BUDGET_CONSTANT_COEFFICIENT = 10;


    // objects of the sampler
    private RegionTree tree;
    protected AbstractSampler sampler;
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
        this.configurationsParameters.put("sampler.biased.class", "define the class of the biased sampler");
        this.configurationsParameters.put("sampler.unbiased.class", "define the class of the unbiasedsampler");
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        // configure tree object
        this.tree.addChild(new SpecificRegionTreeNode(this.ranges));
        this.tree.next();
        this.configureBudgetStrategy();
        this.configureUnbiasedSampler();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        if (!this.sampler.hasMore()) {// reconfiguration is needed
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
        this.sampler.setDimensionsWithRanges(this.tree.getCurrent().getRegion());
        this.sampler.setSamplingRate(this.samplingRate);
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
    }

    private void configureBudgetStrategy()  {
        String className = (this.configuration.containsKey("budget.class") ?
                this.configuration.get("budget.class") : DEFAULT_BUDGET_CLASS);
        try {
            this.budgetStrategy = (AbstractBudgetStrategy) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.budgetStrategy.setRegionTree(this.tree);
        this.budgetStrategy.setDeploymentBudget(this.pointsToPick);
        if(this.budgetStrategy instanceof ConstantBudgetStrategy) {
            ((ConstantBudgetStrategy)this.budgetStrategy).setConstantBudget(
                    (this.configuration.containsKey("budget.constant.coefficient")?
                    new Integer(this.configuration.get("budget.constant.coefficient")):DEFAULT_BUDGET_CONSTANT_COEFFICIENT));
        } else if(this.budgetStrategy instanceof GreedyBudgetStrategy) {

        } else if(this.budgetStrategy instanceof StandardTreeBudgetStrategy) {

        }

    }
}