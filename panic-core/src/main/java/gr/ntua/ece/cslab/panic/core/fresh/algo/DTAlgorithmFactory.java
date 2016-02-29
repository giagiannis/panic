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
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSourceFactory;

import java.util.Properties;

/**
 * Created by Giannis Giannakopoulos on 2/19/16.
 */
public class DTAlgorithmFactory {

    public static String
            CONF_METRICSOURCE_PREFIX_KEY = "metricsource",
            CONF_METRICSOURCE_TYPE_KEY = "metricsource.type",
            CONF_SAMPLER_PREFIX = "sampler",
            CONF_SAMPLER_TYPE_KEY = "sampler.type",
            CONF_SEPARATOR_PREFIX = "separator",
            CONF_SEPARATOR_TYPE_KEY = "separator.type",
            CONF_BUDGET_PREFIX = "budget",
            CONF_BUDGET_POINTS_KEY = "budget.points",
            CONF_BUDGET_TYPE_KEY= "budget.type",
            CONF_SELECTOR_TYPE_KEY ="selector.type",
            CONF_SELECTOR_PREFIX  ="selector";

    public DTAlgorithm create(String type, Properties properties) {
        DTAlgorithm algorithm = null;
        String samplerType = properties.getProperty(CONF_SAMPLER_TYPE_KEY);
        int deploymentBudget = new Integer(properties.getProperty(CONF_BUDGET_POINTS_KEY));
        Properties msProps = isolateProperties(properties, CONF_METRICSOURCE_PREFIX_KEY +"."+properties.getProperty(CONF_METRICSOURCE_TYPE_KEY));
        MetricSourceFactory factory  = new MetricSourceFactory();
        MetricSource source = factory.create(properties.getProperty(CONF_METRICSOURCE_TYPE_KEY), msProps);
        source.configure();
        String separatorType = properties.getProperty(CONF_SEPARATOR_TYPE_KEY);
        String budgetType = properties.getProperty(CONF_BUDGET_TYPE_KEY);
        Properties budgetProperties = this.isolateProperties(properties, CONF_BUDGET_PREFIX+"."+properties.getProperty(CONF_BUDGET_TYPE_KEY));

        String selectorType = properties.getProperty(CONF_SELECTOR_TYPE_KEY);
        Properties selectorProperties = this.isolateProperties(properties, CONF_SELECTOR_PREFIX+"."+properties.getProperty(CONF_SELECTOR_TYPE_KEY));

        switch (type) {
            case "dtonline":
                algorithm = new DTOnline(deploymentBudget,samplerType,source, separatorType,
                        budgetType, budgetProperties, selectorType, selectorProperties);
                break;
            case "dtrt":
                algorithm = new DTRT(deploymentBudget, samplerType, source,separatorType, budgetType, budgetProperties, selectorType, selectorProperties);
                if(new Boolean(properties.getProperty("dtrt.onlinetraining"))) {
                    ((DTRT)algorithm).setOnlineTraining(true);
                }
                break;
            default:
                break;
        }
        return algorithm;
    }

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
}
