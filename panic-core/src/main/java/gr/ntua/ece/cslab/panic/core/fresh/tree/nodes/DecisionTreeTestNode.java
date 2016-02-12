package gr.ntua.ece.cslab.panic.core.fresh.tree.nodes;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

/**
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTreeTestNode extends DecisionTreeNode {
    private final String attribute;
    private final double value;
    private DecisionTreeNode leftChild, rightChild;

    /**
     * Constructor of the test node
     * @param attribute
     * @param value
     * @param leftChild
     * @param rightChild
     */
    public DecisionTreeTestNode(String attribute, double value, DecisionTreeNode leftChild, DecisionTreeNode rightChild) {
        this.attribute = attribute;
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public String getAttribute() {
        return attribute;
    }

    public double getValue() {
        return value;
    }

    public DecisionTreeNode getLeftChild() {
        return leftChild;
    }

    public DecisionTreeNode getRightChild() {
        return rightChild;
    }

    public void setLeftChild(DecisionTreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(DecisionTreeNode rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * Test the point and return left or right child
     * @param p
     * @return
     */
    public DecisionTreeNode test(OutputSpacePoint p) {
        if(p.getInputSpacePoint().getValue(this.attribute) <= this.value)
            return leftChild;
        else
            return rightChild;
    }
}
