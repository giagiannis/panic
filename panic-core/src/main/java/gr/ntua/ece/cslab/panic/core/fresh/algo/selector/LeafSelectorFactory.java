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

import java.util.Properties;
import java.util.Set;

/**
 * Factor class, responsible for the creation of the LeafSelector
 * Created by Giannis Giannakopoulos on 2/29/16.
 */
public class LeafSelectorFactory {

    public LeafSelector create(String type, DecisionTree tree, Set<String> forbidden, Properties properties) {
        LeafSelector selector = null;
        switch (type) {
            case "error":
                selector = new ErrorBasedSelector(tree, forbidden);
                break;
            case "regionerror":
                selector = new RegionErrorSelector(tree, forbidden);
                if(properties.containsKey("error.coefficient")) {
                    ((RegionErrorSelector)selector).setErrorCoefficient(new Double(properties.getProperty("error.coefficient")));
                }
                if(properties.containsKey("region.coefficient")) {
                    ((RegionErrorSelector)selector).setRegionCoefficient(new Double(properties.getProperty("region.coefficient")));
                }
                break;
            default:
                break;

        }
        return selector;
    }
}
