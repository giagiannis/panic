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

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import gr.ntua.ece.cslab.panic.core.fresh.tree.line.SplitLine;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Test class for CandidateSolution
 * Created by Giannis Giannakopoulos on 2/12/16.
 */
public class CandidatePairTest {

    private List<OutputSpacePoint> points;
    private Abstract2DSeparator.CandidateSolution pair;

    @Before
    public void setUp() throws Exception {
        points = new TestUtils.FileReader().getOutputSpacePoints();
        Random random = new Random();
        OutputSpacePoint o = points.get(random.nextInt(points.size()));
        List<String> dimensions = new LinkedList<>(o.getInputSpacePoint().getKeysAsCollection());
        String randomDimension1 = dimensions.get(random.nextInt(dimensions.size()));
        String randomDimension2 = dimensions.get(random.nextInt(dimensions.size()));
        while (randomDimension1.equals(randomDimension2))
            randomDimension2 = dimensions.get(random.nextInt(dimensions.size()));
        InputSpacePoint p1 = points.get(random.nextInt(points.size())).getInputSpacePoint();
        InputSpacePoint p2 = points.get(random.nextInt(points.size())).getInputSpacePoint();
        while (p1.equals(p2)) {
            p2 = points.get(random.nextInt(points.size())).getInputSpacePoint();
        }
        pair = new Abstract2DSeparator.CandidateSolution(points, new TestUtils.FileReader().getDeploymentSpace(), new SplitLine(p1, p2, randomDimension1, randomDimension2));
    }

    @Test
    public void testValidity() throws Exception {
        for(OutputSpacePoint p : pair.getLeftList()) {
            assertTrue(pair.getSplitLine().lessOrEqual(p.getInputSpacePoint()));
        }

        for(OutputSpacePoint p : pair.getRightList()) {
            assertTrue(!pair.getSplitLine().lessOrEqual(p.getInputSpacePoint()));
        }
    }
}