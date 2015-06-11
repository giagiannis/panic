/*
 * Copyright 2014 Giannis Giannakopoulos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.ntua.ece.cslab.panic.core.client;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.metrics.GlobalMetrics;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import gr.ntua.ece.cslab.panic.core.utils.DatabaseClient;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.reflections.Reflections;

/**
 * Abstract class used to implement the most major methods used for argument
 * parsing etc. This class will be inherited by any implementing subclass. The
 * methods provided are static, so that they can by used by main methods of the
 * implementing subclasses.
 *
 * @author Giannis Giannakopoulos
 */
public class Benchmark {

    protected static Options options;
    protected static Double samplingRate;
    protected static PrintStream outputPrintStream, metricsOut;
    protected static String inputFile;
    protected static Sampler[] samplers;
    protected static Model[] models;
    protected static CommandLine cmd;
    protected static DatabaseClient dbClient;

    /**
     * Method used to setup the commons-cli argument parsing. Each implemented
     * benchmark may extend this list at will.
     *
     * @param args
     */
    public static void cliOptionsSetup(String[] args) {
        options = new Options();

        options.addOption("h", "help", false, "prints this help message");

        

        options.addOption("i", "input", true, "define the input file");
        options.getOption("i").setArgName("input");

        options.addOption("sr", "sampling-rate", true, "determines the sampling rate of the dataset (0.0 - 1.0)\ndefault: 0.2");
        options.getOption("sr").setArgName("rate");

        options.addOption("st", "sampling-type", true, "determines the sampling type (if not defined all the available models will be used)");
        options.getOption("st").setArgName("type");

        options.addOption("m", "models", true, "define the models to use (if not defined, all the available models will be trained");
        options.getOption("m").setArgName("model1,model2");

        options.addOption("lm", "list-models", false, "lists the available models");
        options.addOption("ls", "list-samplers", false, "lists the available samplers");
        
        options.addOption("mo", "metrics-output", true, "<deprecated> if specifed, redirects the metrics to an SQLite database (must be created)");
        options.addOption("o", "output", true, "<deprecated> define the output file\ndefault: stdout");
        options.getOption("o").setArgName("output");
        
        
        options.addOption("db", "database", true, "saves the results into a database (sqlite)");
        options.addOption(null, "skip-metrics", false, "do not save the metrics");
        options.addOption(null, "skip-samples", false, "do not save the chosen samples");
        options.addOption(null, "skip-predictions", false, "do not save the models' predictions");

    }

    /**
     * Use reflections to retrieve the supported models inside the classpath.
     *
     * @return
     */
    public static Class<? extends Model>[] discoverModels() {
        List<Class<? extends Model>> list = new ArrayList<>();
        Reflections reflections = new Reflections("gr.ntua.ece.cslab");
        for (Class<? extends Model> c : reflections.getSubTypesOf(Model.class)) {
            if (!c.getName().toLowerCase().contains("abstract") && !c.getName().toLowerCase().contains("deprecated")) {
                list.add(c);
            }
        }

        @SuppressWarnings("unchecked")
        Class<? extends Model>[] modelsDiscovered = new Class[list.size()];

        int i = 0;
        for (Class<? extends Model> c : list) {
            modelsDiscovered[i++] = c;
        }
        return modelsDiscovered;
    }

    /**
     * Use reflections to retrieve the supported samplers inside the classpath.
     *
     * @return
     */
    public static Class<? extends Sampler>[] discoverSamplers() {
        List<Class<? extends Sampler>> list = new ArrayList<>();
        Reflections reflections = new Reflections("gr.ntua.ece.cslab");
        for (Class<? extends Sampler> c : reflections.getSubTypesOf(Sampler.class)) {
            if (!c.getName().toLowerCase().contains("abstract")) {
                list.add(c);
            }
        }

        @SuppressWarnings("unchecked")
        Class<? extends Sampler>[] samplersDiscovered = new Class[list.size()];
        int i = 0;
        for (Class<? extends Sampler> c : list) {
            samplersDiscovered[i++] = c;
        }
        return samplersDiscovered;
    }

