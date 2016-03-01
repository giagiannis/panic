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

import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;

import java.util.Properties;

/**
 * Algorithm that trains the
 * Created by Giannis Giannakopoulos on 3/1/16.
 */
public class DTAdaptive extends DTAlgorithm{
    public DTAdaptive(int deploymentBudget,
                      String samplerType,
                      MetricSource source,
                      String separatorType,
                      String budgetType, Properties budgetProperties,
                      String selectorType, Properties selectorProperties) {
        super(deploymentBudget, samplerType, source, separatorType, budgetType, budgetProperties, selectorType, selectorProperties);
    }

    @Override
    public void run() {
        while(!this.terminationCondition()) {
            this.step();
        }
    }

    private void step() {
        
    }
}
