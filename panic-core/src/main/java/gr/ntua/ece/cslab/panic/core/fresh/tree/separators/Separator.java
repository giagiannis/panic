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

package gr.ntua.ece.cslab.panic.core.fresh.tree.separators;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeTestNode;

import java.util.List;

/**
 * Abstract class dictating the API of the Separator classes and holding the implementation
 * of the common functionality.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public abstract class Separator {
    protected DecisionTreeLeafNode original;
    protected DecisionTreeTestNode result;

    public Separator(DecisionTreeLeafNode original) {
        this.original = original;
    }

    public abstract void separate();

    public DecisionTreeTestNode getResult() {
        return result;
    }
}