    /**
     * Method used to configure the benchmarking tool in order to set the
     * benchmark parameters.
     *
     * @param args
     * @throws Exception
     */
    public static void configure(String args[]) throws Exception {
        // cli arguments parsing
        cliOptionsSetup(args);
        CommandLineParser parser = new GnuParser();
        cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter format = new HelpFormatter();
            format.printHelp(Main.class.toString(), options);
            System.exit(0);
        }

        if (cmd.hasOption("list-models")) {
            for (Class<? extends Model> c : discoverModels()) {
                String className = c.getCanonicalName();
                String[] split = className.split("\\.");
                String shortName = split[split.length - 1];
                System.out.format("%s\t(%s)\n", className, shortName);
            }
            System.exit(1);
        }

        if (cmd.hasOption("list-samplers")) {
            for (Class<? extends Sampler> c : discoverSamplers()) {
                String className = c.getCanonicalName();
                String[] split = className.split("\\.");
                String shortName = split[split.length - 1];
                System.out.format("%s\t(%s)\n", className, shortName);
            }
            System.exit(1);
        }

        if (cmd.hasOption("i")) {
            inputFile = cmd.getOptionValue("i");
        } else {
//            System.err.println("Input file is necessary");
            HelpFormatter format = new HelpFormatter();
            format.printHelp(Main.class.toString(), options);
            System.exit(0);
        }

        if (cmd.hasOption("o")) {
            outputPrintStream = new PrintStream(cmd.getOptionValue("o"));
        } else {
            outputPrintStream = System.out;
        }

        if (cmd.hasOption("sr")) {
            samplingRate = new Double(cmd.getOptionValue("sr"));
        } else {
            samplingRate = 0.2;
        }

        if (!cmd.hasOption("db")) {
            System.err.println("Need database connection! (sqlite file)");
            System.exit(1);
        }  else {
            dbClient = new DatabaseClient();
            dbClient.setDatabaseName(cmd.getOptionValue("db"));
            if(!dbClient.openConnection()) {
                System.err.println("Could not open db! Exiting..");
                System.exit(1);
            }
        }
        
        

        instantiateModels();
        instantiateSamplers();

    }

    public static void instantiateModels() throws Exception {
        Class<? extends Model>[] modelsList = discoverModels();
        if (cmd.hasOption("m")) {
            String[] classNames = cmd.getOptionValue("m").split(",");
            models = new Model[classNames.length];
            for (int i = 0; i < classNames.length; i++) {
                String modelFound = classNames[i];
                for (Class f : modelsList) {
                    if (f.getCanonicalName().endsWith("." + classNames[i])) {
                        modelFound = f.getCanonicalName();
                    }
                }
                models[i] = (Model) Class.forName(modelFound).getConstructor().newInstance();
            }
        } else {
            int i = 0;
            models = new Model[discoverModels().length];
            for (Class<? extends Model> c : discoverModels()) {
                models[i++] = (Model) c.getConstructor().newInstance();
            }
        }
    }

    public static void instantiateSamplers() throws Exception {
        Class<? extends Sampler>[] samplersList = discoverSamplers();
        if (cmd.hasOption("st")) {
            String[] samplersArgs = cmd.getOptionValue("st").split(",");
            samplers = new Sampler[samplersArgs.length];
            int i = 0;
            for (String className : samplersArgs) {
                String classFound = className;
                for (Class f : samplersList) {
                    if (f.getCanonicalName().endsWith("." + className)) {
                        classFound = f.getCanonicalName();
                    }
                }
                samplers[i++] = (Sampler) Class.forName(classFound).getConstructor().newInstance();
            }
        } else {
            int i = 0;
            samplers = new Sampler[discoverSamplers().length];
            for (Class<? extends Sampler> c : discoverSamplers()) {
                samplers[i++] = (Sampler) c.getConstructor().newInstance();
            }
        }
    }

