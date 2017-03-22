/*
 * Copyright 2017 Giannis Giannakopoulos
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

import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeTestNode;

/**
 * Separator interface
 * Created by Giannis Giannakopoulos on 3/20/17.
 */
public interface Separator {

    /**
     * Method used to execute the separator.
     */
    void separate();

    /**
     * getResult method returns the output of the process.
     * @return
     */
    DecisionTreeTestNode getResult();

    /**
     * setAxisParallelSplits identifies whether the calculated lines are axis parallel (faster) or not (more accurate)
     */
    void setAxisParallelSplits(boolean flag);
}
