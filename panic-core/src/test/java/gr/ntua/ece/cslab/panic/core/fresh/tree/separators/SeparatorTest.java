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
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Test class for abstract Separator class
 * Created by Giannis Giannakopoulos on 2/12/16.
 */
public class SeparatorTest {
    private List<OutputSpacePoint> points;
    private Separator separator;

    @Before
    public void setUp() throws Exception {
        points = new TestUtils.FileReader().getOutputSpacePoints();
        DecisionTreeLeafNode node = new DecisionTreeLeafNode(points, new TestUtils.FileReader().getDeploymentSpace());
        separator = new Separator(node) {
            @Override
            protected double estimate(CandidateSolution pair) {
                return new Random().nextDouble();
            }
        };
        separator.separate();
    }

    @Test
    public void testPointsCount() throws Exception {
        // |L| + |R| should be equal to points.size()
        if(separator.getResult()!=null) {
            int pointsCount = 0;
            pointsCount +=separator.getResult().getLeftChild().castToLeaf().getPoints().size();
            pointsCount +=separator.getResult().getRightChild().castToLeaf().getPoints().size();
            assertEquals(pointsCount, points.size());
        }

    }
}