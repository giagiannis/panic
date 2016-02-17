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

package gr.ntua.ece.cslab.panic.core.fresh.metricsource;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Metric source implementation that provides metrics from a file.
 * Created by Giannis Giannakopoulos on 2/17/16.
 */
public class FileMetricSourceTest {

    private FileMetricSource deployer;
    private List<OutputSpacePoint> points;
    @Before
    public void setUp() throws Exception {
        URL url  = this.getClass().getClassLoader().getResource(TestUtils.FileReader.DEFAULT_DATASET_PATH);
        String defaultDatasetPath = null;
        if(url!=null)
             defaultDatasetPath= url.getFile();
        Properties props = new Properties();
        props.setProperty("input", defaultDatasetPath);
        deployer = new FileMetricSource(props);
        deployer.configure();

        points = new TestUtils.FileReader().getOutputSpacePoints();
    }

    @Test
    public void testExisting() throws Exception {
        OutputSpacePoint p=points.get(new Random().nextInt(points.size()));
        assertTrue(p.getValue()==this.deployer.getPoint(p.getInputSpacePoint()).getValue());
    }
    @Test
    public void testNonExisting() throws Exception {
        assertNull(this.deployer.getPoint(null));
    }

    @Test
    public void testDeploymentSpace() throws Exception {
        assertNotNull(this.deployer.getDeploymentSpace());
        System.out.println(deployer.getDeploymentSpace());
    }
}