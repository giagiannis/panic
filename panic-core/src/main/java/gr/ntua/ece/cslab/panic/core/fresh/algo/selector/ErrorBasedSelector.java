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

import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithm;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.util.Set;

/**
 * Created by Giannis Giannakopoulos on 2/29/16.
 */
public class ErrorBasedSelector extends LeafSelector {

    private DecisionTreeLeafNode leaf;
    /**
     * Default constructor
     *
     * @param tree      the tree
     * @param forbidden set containing leaves that are not applicable
     */
    public ErrorBasedSelector(DecisionTree tree, Set<String> forbidden) {
        super(tree, forbidden);
        double maxError = 0.0;
        DecisionTreeLeafNode leaf = null;
        for(DecisionTreeLeafNode l : tree.getLeaves()) {
            double currentError = DTAlgorithm.meanSquareError(l);
            if((!this.forbiddenTreePaths.contains(l.treePath())) && (currentError>maxError || leaf==null)) {
                leaf = l;
                maxError = currentError;
            }
        }
        this.leaf = leaf;
    }

    @Override
    public DecisionTreeLeafNode getLeaf() {
        return this.leaf;
    }

}
