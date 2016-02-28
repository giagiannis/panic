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

package gr.ntua.ece.cslab.panic.core.fresh.algo;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSourceFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 2/26/16.
 */
public class DTRTTest {

    private DTAlgorithm algorithm;

    private Properties isolateProperties(Properties original, String prefix) {
        Properties finalProperties = new Properties();
        for(String key:original.stringPropertyNames()) {
            if(key.contains(prefix)) {
                int index = key.indexOf(prefix) + prefix.length()+1;
                finalProperties.setProperty(key.substring(index), original.getProperty(key));
            }
        }
        return finalProperties;
    }

    @Before
    public void setUp() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("panic-test.properties");
        Properties properties = new Properties();
        if(stream!=null) {
            properties.load(stream);
        }

        String samplerType = properties.getProperty("sampler.type");
        int deploymentBudget = new Integer(properties.getProperty("budget.points"));
//        int deploymentBudget = 10;
        Properties msProps = isolateProperties(properties, "metricsource."+properties.getProperty("metricsource.type"));
        String classLoaderPathFile = this.getClass().getClassLoader().getResource(msProps.getProperty("input")).getFile();
        msProps.setProperty("input", classLoaderPathFile);
        MetricSourceFactory factory  = new MetricSourceFactory();
        MetricSource source = factory.create(properties.getProperty("metricsource.type"), msProps);
        source.configure();

        String separatorType = properties.getProperty("separator.type");
        String budgetType = properties.getProperty("budget.type");
        Properties budgetProperties = this.isolateProperties(properties, "budget"+"."+properties.getProperty("budget.type"));
        algorithm = new DTRT(deploymentBudget,samplerType,source, separatorType,
                budgetType, budgetProperties);

        algorithm.run();
    }


    @Test
    public void testSamplesUniqueness() throws Exception {
        Set<InputSpacePoint> points = algorithm.getSamples().stream().map(OutputSpacePoint::getInputSpacePoint).collect(Collectors.toSet());
        assertEquals(algorithm.getSamples().size(), points.size());
    }
}