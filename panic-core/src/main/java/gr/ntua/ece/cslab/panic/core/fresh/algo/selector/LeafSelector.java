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

package gr.ntua.ece.cslab.panic.core.fresh.algo.selector;

import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.util.Set;

/**
 * Class used to select a leaf from the available ones.
 * Created by Giannis Giannakopoulos on 2/29/16.
 */
public abstract class LeafSelector {

    protected final DecisionTree tree;
    protected final Set<String> forbiddenTreePaths;

    /**
     * Default constructor
     * @param tree the tree
     * @param forbidden set containing leaves that are not applicable
     */
    public LeafSelector(DecisionTree tree, Set<String> forbidden) {
        this.tree = tree;
        this.forbiddenTreePaths = forbidden;
    }

    public abstract DecisionTreeLeafNode getLeaf();
}
