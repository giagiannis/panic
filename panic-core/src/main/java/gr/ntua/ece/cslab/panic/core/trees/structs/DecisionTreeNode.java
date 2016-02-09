package gr.ntua.ece.cslab.panic.core.trees.structs;

/**
 * Abstract object indicating a DecisionTreeNode. Contains a set of methods inherited to
 * DecisionTreeLeafNode and DecisionTreeTestNode.
 * Created by Giannis Giannakopoulos on 1/22/16.
 */
public abstract class DecisionTreeNode {
    protected DecisionTreeNode rightChild, leftChild;

    public DecisionTreeNode() {
        this.leftChild = null;
        this.rightChild = null;
    }

    /**
     * Returns true if node is leaf, else false
     * @return
     */
    public boolean isLeaf() {
        return (this.rightChild==null && this.leftChild==null);
    }

    /**
     * Casts the object to DecisionTreeLeafNode, else returns null
     * @return
     */
    public DecisionTreeLeafNode getDecisionTreeLeafNode() {
        return (this instanceof DecisionTreeLeafNode?(DecisionTreeLeafNode) this:null);
    }

    /**
     * Casts the object to DecisionTreeTestNode, else returns null
     * @return
     */
    public DecisionTreeTestNode getDecisionTreeTestNode() {
        return (this instanceof DecisionTreeTestNode?(DecisionTreeTestNode) this:null);
    }
}
