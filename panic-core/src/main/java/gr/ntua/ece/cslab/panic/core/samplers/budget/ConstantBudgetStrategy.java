package gr.ntua.ece.cslab.panic.core.samplers.budget;

import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

public class ConstantBudgetStrategy extends AbstractBudgetStrategy {

	private Integer constantBudget ;
	public ConstantBudgetStrategy() {
		super();
	}
	
	
	public Integer getConstantBudget() {
		return constantBudget;
	}


	public void setConstantBudget(Integer constantBudget) {
		this.constantBudget = constantBudget;
	}


	@Override
	public void configure() {
		// does nothing else;
	}

	@Override
	public Integer estimateBudget(RegionTreeNode currentTreeNode) {
		return constantBudget;
	}

}
