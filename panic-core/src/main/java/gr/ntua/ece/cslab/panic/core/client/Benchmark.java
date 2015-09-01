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

import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.utils.DatabaseClient;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
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
    protected static HashMap<String, String> configurations = new HashMap<>();

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
        
        options.addOption("c", "configuration", true, "passes configuration parameters to the samplers/models");

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
        
        if(cmd.hasOption("c")) {
            String configurationString=cmd.getOptionValue("c");
            String[] perModel = configurationString.split("\\|");
            for(String c:perModel) {
                String[] array=c.split(":");
                configurations.put(array[0], array[1]);
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
//            samplers = new Sampler[samplersArgs.length];
            List<Sampler> samplersListToArray = new LinkedList<>();
            for (String className : samplersArgs) {
                String classFound = className;
                for (Class f : samplersList) {
                    if (f.getCanonicalName().endsWith("." + className)) {
                        classFound = f.getCanonicalName();
                    }
                }
                Sampler sObject = (Sampler) Class.forName(classFound).getConstructor().newInstance();
                String shortName = classFound.substring(classFound.lastIndexOf('.')+1);
                int count=1;
                String conf = null;
                if(configurations.containsKey(shortName)) {
                    conf = configurations.get(shortName);
                    sObject.setConfiguration(configurations.get(shortName));
                    if(sObject.getConfiguration().containsKey("instances"))
                        count = new Integer(sObject.getConfiguration().get("instances"));
                }
                for(int i=0;i<count;i++) {
                    sObject = (Sampler) Class.forName(classFound).getConstructor().newInstance();
                    if(conf!=null)
                        sObject.setConfiguration(conf);
                    samplersListToArray.add(sObject);
                }
//                samplers[i++] = sObject;
            }
            samplers = new Sampler[samplersListToArray.size()];
            for(int i=0;i<samplers.length;i++)
                samplers[i] = samplersListToArray.get(i);
//            String[] samplersArgs = cmd.getOptionValue("st").split(",");
//            samplers = new Sampler[samplersArgs.length];
//            int i = 0;
//            for (String className : samplersArgs) {
//                String classFound = className;
//                for (Class f : samplersList) {
//                    if (f.getCanonicalName().endsWith("." + className)) {
//                        classFound = f.getCanonicalName();
//                    }
//                }
//                Sampler sObject = (Sampler) Class.forName(classFound).getConstructor().newInstance();
//                String shortName = classFound.substring(classFound.lastIndexOf('.')+1);
//                if(configurations.containsKey(shortName))
//                    sObject.setConfiguration(configurations.get(shortName));
//                samplers[i++] = sObject;
//            }
        } else {
            int i = 0;
            samplers = new Sampler[discoverSamplers().length];
            for (Class<? extends Sampler> c : discoverSamplers()) {
                samplers[i++] = (Sampler) c.getConstructor().newInstance();
            }
        }
    }
}
