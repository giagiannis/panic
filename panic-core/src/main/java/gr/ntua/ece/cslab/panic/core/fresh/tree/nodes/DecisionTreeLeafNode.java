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

import java.util.List;

/**
 * Class  representing a terminal - leaf node of the decision tree
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTreeLeafNode extends DecisionTreeNode {
    private final List<OutputSpacePoint> points;

    public DecisionTreeLeafNode(List<OutputSpacePoint> points) {
        this.points = points;
    }

    public List<OutputSpacePoint> getPoints() {
        return points;
    }

    @Override
    protected String toString(String pad) {
        return String.format("%s[%d]",pad,this.points.size());
    }
}
