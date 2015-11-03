package gr.ntua.ece.cslab.panic.core.samplers.utils;

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
	private LoadingsAnalyzer loadingsAnalyzer;
	private Integer budget;
	
	public RegionTreeNode() {
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
	
	public LoadingsAnalyzer getLoadingsAnalyzer() {
		return loadingsAnalyzer;
	}
	
	public void setLoadingsAnalyzer(LoadingsAnalyzer loadingsAnalyzer) {
		this.loadingsAnalyzer = loadingsAnalyzer;
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