//    /**
//     * Create csv file template where each column contains information about a
//     * specific model.
//     *
//     * @param file needed to load input domain space and get labels
//     * @param sampler sampler object, used to write it to csv as comment
//     * @param picked
//     * @throws Exception
//     */
//    public static void createCSVForModels(CSVFileManager file, Sampler sampler, List<InputSpacePoint> picked) throws Exception {
//        OutputSpacePoint headerPoint = file.getOutputSpacePoints().get(0);
//        outputPrintStream.println("# Created: " + new Date());
//        outputPrintStream.println("# Active sampler: " + sampler.getClass().toString());
//        outputPrintStream.println("# Runtime options:");
//        for (Option p : cmd.getOptions()) {
//            outputPrintStream.println("#\t" + p.getLongOpt() + ":\t" + cmd.getOptionValue(p.getLongOpt()));
//        }
//        outputPrintStream.println("#");
//        outputPrintStream.println("# Points picked");
//        for (InputSpacePoint p : picked) {
//            outputPrintStream.println("# \t" + p);
//        }
//
//        for (String k : headerPoint.getInputSpacePoint().getKeysAsCollection()) {
//            outputPrintStream.print(k + "\t");
//        }
//        outputPrintStream.print(headerPoint.getKey() + "\t");
//
//        for (Model m : models) {
//            outputPrintStream.print(m.getClass().toString().substring(m.toString().lastIndexOf(".") + 7) + "\t");
//        }
//        outputPrintStream.println();
//
//        for (OutputSpacePoint p : file.getOutputSpacePoints()) {
//            outputPrintStream.print(p.getInputSpacePoint().toStringCSVFormat() + "\t");       // input space point
//            outputPrintStream.format("%.4f\t", p.getValue());
//            for (Model m : models) {
//                outputPrintStream.format("%.4f\t", m.getPoint(p.getInputSpacePoint()).getValue());
//            }
//            outputPrintStream.println();
//        }
//        outputPrintStream.println();
//        outputPrintStream.println();
//    }
//
//    public static void reportOnMetrics(CSVFileManager file, Sampler sampler, List<InputSpacePoint> sampled) {
////        if(cmd.hasOption("mo")) {
////            DatabaseClient writer = new DatabaseClient();
////            writer.setDatabaseName(cmd.getOptionValue("mo"));
////            writer.openConnection();
////            int index = sampler.getClass().toString().lastIndexOf('.');
////            String samplerName = sampler.getClass().toString().substring(index+1);
////            for(Model m : models) {
////                index = m.getClass().toString().lastIndexOf('.');
////                String modelName = m.getClass().toString().substring(index+1);
////                GlobalMetrics met = new GlobalMetrics(file.getOutputSpacePoints(), m, sampled);
////                writer.insertExperimentMetrics(samplerName, modelName, new Double(cmd.getOptionValue("sr")), met.getMSE(), met.getAverageError(), met.getDeviation(), met.getR());
////            }
////            writer.closeConnection();
////            return;
////        }
//        metricsOut.println("# Sampler used:\t" + sampler.getClass().toString());
//        metricsOut.println("# Date:\t" + new Date());
//        metricsOut.println("Model\tMSE\tAverage\tDeviation\tR");
//        for (Model m : models) {
//            GlobalMetrics met = new GlobalMetrics(file.getOutputSpacePoints(), m, sampled);
//            metricsOut.print(m.getClass().toString().substring(m.toString().lastIndexOf(".") + 7) + "\t\t\t");
//            metricsOut.format("%.5f\t%.5f\t%.5f\t%.5f", met.getMSE(), met.getAverageError(), met.getDeviation(), met.getR());
//            metricsOut.print("\n");
//        }
//    }
//    
//    public static void saveResults(int experimentId, CSVFileManager file, Sampler sampler, List<InputSpacePoint> picked) {
//        if(!cmd.hasOption("db"))
//            return;
//        // create a new experiment
//        DatabaseClient client = new DatabaseClient();
//        client.setDatabaseName(cmd.getOptionValue("db"));
//        client.openConnection();
////        int experimentId=client.insertExperiment();
//        
//        // save the metrics
//        String samplerName, modelName;
//        int index=0;
//        index = sampler.getClass().getCanonicalName().lastIndexOf('.');
//        samplerName= sampler.getClass().getCanonicalName().substring(index+1);
//        for(Model model:models) {
//            index = model.getClass().getCanonicalName().lastIndexOf('.');
//            modelName= model.getClass().getCanonicalName().substring(index+1);
//            GlobalMetrics met = new GlobalMetrics(file.getOutputSpacePoints(), model, picked);
////            client.insertExperimentMetrics(experimentId, samplerName, modelName,
////                    met.getMSE(), met.getAverageError(), met.getDeviation(), met.getR());
//        }
//        
//        // save the sampled points
//        
//        // save the predictions
//    }
}
