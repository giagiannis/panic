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

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithm;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithmFactory;
import gr.ntua.ece.cslab.panic.core.fresh.evaluation.Metrics;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class used to execute experiments.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class EntryPoint {
    public static boolean DEBUG=false;

    protected static void debugPrint(String message) {
        if(DEBUG) {
            System.err.println(message);
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
        System.getProperties().stringPropertyNames().stream().filter(s -> prop.getProperty(s) != null).forEach(s -> {
            prop.setProperty(s, System.getProperty(s));
        });
        debugPrint("Conf file loaded and parsed");
        debugPrint("Properties: "+prop);
        return prop;
    }

    public static void main(String[] args) throws ParseException, IOException {
        Map<String,String> cliOptions= parseCLIOptions(args);
        Properties properties = loadConfigurationFile(cliOptions);

        int repetitions = new Integer(properties.getProperty("entrypoint.repetitions"));
        double mse = 0.0, leafNodes = 0.0, rSquared = 0.0;
        long time = 0;
        List<DecisionTree> producedTrees = new LinkedList<>();
//        List<Long> executionTimes = new LinkedList<>();

//        List<Double> mseList = new LinkedList<>(), leafList = new LinkedList<>();
        List<Double >executionTimes = new LinkedList<>();
        List<OutputSpacePoint> points = null;
        for(int i=0;i<repetitions;i++) {
            debugPrint("Repetition "+(i+1));
            DTAlgorithm algorithm;
            DTAlgorithm.DEBUG = DEBUG;
            DTAlgorithmFactory factory1 = new DTAlgorithmFactory();
            algorithm = factory1.create(properties.getProperty("entrypoint.algorithm"), properties);
            points = algorithm.getSource().getActualPoints();
            long start=System.currentTimeMillis();
            algorithm.run();
            DecisionTree tree = algorithm.getBestTree();
//            System.out.println(tree);
            producedTrees.add(tree);
            executionTimes.add((System.currentTimeMillis()-start)/1000.0);
//            mse=Metrics.getMSE(tree, algorithm.getSource().getActualPoints());
//            leafNodes=tree.getLeaves().size();
//            mseList.add(mse);
//            leafList.add(leafNodes);
        }
        // output
        final List<OutputSpacePoint> testPoints = new LinkedList<>(points);
        PrintStream out = System.out;
        printVariable(out, producedTrees.stream().map(a->Metrics.getMSE(a, testPoints)).collect(Collectors.toList()));
        printVariable(out, producedTrees.stream().map(a->Metrics.getRSquared(a, testPoints)).collect(Collectors.toList()));
        printVariable(out, executionTimes);
        printVariable(out, producedTrees.stream().map(a->(double)a.getLeaves().size()).collect(Collectors.toList()));
        out.println();

    }

    private static double mean(List<Double> list) {
        if(list.size()==0)
            return Double.NaN;
        double mean = 0.0;
        for(Double o  :list) {
                mean+= o;
        }
        return (list.size()==0?Double.NaN:mean/list.size());
    }

    private static double variance(List<Double> list) {
        double mean = mean(list);
        double variance = 0.0;
        for(Double o : list) {
            variance+=(o-mean)*(o-mean);
        }
        return (list.size()==0?Double.NaN:variance/list.size());
    }


    private static double percentile(List<Double> list, int rank) {
        int index = (int) Math.ceil((rank/100.0)*list.size());
        list.sort((a,b)->a.compareTo(b));
        return list.get(index-1);
    }

    private static void printVariable(PrintStream out, List<Double> metric) {
        out.format("%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t", mean(metric), variance(metric), percentile(metric, 1), percentile(metric, 25), percentile(metric, 50), percentile(metric, 75), percentile(metric, 100));
    }
}
