/*
 * Copyright 2016 Giannis Giannakopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gr.ntua.ece.cslab.panic.core.fresh.tree.nodes;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;

/**
 * Class representing and intermediate - test node.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTreeTestNode extends DecisionTreeNode {
    private final String attribute;
    private final double value;
    private DecisionTreeNode leftChild, rightChild;

    /**
     * Default construction
     * @param attribute the split dimension name
     * @param value the split dimension value
     */
    public DecisionTreeTestNode(String attribute, double value, DeploymentSpace space) {
        this.attribute = attribute;
        this.value = value;
        this.deploymentSpace = space;
    }

    /**
     * Constructor of the test node
     * @param attribute the split dimension name
     * @param value the split dimension value
     * @param leftChild the left child of the test node
     * @param rightChild the right child of the test node
     */

    public DecisionTreeTestNode(String attribute, double value, DeploymentSpace space, DecisionTreeNode leftChild, DecisionTreeNode rightChild) {
        super();
        this.attribute = attribute;
        this.value = value;
        this.setLeftChild(leftChild);
        this.setRightChild(rightChild);;
        this.deploymentSpace = space;
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
        this.leftChild.father = this;
    }

    public void setRightChild(DecisionTreeNode rightChild) {
        this.rightChild = rightChild;
        this.rightChild.father = this;
    }

    /**
     * Test the point and return left or right child
     * @param p the OutputSpacePoint to test
     * @return returns the correct child that p belongs into (may be an intermediate node)
     */
    public DecisionTreeNode test(OutputSpacePoint p) {
        if(p.getInputSpacePoint().getValue(this.attribute) <= this.value)
            return leftChild;
        else
            return rightChild;
    }

    @Override
    public String toString() {
        return this.toString("");
    }

    protected String toString(String pad) {
        return String.format("%s(%s, %.2f)\n%s\n%s",
                pad,
                this.attribute,
                this.value,
                this.leftChild.toString(pad+"\t"),
                this.rightChild.toString(pad+"\t"));
    }
}
