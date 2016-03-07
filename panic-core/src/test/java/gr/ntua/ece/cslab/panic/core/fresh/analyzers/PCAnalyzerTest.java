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
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * PCAnalyzer tests
 * Created by Giannis Giannakopoulos on 2/24/16.
 */
public class PCAnalyzerTest {
    private PCAnalyzer analyzer;
    private Random random;

    @Before
    public void setUp() throws Exception {
        List<OutputSpacePoint> points = new TestUtils.FileReader().getOutputSpacePoints();
        analyzer = new PCAnalyzer(points);
        random = new Random();

    }

    @Test
    public void testConvertData() throws Exception {
        double[][] data = analyzer.convertData();for(int i=0;i<10;i++) {
            int index = random.nextInt(analyzer.samples.size());
            OutputSpacePoint toCheck = analyzer.samples.get(index);
            for(int j=0;j<analyzer.dimensionOrdering.size()-1;j++) {
                assertEquals(data[index][j], toCheck.getInputSpacePoint().getValue(analyzer.dimensionOrdering.get(j)), 0);
            }
            assertEquals(data[index][data[0].length-1], toCheck.getValue(), 0);
        }
    }

    @Test
    public void testMean() throws Exception {
        double[] mean = analyzer.mean(analyzer.convertData());
        double[] toTest = new double[mean.length];

        for(OutputSpacePoint p : analyzer.samples) {
            for(int j=0;j<analyzer.dimensionOrdering.size()-1;j++) {
                toTest[j] += p.getInputSpacePoint().getValue(analyzer.dimensionOrdering.get(j));
            }
            toTest[analyzer.dimensionOrdering.size()-1] += p.getValue();
        }
        for(int j=0;j<toTest.length;j++) {
            toTest[j]/=analyzer.samples.size();
        }

        for(int j=0;j<toTest.length;j++) {
            assertEquals(mean[j], toTest[j],0);
        }
    }


    @Test
    public void testAnalyze() throws Exception {
        analyzer.analyze();
        assertNotNull(analyzer.getEigenValues());
        assertNotNull(analyzer.getEigenVectors());
//        System.out.println(analyzer.distances);
    }
}