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
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;

import java.util.*;

/**
 * Reads the OutputSpacePoints from a file source and loads them in memory.
 * Created by Giannis Giannakopoulos on 2/17/16.
 */
public class FileMetricSource extends MetricSource {
    //    public static String CONF_FILE_INPUT="input";
    private Map<InputSpacePoint, OutputSpacePoint> mapping;

    /**
     * Needed properties: "input"
     * @param configuration must container "input" key, pointing to the input file
     */
    public FileMetricSource(Properties configuration) {
        super(configuration);
        this.mapping = new HashMap<>();
        CSVFileManager manager = new CSVFileManager(this.configuration.getProperty("input"));
        for(OutputSpacePoint p : manager.getOutputSpacePoints()) {
            this.mapping.put(p.getInputSpacePoint(), p);
        }
        this.deploymentSpace = new DeploymentSpace();
        this.deploymentSpace.setRange(manager.getDimensionRanges());
        this.unavailablePoints = manager.getUnavailablePoints();
    }

    @Override
    public void configure() {
        // initialize the manager
    }

    @Override
    public OutputSpacePoint getPoint(InputSpacePoint point) {
        return this.mapping.get(point);
    }

    @Override
    public List<OutputSpacePoint> getActualPoints() {
        return new LinkedList<>(this.mapping.values());
    }


}
