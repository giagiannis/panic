package gr.ntua.ece.cslab.panic.core.samplers.budget;

import java.util.HashMap;
import java.util.List;

import gr.ntua.ece.cslab.panic.core.analyzers.deprec.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

/**
 * This class implement the Greedy Budget Strategy, which focuses on increasing or decreasing 
 * the sample points between different a sub-region and its father. 
 * @author Giannis Giannakopoulos
 *
 */
public class GreedyBudgetStrategy extends ConstantTreeLevelMultiplierBudgetStrategy {
	private final HashMap<Integer, Double> distanceCache, sumCache;

	public GreedyBudgetStrategy() {
		this.distanceCache = new HashMap<>();
		this.sumCache = new HashMap<>();
	}
	
	@Override
	public void configure() {
		super.configure();
	}

	@Override
	public Integer estimateBudget(RegionTreeNode currentTreeNode) {
		Integer levelBudget = (int) Math.round(super.estimateBudget(currentTreeNode)*Math.pow(2, currentTreeNode.getLevel()));
		if(!distanceCache.containsKey(currentTreeNode.hashCode())){ //cache not populated
			List<RegionTreeNode> nodes = this.regionTree.getNodesByLevel(currentTreeNode.getLevel());
			double sum=0.0;
			for(RegionTreeNode n: nodes) {
				double delta = this.deltaState(n);
				this.distanceCache.put(n.hashCode(), delta);
				sum+=delta;
			}
			this.sumCache.put(currentTreeNode.getLevel(), sum);
		}
		double ratio = this.distanceCache.get(currentTreeNode.hashCode())/this.sumCache.get(currentTreeNode.getLevel());
		int points = (int)Math.round(levelBudget*ratio);
		points=(points>0?points:1);
		System.out.format("Ratio: %.5f (%d), (%d) --> points: %d\n", ratio, currentTreeNode.getLevel(), currentTreeNode.hashCode(), points);
		return points;
	}
	
	/**
	 * Method used to quantify the delta between the current sub-region and its father
	 * @param node
	 * @return
	 */
	private double deltaState(RegionTreeNode node) {
		if(node.isRoot()) {
			return Double.MAX_VALUE;
		}
		LoadingsAnalyzer fatherAnalyzer = node.getFather().getLoadingsAnalyzer(), currentAnalyzer=node.getLoadingsAnalyzer();
		double sum=0.0;
		for(int i=0;i<fatherAnalyzer.getDimensionLabels().length-1;i++) {
			sum+=Math.abs(fatherAnalyzer.getDistance(i)-currentAnalyzer.getDistance(i));
		}
		return sum;
	}
}
