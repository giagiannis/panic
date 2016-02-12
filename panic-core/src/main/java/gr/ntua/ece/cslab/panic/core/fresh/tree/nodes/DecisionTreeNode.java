package gr.ntua.ece.cslab.panic.core.fresh.tree.nodes;

/**
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public abstract class DecisionTreeNode {
    protected DecisionTreeNode father;

    public DecisionTreeNode() {

    }

    public DecisionTreeNode getFather() {
        return father;
    }

    public boolean isLeaf() {
        return (this instanceof DecisionTreeLeafNode);
    }

    public DecisionTreeLeafNode castToLeaf() {
        return (DecisionTreeLeafNode) this;
    }

    public DecisionTreeTestNode castToTest() {
        return (DecisionTreeTestNode) this;
    }
}
