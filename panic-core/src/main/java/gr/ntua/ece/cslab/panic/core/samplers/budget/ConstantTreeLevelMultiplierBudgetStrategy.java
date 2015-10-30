package gr.ntua.ece.cslab.panic.core.samplers.budget;

import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

/**
 * Estimates the budget using the sum relation. The budget for each tree level is obtained
 * by multiplying the father level by a constant c.
 * 
 * @author Giannis Giannakopoulos
 *
 */
public class ConstantTreeLevelMultiplierBudgetStrategy extends AbstractBudgetStrategy{

	private Double treeCoefficient;
	private Integer treeLength;
	
	// aux variables
	private Integer treeRootBudget;
	public ConstantTreeLevelMultiplierBudgetStrategy() {
		super();
	}
	
	public Double getTreeCoefficient() {
		return treeCoefficient;
	}


	public void setTreeCoefficient(Double treeCoefficient) {
		this.treeCoefficient = treeCoefficient;
	}

	public Integer getTreeLength() {
		return treeLength;
	}

	public void setTreeLength(Integer treeLength) {
		this.treeLength = treeLength;
	}

	@Override
	public void configure() {
    	this.treeRootBudget = 0;
    	double sum=0.0;
    	for(int i=0;i<treeLength;i++) {
    		sum+=Math.pow(treeCoefficient, i);
    	}
    	this.treeRootBudget = (int)Math.round(deploymentBudget/sum);
	}

	@Override
	public Integer estimateBudget(RegionTreeNode currentTreeNode) {
		int order = currentTreeNode.getLevel();
		return (int)Math.round(Math.pow(this.treeCoefficient, order)*this.treeRootBudget/ Math.pow(2, order));
	}
	
}
