package gr.ntua.ece.cslab.panic.core.trees.structs;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A structure representing a decision tree.
 * Created by Giannis Giannakopoulos on 1/22/16.
 */
public abstract class DecisionTree {
    private DecisionTreeNode root;

    public DecisionTree() {
        this.root = new DecisionTreeLeafNode();
    }

    /**
     * Returns true of the DecisionTree is empty, else false
     * @return
     */
    public boolean isEmpty() {
        return(root.isLeaf()?root.getDecisionTreeLeafNode().isEmpty():false);
    }


    // decision tree interface

    /**
     * Method used to add new OutputSpacePoint to the Decision Tree.
     * @param point
     */
    public void add(OutputSpacePoint point) {
        DecisionTreeLeafNode leaf = this.findLeaf(point);
        leaf.add(point);
    }

    /**
     * Method that parses all the leaves and partitions them. If at least one leaf was partitioned that it is returned
     * true (indicative of non convergence), else false.
     * @return
     */
    public abstract boolean partition();

    /**
     * Returns the leaves of the tree.
     * @return
     */
    public List<DecisionTreeLeafNode> getLeaves() {
        List<DecisionTreeLeafNode> leaves = new LinkedList<>();
        List<DecisionTreeNode> toVisit = new LinkedList<>();
        while(!toVisit.isEmpty()) {
            DecisionTreeNode n = toVisit.remove(0);
            if(n.isLeaf()) {
                leaves.add(n.getDecisionTreeLeafNode());
            } else {
                toVisit.add(n.getDecisionTreeTestNode().getRightChild());
                toVisit.add(n.getDecisionTreeTestNode().getLeftChild());
            }
        }

        return leaves;
    }


    /**
     * Returns the leaf node that corresponds to this OutputSpacePoint
     * @param point
     * @return
     */
    public DecisionTreeLeafNode findLeaf(OutputSpacePoint point) {
        DecisionTreeNode current = this.root;
        while(!current.isLeaf()) {
            current=current.getDecisionTreeTestNode().test(point);
        }
        return current.getDecisionTreeLeafNode();
    }
}
