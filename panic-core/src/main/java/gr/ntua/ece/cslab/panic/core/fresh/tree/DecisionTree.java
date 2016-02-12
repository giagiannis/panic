package gr.ntua.ece.cslab.panic.core.fresh.tree;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;

import java.util.LinkedList;
import java.util.List;

/**
 * Decision Tree
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTree {
    private DecisionTreeNode root;

    private List<DecisionTreeNode> leaves;
    private boolean leavesChanged = false;

    public static class Configuration {
        public static String SEPARATOR_TYPE;
    }

    public DecisionTree() {
        this.root = new DecisionTreeLeafNode(new LinkedList<OutputSpacePoint>());
    }

    /**
     * Calls the DecisionTree.addPoint method many times.
     * @param points
     */
    public void addPoint(List<OutputSpacePoint> points) {
        for(OutputSpacePoint p : points) {
            this.addPoint(p);
        }
    }

    /**
     * Adds a new point into the leaf that the specified point should belong into.
     * @param point
     */
    public void addPoint(OutputSpacePoint point) {

    }


    /**
     * Returns the leaves of the DecisionTree. This methods retains a cache: if no new nodes have been created, then
     * the leaves are returned from the cache, else the leaves are re-calculated and returned.
     * @return
     */
    public List<DecisionTreeNode> getLeaves() {
        if(leavesChanged) {
            this.leaves.clear();
            List<DecisionTreeNode> toVisit = new LinkedList<>();
            toVisit.add(this.root);
            while (!toVisit.isEmpty()) {
                DecisionTreeNode n = toVisit.remove(0);
                if(!n.isLeaf()) {
                    toVisit.add(n.castToTest().getLeftChild());
                    toVisit.add(n.castToTest().getRightChild());
                } else {
                    this.leaves.add(n);
                }
            }
            leavesChanged = false;
        }
        return leaves;
    }


    /**
     * Returns the leaf in which the specified point belongs.
     * @param point
     * @return
     */
    public DecisionTreeLeafNode getLeaf(OutputSpacePoint point) {
        DecisionTreeNode current = this.root;
        while(!current.isLeaf()) {
            current=current.castToTest().test(point);
        }
        return current.castToLeaf();
    }

    /**
     * Function used to evaluate the leaves and
     */
    public void breakLeaves() {
        // FIXME: write the DecisionTree.breakLeaves method
    }


    // aux functions
    private void replaceNode(DecisionTreeNode oldNode, DecisionTreeNode newNode) {
        DecisionTreeNode father = oldNode.getFather();
        if(father.castToTest().getLeftChild() == oldNode) {
            father.castToTest().setLeftChild(newNode);
        } else if(father.castToTest().getRightChild() == oldNode) {
            father.castToTest().setRightChild(newNode);
        } else {
            System.err.println("oldnode says: my father does not recognize me :(");
            System.exit(1);
        }
    }
}
