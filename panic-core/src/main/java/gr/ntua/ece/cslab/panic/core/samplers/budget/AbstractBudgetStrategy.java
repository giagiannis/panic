package gr.ntua.ece.cslab.panic.core.samplers.budget;

import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

public abstract class AbstractBudgetStrategy {
	
	protected RegionTree regionTree;
	protected Integer deploymentBudget;
	
	
	public AbstractBudgetStrategy() {
		
	}
	
	public RegionTree getRegionTree() {
		return regionTree;
	}



	public void setRegionTree(RegionTree regionTree) {
		this.regionTree = regionTree;
	}



	public Integer getDeploymentBudget() {
		return deploymentBudget;
	}



	public void setDeploymentBudget(Integer deploymentBudget) {
		this.deploymentBudget = deploymentBudget;
	}



	// abstract methods to be implemented
	/**
	 * Method used to configure the Budget strategy
	 */
	public abstract void configure();
	
	
	/**
	 * Method used to return the budget that should be allocated for this exact current tree node
	 * @param currentTreeNode
	 * @return
	 */
	public abstract Integer estimateBudget(RegionTreeNode currentTreeNode);

}