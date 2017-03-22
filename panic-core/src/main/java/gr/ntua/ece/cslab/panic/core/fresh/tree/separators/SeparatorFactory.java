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

import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.util.Properties;

/**
 * Class used to construct different separators.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class SeparatorFactory {

    public Separator create(String separatorType, DecisionTreeLeafNode node, Properties properties) {
        Separator separator;
        switch (separatorType) {
            case "variance":
                separator = new VarianceSeparator(node);
                break;
            case "modelerror":
                separator = new ModelErrorSeparator(node);
                break;
            case "sa":
                separator = new SASeparator(node);
                if(properties.getProperty("sa.script")!=null) {
                    ((SASeparator) separator).setScriptPath(properties.getProperty("sa.script"));
                } else {
                    System.err.println("You have to define the script parameter for sa");
                    System.exit(1);
                }
                break;
            default:
                separator = null;
                break;
        }
        if(properties.containsKey("splits.axisparallel")) {
            separator.setAxisParallelSplits(new Boolean(properties.getProperty("splits.axisparallel")));
        }
        return separator;
    }
}
