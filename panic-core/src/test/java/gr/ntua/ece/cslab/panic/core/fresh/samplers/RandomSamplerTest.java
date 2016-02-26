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
import gr.ntua.ece.cslab.panic.core.fresh.samplers.RandomSampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.deprec.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 2/26/16.
 */
public class RandomSamplerTest {
    private List<OutputSpacePoint> points;
    private DeploymentSpace space;
    RandomSampler sampler;
    @Before
    public void setUp() throws Exception {
        TestUtils.FileReader reader = new TestUtils.FileReader();
        this.points = reader.getOutputSpacePoints();
        this.space = reader.getDeploymentSpace();
        sampler =  new RandomSampler(this.space, TestUtils.FileReader.POINTS_COUNT);
    }


    @Test
    public void testUniqueness() throws Exception {
        Set<InputSpacePoint> points = new HashSet<>();
        while (sampler.hasMore()) {
            InputSpacePoint point = sampler.next();
            points.add(point);
        }
        assertEquals(TestUtils.FileReader.POINTS_COUNT, points.size());
    }

    @Test
    public void testForbidded() throws Exception {
        sampler = new RandomSampler(this.space, 10);
        List<InputSpacePoint> forbidden = new LinkedList<>();
        while(sampler.hasMore()) {
            forbidden.add(sampler.next());
        }

        sampler  = new RandomSampler(this.space, 1000);
        sampler.setForbiddenPoints(forbidden);
        int pointCount = 0;
        while(sampler.hasMore()) {
            InputSpacePoint p = sampler.next();
            if(p!=null)
                pointCount+=1;
            assertFalse(forbidden.contains(p));
        }
        assertEquals(sampler.pickedPoints.size(), pointCount);
    }
}