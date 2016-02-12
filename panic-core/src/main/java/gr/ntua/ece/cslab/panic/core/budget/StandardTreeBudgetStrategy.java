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

import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;

/**
 * Estimates the budget using the sum relation. The budget for each tree level is obtained
 * by multiplying the father level by a constant c.
 * 
 * @author Giannis Giannakopoulos
 *
 */
public class StandardTreeBudgetStrategy extends AbstractBudgetStrategy{

	private Double treeCoefficient;
	private Integer treeLength;
	
	// aux variables
	private Integer treeRootBudget;
	public StandardTreeBudgetStrategy() {
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
		return (int)(Math.round(Math.pow(this.treeCoefficient, order)*this.treeRootBudget/Math.pow(2, order)));
	}
	
}
