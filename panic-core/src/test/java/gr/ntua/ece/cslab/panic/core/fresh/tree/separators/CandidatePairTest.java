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
    private Separator.CandidateSolution pair;

    @Before
    public void setUp() throws Exception {
        points = new TestUtils.FileReader().getOutputSpacePoints();
        Random random = new Random();
        OutputSpacePoint o = points.get(random.nextInt(points.size()));
        List<String> dimensions = new LinkedList<>(o.getInputSpacePoint().getKeysAsCollection());
        String randomDimension = dimensions.get(random.nextInt(dimensions.size()));
        Double randomValue = o.getInputSpacePoint().getValue(randomDimension);
        pair = new Separator.CandidateSolution(points, randomDimension, randomValue, new TestUtils.FileReader().getDeploymentSpace());
    }

    @Test
    public void testValidity() throws Exception {
        for(OutputSpacePoint p : pair.getLeftList()) {
            double pointValue = p.getInputSpacePoint().getValue(pair.getSeparationDimension());
            assertTrue(pointValue <= pair.getSeparationValue());
        }

        for(OutputSpacePoint p : pair.getRightList()) {
            double pointValue = p.getInputSpacePoint().getValue(pair.getSeparationDimension());
            assertTrue(pointValue > pair.getSeparationValue());
        }
    }
}