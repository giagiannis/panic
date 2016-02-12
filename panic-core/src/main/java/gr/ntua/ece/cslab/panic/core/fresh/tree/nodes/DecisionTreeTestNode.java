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
