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

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Decision Tree
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTree {
    private DecisionTreeNode root;

    private List<DecisionTreeLeafNode> leaves;
    private boolean leavesChanged = true;


    public DecisionTree(DeploymentSpace space) {
        this.root = new DecisionTreeLeafNode(new LinkedList<>(), space);
        this.leaves = new LinkedList<>();
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
        this.getLeaf(point).getPoints().add(point);
    }


    /**
     * Returns the leaves of the DecisionTree. This methods retains a cache: if no new nodes have been created, then
     * the leaves are returned from the cache, else the leaves are re-calculated and returned.
     * @return the leaves of the tree
     */
    public List<DecisionTreeLeafNode> getLeaves() {
//            this.leaves.clear();

        List<DecisionTreeLeafNode> leaves = new LinkedList<>();
        List<DecisionTreeNode> toVisit = new LinkedList<>();
        toVisit.add(this.root);
        while (!toVisit.isEmpty()) {
            DecisionTreeNode n = toVisit.remove(0);
            if(!n.isLeaf()) {
                toVisit.add(n.castToTest().getLeftChild());
                toVisit.add(n.castToTest().getRightChild());
            } else {
                leaves.add(n.castToLeaf());
            }
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
     * Returns the leaf in which the specified point belongs.
     * @param point the output space point to search
     * @return the leaf that this point belongs into
     */
    public DecisionTreeLeafNode getLeaf(InputSpacePoint point) {
        return this.getLeaf(new OutputSpacePoint(point, 0.0, "y"));
    }

    /**
     * Function used to replace an old tree node with a new tree node. Use this function to build the tree and convert
     * leaves into test nodes.
     * @param oldNode the old node
     * @param newNode the new node, to replace the former one
     */
    public final void replaceNode(DecisionTreeNode oldNode, DecisionTreeNode newNode) {
        this.leavesChanged = true;
        DecisionTreeNode father = oldNode.getFather();
        if(father == null) { // we need to change the root here
            this.root = newNode;
        } else if(father.castToTest().getLeftChild() == oldNode) {
            father.castToTest().setLeftChild(newNode);
        } else if(father.castToTest().getRightChild() == oldNode) {
            father.castToTest().setRightChild(newNode);
        } else {
            System.err.println("oldnode says: my father does not recognize me :(");
            System.exit(1);
        }
    }


    public final List<OutputSpacePoint> getSamples() {
        List<OutputSpacePoint> points = new LinkedList<>();
        for(DecisionTreeLeafNode leaf : this.getLeaves()) {
            points.addAll(leaf.getPoints());
        }
        return points;
    }

    @Override
    public String toString() {
        String buffer = "";
        List<DecisionTreeNode> toVisit = new LinkedList<>();
        toVisit.add(this.root);
        while (!toVisit.isEmpty()) {
            DecisionTreeNode n = toVisit.remove(0);
            if(!n.isLeaf()) {
                toVisit.add(0,n.castToTest().getLeftChild());
                toVisit.add(0,n.castToTest().getRightChild());
            }
            DecisionTreeNode t = n;
            while(t!=root) {
                t = t.getFather();
                buffer+="    ";
            }
            if(n.isLeaf()) {
                buffer+=String.format("%s (%d) [%s]\n", n.getId(),n.castToLeaf().getPoints().size(), n.treePath());
            } else {
                buffer += String.format("%s (%s <> %.2f) [%s]\n", n.getId(), n.castToTest().getAttribute(), n.castToTest().getValue(), n.treePath());
            }
        }
        return buffer.substring(0,buffer.length()-1);
    }

    public DecisionTree clone() {
        DecisionTree tree = new DecisionTree(this.root.getDeploymentSpace());
        Map<DecisionTreeNode, DecisionTreeNode> mapping = new HashMap<>();
        List<DecisionTreeNode> nodes = new LinkedList<>();
        nodes.add(root);

        while(!nodes.isEmpty()) {
            DecisionTreeNode current = nodes.remove(0);
            mapping.put(current, current.clone());
            if(!current.isLeaf()) {
                nodes.add(current.castToTest().getLeftChild());
                nodes.add(current.castToTest().getRightChild());
            }
        }
        for(DecisionTreeNode original : mapping.keySet()) {
            if(original.getFather()==null){
                tree.root = mapping.get(original);
            }
            if(!original.isLeaf()) {
                DecisionTreeNode left = original.castToTest().getLeftChild();
                DecisionTreeNode right = original.castToTest().getRightChild();

                DecisionTreeNode newNode = mapping.get(original);
                DecisionTreeNode newNodeLeft = mapping.get(left);
                DecisionTreeNode newNodeRight = mapping.get(right);
                newNode.castToTest().setLeftChild(newNodeLeft);
                newNode.castToTest().setRightChild(newNodeRight);
            }
        }
        return tree;
    }
}
