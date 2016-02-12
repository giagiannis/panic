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

    private List<DecisionTreeLeafNode> leaves;
    private boolean leavesChanged = false;


    public DecisionTree() {
        this.root = new DecisionTreeLeafNode(new LinkedList<OutputSpacePoint>());
    }

    /**
     * Calls the DecisionTree.addPoint method many times.
     * @param points the points to add
     */
    public void addPoint(List<OutputSpacePoint> points) {
        for(OutputSpacePoint p : points) {
            this.addPoint(p);
        }
    }

    /**
     * Adds a new point into the leaf that the specified point should belong into.
     * @param point the point to add
     */
    public void addPoint(OutputSpacePoint point) {

    }


    /**
     * Returns the leaves of the DecisionTree. This methods retains a cache: if no new nodes have been created, then
     * the leaves are returned from the cache, else the leaves are re-calculated and returned.
     * @return the leaves of the tree
     */
    public List<DecisionTreeLeafNode> getLeaves() {
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
                    this.leaves.add(n.castToLeaf());
                }
            }
            leavesChanged = false;
        }
        return leaves;
    }


    /**
     * Returns the leaf in which the specified point belongs.
     * @param point the output space point to search
     * @return the leaf that this point belongs into
     */
    public DecisionTreeLeafNode getLeaf(OutputSpacePoint point) {
        DecisionTreeNode current = this.root;
        while(!current.isLeaf()) {
            current=current.castToTest().test(point);
        }
        return current.castToLeaf();
    }

    /**
     * Function used to replace an old tree node with a new tree node. Use this function to build the tree and convert
     * leaves into test nodes.
     * @param oldNode the old node
     * @param newNode the new node, to replace the former one
     */
    public final void replaceNode(DecisionTreeNode oldNode, DecisionTreeNode newNode) {
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
