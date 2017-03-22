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

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 3/21/17.
 */
public class SASeparatorTest {
    private List<OutputSpacePoint> points;
    private SASeparator separator;
    private DeploymentSpace space;

    @Before
    public void setUp() throws Exception {
        space = new TestUtils.FileReader().getDeploymentSpace();
        points = new TestUtils.FileReader().getOutputSpacePoints();
        DecisionTreeLeafNode leaf = new DecisionTreeLeafNode(points, space);

        separator = new SASeparator(leaf);
        separator.setScriptPath("src/main/scripts/rscripts/gensa.R");
    }

    @Test
    public void separate() throws Exception {
        separator.separate();
    }
}