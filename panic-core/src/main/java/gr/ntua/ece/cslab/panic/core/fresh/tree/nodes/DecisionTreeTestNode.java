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
import gr.ntua.ece.cslab.panic.core.fresh.tree.line.SplitLine;

import java.util.LinkedList;
import java.util.List;

/**
 * Class representing and intermediate - test node.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTreeTestNode extends DecisionTreeNode {
    private final SplitLine splitLine;
    private DecisionTreeNode leftChild, rightChild;

    public DecisionTreeTestNode(SplitLine line, DeploymentSpace space) {
        super();
        this.splitLine = line;
        this.deploymentSpace = space;
    }

    public DecisionTreeTestNode(SplitLine line, DeploymentSpace space, String id) {
        this(line, space);
        this.id = id;
    }

    public DecisionTreeTestNode(SplitLine line, DeploymentSpace space, DecisionTreeNode leftChild, DecisionTreeNode rightChild) {
        this(line, space);
        this.setLeftChild(leftChild);
        this.setRightChild(rightChild);
    }

    public DecisionTreeTestNode(SplitLine line,  DeploymentSpace space, DecisionTreeNode leftChild, DecisionTreeNode rightChild, String id) {
        this(line, space, leftChild, rightChild);
        this.id = id;
    }

    public SplitLine getSplitLine() {
        return splitLine;
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
        int c = this.splitLine.comparePoint(p.getInputSpacePoint());
        if(c==-1 || c==0)
            return leftChild;
        else
            return rightChild;
    }

    /**
     * Returns the samples for the specified subtree
     * @return
     */
    public List<OutputSpacePoint> getSamples() {
        List<OutputSpacePoint> points = new LinkedList<>();
        List<DecisionTreeNode> toVisit = new LinkedList<>();
        toVisit.add(this);
        while(!toVisit.isEmpty()) {
            DecisionTreeNode current = toVisit.remove(0);
            if(current.isLeaf()) {
                points.addAll(current.castToLeaf().getPoints());
            } else {
                toVisit.add(current.castToTest().getLeftChild());
                toVisit.add(current.castToTest().getRightChild());
            }
        }
        return points;
    }
    @Override
    public String toString() {
        return this.toString("");
    }

    protected String toString(String pad) {
        return String.format("%s%s(%s)\n%s\n%s",
                pad,
                this.id,
                this.splitLine,
                this.leftChild.toString(pad+"\t"),
                this.rightChild.toString(pad+"\t"));
    }

    public DecisionTreeTestNode clone() {
        DecisionTreeTestNode test = new DecisionTreeTestNode(this.splitLine, this.deploymentSpace, this.id);
        return  test;
    }
}
