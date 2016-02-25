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

import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;

import java.util.UUID;

/**
 * The base class, used for all the decision tree nodes.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public abstract class DecisionTreeNode {
    protected DecisionTreeNode father;
    protected DeploymentSpace deploymentSpace;
    protected String id;

    public DecisionTreeNode() {
        this.id = UUID.randomUUID().toString();
    }

    public DeploymentSpace getDeploymentSpace() {
        return deploymentSpace;
    }

    public void setDeploymentSpace(DeploymentSpace deploymentSpace) {
        this.deploymentSpace = deploymentSpace;
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

    public String getId() {
        return this.id;
    }
    protected abstract String toString(String pad);

    public DecisionTreeNode clone() {
        if(this instanceof DecisionTreeLeafNode){
            return ((DecisionTreeLeafNode)this).clone();
        } else {
            return ((DecisionTreeTestNode)this).clone();
        }

    }
}
