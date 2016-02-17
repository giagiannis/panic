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

package gr.ntua.ece.cslab.panic.core.fresh.client;

import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAdaptive;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSourceFactory;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class used to execute experiments.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class EntryPoint {
    public static boolean DEBUG=false;
    public static String
            CONF_METRICSOURCE_PREFIX_KEY = "metricsource",
            CONF_METRICSOURCE_TYPE_KEY = "metricsource.type",
            CONF_SAMPLER_PREFIX = "sampler",
            CONF_SAMPLER_TYPE_KEY = "sampler.type",
            CONF_SEPARATOR_PREFIX = "separator",
            CONF_SEPARATOR_TYPE_KEY = "separator.type",
            CONF_BUDGET_PREFIX = "budget",
            CONF_BUDGET_POINTS_KEY = "budget.points",
            CONF_BUDGET_TYPE_KEY= "budget.type";


    protected static void debugPrint(String message) {
        if(DEBUG) {
            System.out.println(message);
        }
    }
    /**
     * Parses the CLI options and returns them into a HashMap. If help has been requested, it prints the menu
     * and exits
     */
    protected static Map<String, String> parseCLIOptions(String[] args) throws ParseException {
        // setting the options
        Options options = new Options();
        options.addOption("h","help", false,"Prins this menu");
        options.addOption("c", "conf", true, "overrides the configuration file that is, by default into the classpath");
        options.getOption("c").setArgName("config");
        options.addOption(null, "debug", false, "if set, prints diagnostic messages");


        // parsing from args
        CommandLineParser parser = new GnuParser();
        CommandLine cline = parser.parse(options, args);

        // creating conf HashMap
        HashMap<String, String> kv = new HashMap<>();
        for(Option o:cline.getOptions()) {
            kv.put(o.getLongOpt(), o.getValue());
        }

        // setting parameters from CLI options
        if(kv.containsKey("help")) { // quick and dirty
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(EntryPoint.class.toString(), options);
            System.exit(0);
        }

        if(kv.containsKey("debug")) {
            DEBUG = true;
        }
        return kv;
    }

    protected static Properties loadConfigurationFile(Map<String,String> cliOptions) throws IOException {
        String confFileName = "panic.properties";
        Properties prop = new Properties();
        InputStream stream;
        if(cliOptions.containsKey("conf")) {
            confFileName = cliOptions.get("conf");
            debugPrint("Loading configuration file "+confFileName+" from filesystem");
            stream = new FileInputStream(confFileName);
        } else {
            debugPrint("Loading configuration file "+confFileName+" from classpath");
            stream = EntryPoint.class.getClassLoader().getResourceAsStream(confFileName);
        }
        prop.load(stream);
        debugPrint("Conf file loaded and parsed");
        return prop;
    }

    protected static Properties isolateProperties(Properties original, String prefix) {
        Properties finalProperties = new Properties();
        for(String key:original.stringPropertyNames()) {
            if(key.contains(prefix)) {
                int index = key.indexOf(prefix) + prefix.length()+1;
                finalProperties.setProperty(key.substring(index), original.getProperty(key));
            }
        }
        return finalProperties;

    }
    public static void main(String[] args) throws ParseException, IOException {
        Map<String,String> cliOptions= parseCLIOptions(args);
        Properties properties = loadConfigurationFile(cliOptions);

        // MetricSource
        Properties msProps = isolateProperties(properties, CONF_METRICSOURCE_PREFIX_KEY +"."+properties.getProperty(CONF_METRICSOURCE_TYPE_KEY));
        MetricSourceFactory factory  = new MetricSourceFactory();
        MetricSource source = factory.create(properties.getProperty(CONF_METRICSOURCE_TYPE_KEY), msProps);
        source.configure();

        // deploymentBudget count
        int budget = new Integer(properties.getProperty(CONF_BUDGET_POINTS_KEY));
        Properties budgetProperties = isolateProperties(properties, CONF_BUDGET_PREFIX+"."+properties.getProperty(CONF_BUDGET_TYPE_KEY));
        Properties samplerProperties = isolateProperties(properties, CONF_SAMPLER_PREFIX+"."+properties.getProperty(CONF_SAMPLER_TYPE_KEY));

        DTAdaptive adaptive = new DTAdaptive(budget, source,
                properties.getProperty(CONF_BUDGET_TYPE_KEY), budgetProperties,
                properties.getProperty(CONF_SAMPLER_TYPE_KEY),
                properties.getProperty(CONF_SEPARATOR_TYPE_KEY));

        adaptive.run();
    }
}
