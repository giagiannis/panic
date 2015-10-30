package gr.ntua.ece.cslab.panic.core.samplers.utils;

import java.util.HashMap;
import java.util.List;

public class RegionTreeNode {
	
	private HashMap<String, List<Double>> region;
	private RegionTreeNode leftChild=null, rightChild=null;
	private RegionTreeNode father=null;
	private int level; 
	
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
	
	public boolean isLeaf() {
		return (this.leftChild==null && this.rightChild==null);
	}
	
	public boolean isRoot() {
		return this.father==null;
	}
}
