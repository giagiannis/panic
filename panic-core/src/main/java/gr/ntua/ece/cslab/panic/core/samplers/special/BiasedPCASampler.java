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
import gr.ntua.ece.cslab.panic.core.samplers.partitioners.RangeBisectionPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;

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

	public BiasedPCASampler() {
		super();
		this.regionTree = new RegionTree();
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
			sample = this.unbiasedSampler.next();
		} else if (this.biasedSampler != null && this.biasedSampler.hasMore()) {
			sample = this.biasedSampler.next();
		} else {
			// revalidation phase
			this.splitRegion();
			this.regionTree.next();
			this.configureBiasedSampler();
			sample = this.biasedSampler.next();
		}
		return sample;
	}

	private void configureUnbiasedSampler() {
		this.unbiasedSampler = new LatinHypercubeSampler();
		this.unbiasedSampler.setDimensionsWithRanges(this.ranges);
		this.unbiasedSampler.setPointsToPick(this.budgetStrategy.estimateBudget(this.regionTree.getCurrent()));
		this.unbiasedSampler.configureSampler();
	}

	private void configureBiasedSampler() {
		LoadingsAnalyzer analyzer = this.performPCA(this.regionTree.getCurrent().getRegion());
		Integer pointsToPick=this.budgetStrategy.estimateBudget(this.regionTree.getCurrent());
		this.regionTree.getCurrent().setLoadingsAnalyzer(analyzer);
		this.regionTree.getCurrent().setBudget(pointsToPick);
		
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

	}

	private void configureBudgetStrategy(String budgetStrategyName) {
		try {
			this.budgetStrategy = (AbstractBudgetStrategy) Class.forName(budgetStrategyName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.budgetStrategy.setRegionTree(this.regionTree);
		this.budgetStrategy.setDeploymentBudget(this.pointsToPick);

		if (this.budgetStrategy instanceof ConstantTreeLevelMultiplierBudgetStrategy) {
			Integer treeLength = 0;
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

		RangeBisectionPartitioner partitioner = new RangeBisectionPartitioner();
		partitioner.setRanges(this.regionTree.getCurrent().getRegion());
		partitioner.setDimensionKey(ordering[0]);
		partitioner.configure();

		if (partitioner.getHigherRegionRanges() != null
				&& RangeBisectionPartitioner.filterPoints(this.outputSpacePoints, partitioner.getHigherRegionRanges())
						.size() >= this.ranges.size()) {
			this.regionTree.addChild(partitioner.getHigherRegionRanges());
		}

		if (partitioner.getLowerRegionRanges() != null
				&& RangeBisectionPartitioner.filterPoints(this.outputSpacePoints, partitioner.getLowerRegionRanges())
						.size() >= this.ranges.size()) {
			this.regionTree.addChild(partitioner.getLowerRegionRanges());
		}
	}

	private LoadingsAnalyzer performPCA(Map<String, List<Double>> currentRange) {
		PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
		List<OutputSpacePoint> data = RangeBisectionPartitioner.filterPoints(outputSpacePoints, currentRange);
		analyzer.setInputData(data);
		try {
			analyzer.calculateVarianceMatrix();
			analyzer.calculateCorrelationMatrix();
			// analyzer.calculateBaseWithVarianceMatrix();
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
