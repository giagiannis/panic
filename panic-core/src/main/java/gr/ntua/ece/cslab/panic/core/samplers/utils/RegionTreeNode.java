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

package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.core.analyzers.deprec.LoadingsAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class RegionTreeNode {
	
	// tree navigation fields
	private RegionTreeNode leftChild=null, rightChild=null;
	private RegionTreeNode father=null;
	
	// node properties
	private int level; 
	private HashMap<String, List<Double>> region;
	private Integer budget;
	
	public RegionTreeNode() {
	}

	public RegionTreeNode(HashMap<String, List<Double>> region) {
		this.region = region;
	}

	public HashMap<String, List<Double>> getRegion() {
		return region;
	}

	public void setRegion(HashMap<String, List<Double>> region) {
		this.region = region;
	}

	public RegionTreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(RegionTreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public RegionTreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(RegionTreeNode rightChild) {
		this.rightChild = rightChild;
	}

	public RegionTreeNode getFather() {
		return father;
	}

	public void setFather(RegionTreeNode father) {
		this.father = father;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setBudget(Integer budget) {
		this.budget = budget;
	}
	
	public Integer getBudget() {
		return budget;
	}
	
	public boolean isLeaf() {
		return (this.leftChild==null && this.rightChild==null);
	}
	
	public boolean isRoot() {
		return this.father==null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.hashCode());
		builder.append(' ');
		for(Entry<String, List<Double>> kv:this.region.entrySet()) {
			builder.append(kv.getKey());
			builder.append(":");
			builder.append(kv.getValue().size());
			builder.append(" ");
		}
//		builder.append(' ');
		builder.append('(');
		builder.append(this.level);
		builder.append(')');
		return builder.toString();
	}
}
