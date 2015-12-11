package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.GridSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.samplers.TotalOrderingSampler;
import gr.ntua.ece.cslab.panic.core.samplers.budget.AbstractBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.samplers.budget.ConstantBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.samplers.budget.ConstantTreeLevelMultiplierBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.samplers.budget.GreedyBudgetStrategy;
import gr.ntua.ece.cslab.panic.core.partitioners.AbstractPartitioner;
import gr.ntua.ece.cslab.panic.core.partitioners.RandomPartitioner;
import gr.ntua.ece.cslab.panic.core.partitioners.SplitByDimensionPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class BiasedPCASampler extends AbstractAdaptiveSampler {

    private AbstractSampler unbiasedSampler;
    private AbstractSampler biasedSampler;
    private AbstractBudgetStrategy budgetStrategy;
    private final RegionTree regionTree;

    // params
    private String biasedSamplerClassName;
    private Integer treeLength;

    public BiasedPCASampler() {
        super();
        this.regionTree = new RegionTree();
        this.configurationsParameters.put("budget.strategy", "Budget strategy to be used");
        this.configurationsParameters.put("biased.sampler", "Biased sampler to be used");
        this.configurationsParameters.put("tree.length", "Length of tree to be created");
        this.configurationsParameters.put("tree.coefficient", "Coefficient of tree");
        this.configurationsParameters.put("budget.constant", "Constant value for constant budget strategy");
        this.configurationsParameters.put("partitioner", "Partitioner to be used");
    }

    @Override
    public void configureSampler() {
        super.configureSampler();

        this.regionTree.addChild(this.ranges);
        this.regionTree.next();

        String budgetStrategyName = "gr.ntua.ece.cslab.panic.core.samplers.budget.ConstantTreeLevelMultiplierBudgetStrategy";
        if (this.configuration.containsKey("budget.strategy")) {
            budgetStrategyName = this.configuration.get("budget.strategy");
        }
        this.configureBudgetStrategy(budgetStrategyName);
        this.configureUnbiasedSampler();

        if (this.configuration.containsKey("biased.sampler")) {
            this.biasedSamplerClassName = this.configuration.get("biased.sampler");
        } else {
            this.biasedSamplerClassName = "gr.ntua.ece.cslab.panic.core.samplers.GridSampler";
        }
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sample;
        if (this.unbiasedSampler.hasMore()) {
//            System.out.print("Unbiased sampler:\t\t");
            sample = this.unbiasedSampler.next();
        } else if (this.biasedSampler != null && this.biasedSampler.hasMore()) {
//            System.out.print("Biased sampler:\t\t");
            sample = this.biasedSampler.next();
        } else {
            // revalidation phase
            this.splitRegion();
            this.regionTree.next();
            this.configureBiasedSampler();
//            System.out.print("Revalidation phase (" + this.regionTree.getCurrent() + "):\t");
            sample = this.biasedSampler.next();
        }
//        System.out.println(sample);
        return sample;
    }

    public RegionTree getRegionTree() {
        return regionTree;
    }

    private void configureUnbiasedSampler() {
        this.unbiasedSampler = new LatinHypercubeSampler();
        this.unbiasedSampler.setDimensionsWithRanges(this.ranges);
        this.unbiasedSampler.setPointsToPick(this.budgetStrategy.estimateBudget(this.regionTree.getCurrent()));
        this.unbiasedSampler.configureSampler();
//		System.out.println("Budget to spent to unbiased: "+this.budgetStrategy.estimateBudget(this.regionTree.getCurrent()));
    }

    private void configureBiasedSampler() {
        LoadingsAnalyzer analyzer = this.regionTree.getCurrent().getLoadingsAnalyzer();
        this.regionTree.getCurrent().setBudget(pointsToPick);
        Integer pointsToPick = this.budgetStrategy.estimateBudget(this.regionTree.getCurrent());

        try {
            this.biasedSampler = (AbstractSampler) Class.forName(biasedSamplerClassName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.biasedSampler.setDimensionsWithRanges(this.regionTree.getCurrent().getRegion());

        if (this.biasedSampler instanceof TotalOrderingSampler) {
            ((TotalOrderingSampler) this.biasedSampler).setDimensionOrdering(analyzer.getInputDimensionsOrder());
        } else if (this.biasedSampler instanceof GridSampler) {
            HashMap<String, Double> coefficients = new HashMap<>();
            for (int i = 0; i < analyzer.getDimensionLabels().length - 1; i++) {
                double distance = (analyzer.getAngle(i) <= 90 ? analyzer.getDistance(i)
                        : analyzer.getDistanceSymmetric(i));
                String label = analyzer.getDimensionLabels()[i];
                if (distance == 0.0) {
                    System.err.println("Distance for " + label + " is zero!");
                    distance = 1.0;
                }
                coefficients.put(label, 1.0 / distance);
            }
            ((GridSampler) this.biasedSampler).setWeights(coefficients);
        }
        this.biasedSampler.setPointsToPick(pointsToPick);
        this.biasedSampler.configureSampler();
//		System.out.println("Budget to spent to biased: "+this.budgetStrategy.estimateBudget(this.regionTree.getCurrent()));

    }

    private void configureBudgetStrategy(String budgetStrategyName) {
        try {
            this.budgetStrategy = (AbstractBudgetStrategy) Class.forName(budgetStrategyName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.budgetStrategy.setRegionTree(this.regionTree);
        this.budgetStrategy.setDeploymentBudget(this.pointsToPick);

        if (this.budgetStrategy instanceof ConstantTreeLevelMultiplierBudgetStrategy || this.budgetStrategy instanceof GreedyBudgetStrategy) {
            treeLength = 0;
            if (this.configuration.containsKey("tree.length")) {
                treeLength = new Integer(this.configuration.get("tree.length"));
            } else {
                System.err.println("tree.length must be set!!");
                System.exit(1);
            }
            Double treeCoefficient = 0.0;
            if (this.configuration.containsKey("tree.coefficient")) {
                treeCoefficient = new Double(this.configuration.get("tree.coefficient"));
            } else {
                System.err.println("tree.coefficient to be set!!");
                System.exit(1);
            }
            ((ConstantTreeLevelMultiplierBudgetStrategy) this.budgetStrategy).setTreeCoefficient(treeCoefficient);
            ((ConstantTreeLevelMultiplierBudgetStrategy) this.budgetStrategy).setTreeLength(treeLength);
        } else if (this.budgetStrategy instanceof ConstantBudgetStrategy) {
            // FIXME: define treeLength
            Integer constantBudget = 0;
            if (this.configuration.containsKey("budget.constant")) {
                constantBudget = new Integer(this.configuration.get("budget.constant"));
            } else {
                System.err.println("budget.constant must be set!!");
                System.exit(1);
            }
            ((ConstantBudgetStrategy) this.budgetStrategy).setConstantBudget(constantBudget);
        }
        this.budgetStrategy.configure();
    }

    private void splitRegion() {
        LoadingsAnalyzer analyzer = this.performPCA(this.regionTree.getCurrent().getRegion());
        this.regionTree.getCurrent().setLoadingsAnalyzer(analyzer);	// setting analyzer for father
        String[] ordering = analyzer.getInputDimensionsOrder();
        
        if(this.regionTree.getCurrent().getLevel()>=this.treeLength-1) {
            return;
        }
        String partitionerClassName = "gr.ntua.ece.cslab.panic.core.partitioners.SplitByDimensionPartitioner";
        if (this.configuration.containsKey("partitioner")) {
            partitionerClassName = this.configuration.get("partitioner");
        }
//		System.out.println(partitionerClassName);

        AbstractPartitioner partitioner = null;
        try {
            partitioner = (AbstractPartitioner) Class.forName(partitionerClassName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        partitioner.setRanges(this.regionTree.getCurrent().getRegion());
        if (partitioner instanceof SplitByDimensionPartitioner) {
            ((SplitByDimensionPartitioner) partitioner).setDimensionKey(ordering[0]);
        } else if (partitioner instanceof RandomPartitioner) {
            // nothing is needed
        }
        partitioner.configurePartitioner();

        if (partitioner.getHigherRegion() != null
                && AbstractPartitioner.filterPoints(this.outputSpacePoints, partitioner.getHigherRegion())
                .size() >= this.ranges.size()) {
            RegionTreeNode n = this.regionTree.addChild(partitioner.getHigherRegion());
            n.setLoadingsAnalyzer(this.performPCA(n.getRegion()));
        }

        if (partitioner.getLowerRegion() != null
                && AbstractPartitioner.filterPoints(this.outputSpacePoints, partitioner.getLowerRegion())
                .size() >= this.ranges.size()) {
            RegionTreeNode n = this.regionTree.addChild(partitioner.getLowerRegion());
            n.setLoadingsAnalyzer(this.performPCA(n.getRegion()));
        }
    }

    private LoadingsAnalyzer performPCA(Map<String, List<Double>> currentRange) {
        PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
        List<OutputSpacePoint> data = AbstractPartitioner.filterPoints(outputSpacePoints, currentRange);
        analyzer.setInputData(data);
        try {
            analyzer.calculateVarianceMatrix();
            analyzer.calculateCorrelationMatrix();
            analyzer.calculateBaseWithDataMatrix();
        } catch (Exception ex) {
            Logger.getLogger(BiasedPCASampler.class.getName()).log(Level.SEVERE, null, ex);
        }
        int numberOfPC = 2;
        LoadingsAnalyzer loadingsAnalyzer = analyzer.getLoadingsAnalyzer(numberOfPC);
        loadingsAnalyzer.setPcWeights(analyzer.getPCWeights());
        return loadingsAnalyzer;
    }
}
