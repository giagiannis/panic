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
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 3/7/16.
 */
public class SystematicSamplerTest {
    private DeploymentSpace space;
    private SystematicSampler sampler;
    private List<InputSpacePoint> forbiddenPoints;
    @Before
    public void setUp() throws Exception {
        TestUtils.FileReader reader = new TestUtils.FileReader();
        space = reader.getDeploymentSpace();
        int budget = 50;
//        String[] dimensionOrder = space.getDimensionLabels();
        this.forbiddenPoints = new LinkedList<>();
        RandomSampler sampler = new RandomSampler(reader.getDeploymentSpace(), budget);
        while (sampler.hasMore()) {
            this.forbiddenPoints.add(sampler.next());
        }

    }

    @Test
    public void testNext() throws Exception {
        int budget = 100;
        String[] dimensionOrder = space.getDimensionLabels();
        this.sampler = new SystematicSampler(this.space, budget, dimensionOrder);
        this.sampler.setForbiddenPoints(this.forbiddenPoints);
        int count = 0;
        Set<InputSpacePoint> pointsPicked = new HashSet<>();
        while(sampler.hasMore()) {
            InputSpacePoint point = sampler.next();
            if(point!=null) {
                assertTrue(!this.forbiddenPoints.contains(point));
                assertTrue(!pointsPicked.contains(point));
                pointsPicked.add(point);
                count++;
            }
        }
        System.out.println(count);
    }
}