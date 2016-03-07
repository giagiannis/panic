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
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Test class for VarianceSeparator
 * Created by Giannis Giannakopoulos on 2/12/16.
 */
public class VarianceSeparatorTest {
    private List<OutputSpacePoint> points;
    private Separator separator;
    private DeploymentSpace space;
    @Before
    public void setUp() throws Exception {
        space = new TestUtils.FileReader().getDeploymentSpace();
        points = new TestUtils.FileReader().getOutputSpacePoints();
        SeparatorFactory factory = new SeparatorFactory();
        separator = factory.create("variance", new DecisionTreeLeafNode(points, space));
        separator.separate();
    }

    @Test
    public void testValidity() throws Exception {
        // the pair returned by the separator must be the best in terms of variance
        HashMap<String, Set<Double>> possibleValues = separator.possibleValues(this.points);

        Separator.CandidateSolution best = new Separator.CandidateSolution(points,separator.getResult().getAttribute(), separator.getResult().getValue(), space);
        double minVariance = separator.estimate(best);

        for(String candidateDimension:possibleValues.keySet()) {
            for(Double candidateValue: possibleValues.get(candidateDimension)) {
                Separator.CandidateSolution candidatePair;
                candidatePair = new Separator.CandidateSolution(points, candidateDimension, candidateValue, space);
                double estimation = separator.estimate(candidatePair);
                assertTrue(minVariance>=separator.estimate(candidatePair));
            }
        }
    }
}