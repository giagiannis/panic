package gr.ntua.ece.cslab.panic.core.samplers.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/** 
 * A binary tree containing the ordering of the deployment subspaces 
 */
public class RegionTree {
	
	private RegionTreeNode root, currentNode;
	private final List<RegionTreeNode> nodesToVisit;
	private int listIndex;
	
	public RegionTree() {
		this.nodesToVisit = new ArrayList<>();
		this.root=null;
		this.currentNode=null;
		this.listIndex=-1;
	}
	
	/**
	 * Adds a child under the specified node
	 * @param currentNode
	 * @param region
	 */
	public RegionTreeNode addChild(RegionTreeNode currentNode, HashMap<String, List<Double>> region) {
		RegionTreeNode newNode = new RegionTreeNode();
		newNode.setRegion(region);
		newNode.setFather(currentNode);
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
		this.nodesToVisit.add(newNode);
		return newNode;
//		System.err.format("%50s added child %50s\n",currentNode,newNode);
	}
	
	/**
	 * Adds a child under the currentNode of the struct 
	 * @param region
	 */
	public RegionTreeNode addChild(HashMap<String, List<Double>> region) {
		return this.addChild(this.currentNode, region);
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
		this.currentNode = this.nodesToVisit.get(this.listIndex);
	}
	
	
	/**
	 * Returns the leaves of the tree
	 * @return
	 */
	public List<RegionTreeNode> getLeaves() {
		this.clearNotVisited();
		
		List<RegionTreeNode> toVisit = new LinkedList<>();
		List<RegionTreeNode> leaves = new LinkedList<>();
		toVisit.add(root);
		while(!toVisit.isEmpty()){
			RegionTreeNode current = toVisit.remove(0);
			if(current.isLeaf()) {
				leaves.add(current);
			} 
			if(current.getLeftChild()!=null) {
				toVisit.add(current.getLeftChild());
			}
			if(current.getRightChild()!=null) {
				toVisit.add(current.getRightChild());
			}
		}
//		System.out.println(leaves);
		return leaves;
	}
	
	public List<HashMap<String, List<Double>>> getLeafRegions() {
		this.clearNotVisited();
		List<HashMap<String, List<Double>>> leaves = new LinkedList<>();
		for(RegionTreeNode n:this.getLeaves())
			leaves.add(n.getRegion());
		return leaves;
	}
	
	private void clearNotVisited() {
		for(int i=this.listIndex+1;i<this.nodesToVisit.size();i++) {
			RegionTreeNode current = this.nodesToVisit.get(i);
			if(current.getLoadingsAnalyzer()==null) {
				RegionTreeNode father = current.getFather();
				father.setLeftChild(null);
				father.setRightChild(null);
			}
		}
		
	}
	
	public List<RegionTreeNode> getNodesByLevel(int level) {
		List<RegionTreeNode> result = new LinkedList<>();
		for(RegionTreeNode n:this.nodesToVisit) {
			if(n.getLevel()==level)
				result.add(n);
		}
		return result;
	}
	
}
