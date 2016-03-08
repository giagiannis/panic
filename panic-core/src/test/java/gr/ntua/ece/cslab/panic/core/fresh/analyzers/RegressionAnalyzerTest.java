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

package gr.ntua.ece.cslab.panic.core.fresh.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.RandomSampler;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 3/8/16.
 */
public class RegressionAnalyzerTest {

    private List<OutputSpacePoint> points;
    private String performanceDimension;
    private String[] inputDimensions;
    @Before
    public void setUp() throws Exception {
        TestUtils.FileReader reader = new TestUtils.FileReader();
        points = reader.getOutputSpacePoints();
        this.performanceDimension = points.get(0).getKey();
        this.inputDimensions  =
                points.get(0).getInputSpacePoint().getKeysAsCollection().toArray(new String[points.get(0).getInputSpacePoint().getKeysAsCollection().size()]);
        int size = 134;
        int start = new Random().nextInt(points.size()-size);
        points = points.subList(start, start+size);


    }

    @Test
    public void testAnalyze() throws Exception {
        RegressionAnalyzer analyzer = new RegressionAnalyzer(points);
        analyzer.analyze();
        assertTrue(!analyzer.getDistances().get(this.performanceDimension).isEmpty());
        for(String s : this.inputDimensions)
            assertTrue(analyzer.getDistances().get(this.performanceDimension).containsKey(s));
    }
}