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

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithm;
import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithmFactory;
import gr.ntua.ece.cslab.panic.core.fresh.evaluation.Metrics;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.line.SplitLine;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeNode;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

/**
 * Class used to print the approximate solutions + samples obtained from the executed algorithm.
 * Created by Giannis Giannakopoulos on 2/25/16.
 */
public class AlgorithmPrinter {

    public static boolean DEBUG = false;

    protected static void debugPrint(String message) {
        if (DEBUG) {
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
        options.addOption("h", "help", false, "Prints this menu");
        options.addOption("c", "conf", true, "overrides the configuration file that is, by default into the classpath");
        options.getOption("c").setArgName("config");

        // parsing from args
        CommandLineParser parser = new GnuParser();
        CommandLine cline = parser.parse(options, args);

        // creating conf HashMap
        HashMap<String, String> kv = new HashMap<>();
        for (Option o : cline.getOptions()) {
            kv.put(o.getLongOpt(), o.getValue());
        }

        // setting parameters from CLI options
        if (kv.containsKey("help")) { // quick and dirty
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(EntryPoint.class.toString(), options);
            System.exit(0);
        }

        if (kv.containsKey("debug")) {
            DEBUG = true;
        }
        return kv;
    }

    protected static Properties loadConfigurationFile(Map<String, String> cliOptions) throws IOException {
        String confFileName = "panic.properties";
        Properties prop = new Properties();
        InputStream stream;
        if (cliOptions.containsKey("conf")) {
            confFileName = cliOptions.get("conf");
            debugPrint("Loading configuration file " + confFileName + " from filesystem");
            stream = new FileInputStream(confFileName);
        } else {
            debugPrint("Loading configuration file " + confFileName + " from classpath");
            stream = EntryPoint.class.getClassLoader().getResourceAsStream(confFileName);
        }
        prop.load(stream);
        for (String s : System.getProperties().stringPropertyNames()) {
            if (prop.getProperty(s) != null) {
                prop.setProperty(s, System.getProperty(s));
            }
        }
        debugPrint("Conf file loaded and parsed");
        return prop;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> cliOptions = parseCLIOptions(args);
        Properties properties = loadConfigurationFile(cliOptions);
        if (args.length < 4) {
            System.err.format("Provide the following args:\n(1) %s\n" +
                            "(2) %s\n" +
                            "(3) %s\n" +
                            "(4) %s\n",
                    "sample file", "predicted values file", "cuts file", "error values per leaf");
            System.exit(1);
        }


        DecisionTree bestTree = null;
        double mse = Double.MAX_VALUE;

        int repetitions = new Integer(properties.getProperty("entrypoint.repetitions"));
        for (int i = 0; i < repetitions; i++) {
            System.out.format("Running execution %d\n", i + 1);
            DTAlgorithm algorithm;
            DTAlgorithmFactory factory1 = new DTAlgorithmFactory();
            algorithm = factory1.create(properties.getProperty("entrypoint.algorithm"), properties);
            algorithm.run();
            DecisionTree tree = algorithm.getBestTree();
            double current = Metrics.getMSE(tree, algorithm.getSource().getActualPoints());
//            mse += Metrics.getMSE(tree, algorithm.getSource().getActualPoints());

            System.out.format("\tCross-validation score: %.5f\n", current);
            if (current <= mse) {
                bestTree = tree;
                mse = current;
                System.out.println("\tAssigned best!");
            }

        }

        PrintStream sampleFile = new PrintStream(new File(args[0]));
        PrintStream predictedFile = new PrintStream(new File(args[1]));
        PrintStream cuts = new PrintStream(new File(args[2]));
        PrintStream errorsPerLeafStream = new PrintStream(new File(args[3]));

        Map<String, Model> models = new HashMap<>();
        for (DecisionTreeLeafNode leaf : bestTree.getLeaves()) {
            Model model = new LinearRegression();
            model.configureClassifier();
            model.feed(leaf.getPoints());
            model.train();
            models.put(leaf.getId(), model);
        }
        for (OutputSpacePoint p : bestTree.getSamples()) {
            sampleFile.println(p.getInputSpacePoint().toStringCSVFormat() + "\t0.0");
        }
        sampleFile.flush();
        sampleFile.close();


        CSVFileManager manager = new CSVFileManager();
        manager.setFilename(properties.getProperty("metricsource.file.input"));
        for (InputSpacePoint p : manager.getInputSpacePoints()) {
            String id = bestTree.getLeaf(p).getId();
            OutputSpacePoint predicted = models.get(id).getPoint(p);
            predictedFile.println(predicted.getInputSpacePoint().toStringCSVFormat() + "\t" + predicted.getValue());
        }
        predictedFile.close();

        HashMap<String, Double> errorPerLeaf = Metrics.getMSEPerLeaf(bestTree, manager.getOutputSpacePoints());
        for (DecisionTreeLeafNode leaf : bestTree.getLeaves()) {
//            double minX1, maxX1;
//            maxX1 = leaf.getDeploymentSpace().getPoints().parallelStream().mapToDouble(a -> a.getValue("x1")).max().getAsDouble();
//            minX1 = leaf.getDeploymentSpace().getPoints().parallelStream().mapToDouble(a -> a.getValue("x1")).min().getAsDouble();
//
//            double minX2, maxX2;
//            maxX2 = leaf.getDeploymentSpace().getPoints().parallelStream().mapToDouble(a -> a.getValue("x2")).max().getAsDouble();
//            minX2 = leaf.getDeploymentSpace().getPoints().parallelStream().mapToDouble(a -> a.getValue("x2")).min().getAsDouble();

//            cuts.format("%.3f\t%.3f\t%.3f\t%.3f\n", minX1, minX2, 0.0, maxX2 - minX2);
//            cuts.format("%.3f\t%.3f\t%.3f\t%.3f\n", maxX1, minX2, 0.0, maxX2 - minX2);
//            cuts.format("%.3f\t%.3f\t%.3f\t%.3f\n", minX1, minX2, maxX1 - minX1, 0.0);
//            cuts.format("%.3f\t%.3f\t%.3f\t%.3f\n", minX1, maxX2, maxX1 - minX1, 0.0);

            DecisionTreeNode n = leaf;
            List<SplitLine> borderLines = new ArrayList<>();
            while ((n = n.getFather()) != null) {
                borderLines.add(n.castToTest().getSplitLine());
            }

            for(SplitLine s : borderLines) {
                for(InputSpacePoint p : leaf.getDeploymentSpace().getPoints()) {
                    if(SplitLine.fuzzyCompare(s, p, 0.02)==0) {
                        cuts.format("%.5f\t%.5f\n", p.getValue("x1"), p.getValue("x2"));
                    }
                }
            }

            Double x1Avg= leaf.getDeploymentSpace().getPoints().parallelStream().mapToDouble(a->a.getValue("x1")).average().getAsDouble();
            Double x2Avg= leaf.getDeploymentSpace().getPoints().parallelStream().mapToDouble(a->a.getValue("x2")).average().getAsDouble();

            errorsPerLeafStream.format("%.3f\t%.3f\t%.5f\n", x1Avg, x2Avg, errorPerLeaf.get(leaf.getId()));
        }
        cuts.close();
        errorsPerLeafStream.close();
    }
}
