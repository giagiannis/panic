package gr.ntua.ece.cslab.panic.core.trees.structs;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

/**
 * Represents a test (intermediate - non terminal) node on the decision tree.
 * Created by Giannis Giannakopoulos on 1/22/16.
 */
public class DecisionTreeTestNode extends DecisionTreeNode {

    private String attribute;
    private double threshold;

    public DecisionTreeTestNode() {
        super();
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public DecisionTreeNode setRightChild(DecisionTreeNode node) {
        return this.rightChild;
    }

    public DecisionTreeNode getRightChild() {
        return this.rightChild;
    }

    public DecisionTreeNode setLeftChild(DecisionTreeNode node) {
        return this.leftChild;
    }

    public DecisionTreeNode getLeftChild() {
        return this.leftChild;
    }

    /**
     * Method used to navigate through the DecisionTree: the point is tested for the specified attribute
     * and if the value is less or equal to the specified threshold, the rightChild is returned, else the left one
     * @param point
     * @return
     */
    public DecisionTreeNode test(OutputSpacePoint point) {
        if(point.getInputSpacePoint().getValue(this.attribute) <= threshold)
            return this.rightChild;
        else
            return this.leftChild;
    }
}
