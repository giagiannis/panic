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

import java.util.Properties;

/**
 * Abstract class representing a MetricSource object.
 * Created by Giannis Giannakopoulos on 2/17/16.
 */
public abstract class MetricSource {
    protected Properties configuration;

    public MetricSource(Properties configuration) {
        this.configuration = configuration;
    }

    /**
     * Configure the metric source
     */
    public abstract void configure();

    /**
     * Return an OutputSpacePoint for a given InputSpacePoint.
     * @param point the inputspace point to search for
     * @return the OutputSpacePoint
     */
    public abstract OutputSpacePoint getPoint(InputSpacePoint point);
}
