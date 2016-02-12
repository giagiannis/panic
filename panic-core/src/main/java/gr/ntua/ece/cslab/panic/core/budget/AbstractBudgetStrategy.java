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

package gr.ntua.ece.cslab.panic.core.budget;

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