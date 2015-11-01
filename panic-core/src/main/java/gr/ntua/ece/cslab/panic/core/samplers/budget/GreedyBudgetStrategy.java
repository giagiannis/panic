package gr.ntua.ece.cslab.panic.core.samplers.budget;

import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

/**
 * This class implement the Greedy Budget Strategy, which focuses on increasing or decreasing 
 * the sample points between different a sub-region and its father. 
 * @author Giannis Giannakopoulos
 *
 */
public class GreedyBudgetStrategy extends AbstractBudgetStrategy {

	private Double coefficient;
	private Integer rootBudget;
	
	public GreedyBudgetStrategy() {
		
	}
	
	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}
	
	public Double getCoefficient() {
		return coefficient;
	}
	
	public void setRootBudget(Integer rootBudget) {
		this.rootBudget = rootBudget;
	}
	
	public Integer getRootBudget() {
		return rootBudget;
	}
	
	@Override
	public void configure() {
		// no need to do anything here, right?
	}

	@Override
	public Integer estimateBudget(RegionTreeNode currentTreeNode) {
		if(currentTreeNode.isRoot()) {
			return this.rootBudget;
		}
		this.compareAnalyzer(currentTreeNode.getFather().getLoadingsAnalyzer(), currentTreeNode.getLoadingsAnalyzer());
		return null;
	}
	
	/**
	 * Method used to compare the {@link LoadingsAnalyzer} objects of the 
	 * father and the current region. This implements the greedy criterion of 
	 * the budget strategy.
	 * @return
	 */
	private boolean compareAnalyzer(LoadingsAnalyzer fatherAnalyzer, LoadingsAnalyzer currentAnalyzer) {
		return true;
	}

}
