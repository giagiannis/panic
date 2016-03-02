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

package gr.ntua.ece.cslab.panic.core.fresh.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 3/2/16.
 */
public class TotalOrderSamplerTest {

    private DeploymentSpace space;
    private List<OutputSpacePoint> points;
    private String[] dimensionOrdering;

    private TotalOrderSampler sampler;
    @Before
    public void setUp() throws Exception {
        TestUtils.FileReader reader = new TestUtils.FileReader();
        this.space = reader.getDeploymentSpace();
        this.points = reader.getOutputSpacePoints();
        List<String> dimensions = new LinkedList<>(this.space.getRange().keySet());
        Collections.shuffle(dimensions);
        this.dimensionOrdering = new String[dimensions.size()];
        dimensions.toArray(this.dimensionOrdering);
    }

    @Test
    public void testNext() throws Exception {
        sampler = new TotalOrderSampler(this.space, TestUtils.FileReader.POINTS_COUNT, this.dimensionOrdering);

        InputSpacePoint p1 = sampler.next();
        InputSpacePoint p2 = sampler.next();

        assertEquals(p1.getValue(this.dimensionOrdering[0]), p2.getValue(this.dimensionOrdering[0]));
        assertNotEquals(
                p1.getValue(this.dimensionOrdering[this.dimensionOrdering.length-1]),
                p2.getValue(this.dimensionOrdering[this.dimensionOrdering.length-1]));
    }
}