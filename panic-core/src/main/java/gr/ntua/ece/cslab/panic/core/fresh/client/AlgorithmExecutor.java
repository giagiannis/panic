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
import gr.ntua.ece.cslab.panic.core.models.*;
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
public class AlgorithmExecutor extends Client{
    public static boolean DEBUG=false, WEKA_MODELS = false;

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
        options.addOption(null, "weka-models", false, "if set, bypasses standard tree modeling and trains multiple other models");


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
            formatter.printHelp(AlgorithmExecutor.class.toString(), options);
            System.exit(0);
        }

        if(kv.containsKey("debug")) {
            DEBUG = true;
        }

        if(kv.containsKey("weka-models")) {
            WEKA_MODELS=true;
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
            stream = AlgorithmExecutor.class.getClassLoader().getResourceAsStream(confFileName);
        }
        if(stream==null) {
            System.err.println("You have to provide a configuration file!");
            System.exit(0);
        }
        prop.load(stream);
        System.getProperties().stringPropertyNames().stream().filter(s -> prop.getProperty(s) != null).forEach(s -> {
            prop.setProperty(s, System.getProperty(s));
        });
        debugPrint("Conf file loaded and parsed");
        debugPrint("Properties: "+prop);
        return prop;
    }

    public static void main(String[] args) throws Exception {
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

        String costFunction = "";
        if(properties.containsKey("entrypoint.cost.function"))
            costFunction = properties.getProperty("entrypoint.cost.function");
        else
            costFunction = "";
        final String costFunctionFinal = new String(costFunction);
        if(!WEKA_MODELS) {
            printVariable(out, producedTrees.stream().map(a -> Metrics.getMSE(a, testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a -> Metrics.getRSquared(a, testPoints)).collect(Collectors.toList()));
            printVariable(out, executionTimes);
            printVariable(out, producedTrees.stream().map(a -> Metrics.getMAE(a, testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a -> (double) a.getLeaves().size()).collect(Collectors.toList()));
            if(costFunctionFinal!=null && !costFunctionFinal.equals(""))
                printVariable(out, producedTrees.stream().map(a -> Metrics.getCost(a.getSamples(), costFunctionFinal)).collect(Collectors.toList()));
            out.println();
        } else {
            // RandomCommittee
            printVariable(out, producedTrees.stream().map(a->Metrics.getMSE(RandomCommittee.class, a.getSamples(), testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a->Metrics.getMAE(RandomCommittee.class, a.getSamples(), testPoints)).collect(Collectors.toList()));

            //MLPerceptron
            printVariable(out, producedTrees.stream().map(a->Metrics.getMSE(MLPerceptron.class, a.getSamples(), testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a->Metrics.getMAE(MLPerceptron.class, a.getSamples(), testPoints)).collect(Collectors.toList()));

            // LeastSquares
            printVariable(out, producedTrees.stream().map(a->Metrics.getMSE(LeastSquares.class, a.getSamples(), testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a->Metrics.getMAE(LeastSquares.class, a.getSamples(), testPoints)).collect(Collectors.toList()));

            // RandomSubSpaces
            printVariable(out, producedTrees.stream().map(a->Metrics.getMSE(RandomSubSpaces.class, a.getSamples(), testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a->Metrics.getMAE(RandomSubSpaces.class, a.getSamples(), testPoints)).collect(Collectors.toList()));

            // BagClassify
            printVariable(out, producedTrees.stream().map(a->Metrics.getMSE(BagClassify.class, a.getSamples(), testPoints)).collect(Collectors.toList()));
            printVariable(out, producedTrees.stream().map(a->Metrics.getMAE(BagClassify.class, a.getSamples(), testPoints)).collect(Collectors.toList()));

            // cost
            if(costFunctionFinal!=null && !costFunctionFinal.equals(""))
                printVariable(out, producedTrees.stream().map(a -> Metrics.getCost(a.getSamples(), costFunctionFinal)).collect(Collectors.toList()));
            out.println();

        }
    }
}