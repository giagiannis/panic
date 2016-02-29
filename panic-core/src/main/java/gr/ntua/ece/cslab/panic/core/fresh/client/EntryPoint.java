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

import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithm;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithmFactory;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTOnline;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSourceFactory;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
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
        options.addOption("h","help", false,"Prints this menu");
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
        for(String s:System.getProperties().stringPropertyNames()) {
            if(prop.getProperty(s)!=null) {
                prop.setProperty(s, System.getProperty(s));
            }
        }
        debugPrint("Conf file loaded and parsed");
        return prop;
    }

    public static void main(String[] args) throws ParseException, IOException {
        Map<String,String> cliOptions= parseCLIOptions(args);
        Properties properties = loadConfigurationFile(cliOptions);

        int repetitions = new Integer(properties.getProperty("entrypoint.repetitions"));
        double mse = 0.0, leafNodes = 0.0;
        long time = 0;
        for(int i=0;i<repetitions;i++) {
//            System.err.format("Repetition %d started...\t", i+1);
            DTAlgorithm algorithm;
            DTAlgorithmFactory factory1 = new DTAlgorithmFactory();
            algorithm = factory1.create(properties.getProperty("entrypoint.algorithm"), properties);
            long start=System.currentTimeMillis();
            algorithm.run();
            DecisionTree tree = algorithm.getBestTree();
            time+=(System.currentTimeMillis()-start);
            mse+=DTAlgorithm.meanSquareError(tree);
            leafNodes+=tree.getLeaves().size();
//            System.err.format("Done! (elapsed: %d)\n",(System.currentTimeMillis()-start)/1000);
        }
        System.out.format("%.5f\t%.5f\t%.5f\n", mse/repetitions, leafNodes/repetitions, time/(1000.0*repetitions));
    }
}
