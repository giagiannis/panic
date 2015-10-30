package gr.ntua.ece.cslab.panic.core.samplers.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * A binary tree containing the ordering of the deployment subspaces 
 */
public class RegionTree {
	
	private RegionTreeNode root=null, currentNode=null;
	private List<RegionTreeNode> list;
	private int listIndex=-1;
	
	public RegionTree() {
		this.list = new ArrayList<>();
	}
	
	/**
	 * Adds a child under the specified node
	 * @param currentNode
	 * @param region
	 */
	public void addChild(RegionTreeNode currentNode, HashMap<String, List<Double>> region) {
		RegionTreeNode newNode = new RegionTreeNode();
		newNode.setRegion(region);
		if(currentNode==null) {
			if(this.root!=null) {
				System.err.println("Overwriting the root!");
			}
			this.root = newNode;
			this.root.setLevel(0);
			this.currentNode = this.root;
		} else {
			newNode.setLevel(currentNode.getLevel()+1);
			if(currentNode.getLeftChild()==null){
				currentNode.setLeftChild(newNode);
			} else if(currentNode.getRightChild()==null) {
				currentNode.setRightChild(newNode);
			} else {
				System.err.println("Cannot append third child into a binary tree!!");
				System.exit(1);
			}
		}
		this.list.add(newNode);
	}
	
	/**
	 * Adds a child under the currentNode of the struct 
	 * @param region
	 */
	public void addChild(HashMap<String, List<Double>> region) {
		this.addChild(this.currentNode, region);
	}
	
	/**
	 * Returns the current {@link RegionTreeNode}
	 * @return
	 */
	public RegionTreeNode getCurrent() {
		return this.currentNode;
	}
	
	/**
	 * Moves the pointer to the next {@link RegionTreeNode}
	 */
	public void next() {
		this.listIndex+=1;
		this.currentNode = this.list.get(this.listIndex);
	}
	
}
