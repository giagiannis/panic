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
package gr.ntua.ece.cslab.panic.server.utils;

import gr.ntua.ece.cslab.panic.server.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.server.models.Model;
import gr.ntua.ece.cslab.panic.server.samplers.Sampler;
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
    protected static PrintStream outputPrintStream;
    protected static String inputFile;
    protected static Sampler[] samplers;
    protected static Model[] models;
    protected static CommandLine cmd;

    /**
     * Method used to setup the commons-cli argument parsing. Each implemented
     * benchmark may extend this list at will.
     *
     * @param args
     */
    public static void cliOptionsSetup(String[] args) {
        options = new Options();

        options.addOption("h", "help", false, "prints this help message");

        options.addOption("o", "output", true, "define the output file\ndefault: stdout");
        options.getOption("o").setArgName("output");

        options.addOption("i", "input", true, "define the input file");
        options.getOption("i").setArgName("input");

        options.addOption("sr", "sampling-rate", true, "determines the sampling rate of the dataset (0.0 - 1.0)\ndefault: 0.2");
        options.getOption("sr").setArgName("rate");

        options.addOption("st", "sampling-type", true, "determines the sampling type (if not defined all the available models will be used)");
        options.getOption("st").setArgName("type");

        options.addOption("m", "models", true, "define the models to use (if not defined, all the available models will be trained");
        options.getOption("m").setArgName("model1,model2");

        options.addOption(null, "list-models", false, "lists the available models");
        options.addOption(null, "list-samplers", false, "lists the available samplers");

    }

    /**
     * Use reflections to retrieve the supported models inside the classpath.
     *
     * @return
     */
    public static Class[] discoverModels() {
        List<Class> list = new ArrayList<>();
        Reflections reflections = new Reflections("gr.ntua.ece.cslab");
        for (Class c : reflections.getSubTypesOf(Model.class)) {
            if (!c.getName().toLowerCase().contains("abstract")) {
                list.add(c);
            }
        }
        Class[] modelsDiscovered = new Class[list.size()];
        int i = 0;
        for (Class c : list) {
            modelsDiscovered[i++] = c;
        }
        return modelsDiscovered;
    }

    /**
     * Use reflections to retrieve the supported samplers inside the classpath.
     *
     * @return
     */
    public static Class[] discoverSamplers() {
        List<Class> list = new ArrayList<>();
        Reflections reflections = new Reflections("gr.ntua.ece.cslab");
        for (Class c : reflections.getSubTypesOf(Sampler.class)) {
            if (!c.getName().toLowerCase().contains("abstract")) {
                list.add(c);
            }
        }
        Class[] samplersDiscovered = new Class[list.size()];
        int i = 0;
        for (Class c : list) {
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
            format.printHelp(CompareModelsBenchmark.class.toString(), options);
            System.exit(0);
        }

        if (cmd.hasOption("list-models")) {
            for (Class c : discoverModels()) {
                System.out.println(c.toString());
            }
            System.exit(1);
        }

        if (cmd.hasOption("list-samplers")) {
            for (Class c : discoverSamplers()) {
                System.out.println(c.toString());
            }
            System.exit(1);
        }

        if (cmd.hasOption("i")) {
            inputFile = cmd.getOptionValue("i");
        } else {
            System.err.println("Please use -h flag to see the necessary options...");
            System.exit(1);
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
        
        instantiateModels();
        instantiateSamplers();

    }

    public static void instantiateModels() throws Exception {
        if (cmd.hasOption("m")) {
            String[] classNames = cmd.getOptionValue("m").split(",");
            models = new Model[classNames.length];
            for (int i = 0; i < classNames.length; i++) {
                models[i] = (Model) Class.forName(classNames[i]).getConstructor().newInstance();
            }
        } else {
            int i = 0;
            models = new Model[discoverModels().length];
            for (Class c : discoverModels()) {
                models[i++] = (Model) c.getConstructor().newInstance();
            }
        }
    }

    public static void instantiateSamplers() throws Exception {
        if (cmd.hasOption("st")) {
            String[] samplersArgs = cmd.getOptionValue("st").split(",");
            samplers = new Sampler[samplersArgs.length];
            int i = 0;
            for (String className : samplersArgs) {
                samplers[i++] = (Sampler) Class.forName(className).getConstructor().newInstance();
            }
        } else {
            int i=0;
            samplers = new Sampler[discoverSamplers().length];
            for(Class c : discoverSamplers())
                samplers[i++] = (Sampler) c.getConstructor().newInstance();
        }
    }

    /**
     * Create csv file template where each column contains information about a
     * specific model.
     *
     * @param file needed to load input domain space and get labels
     * @param sampler sampler object, used to write it to csv as comment
     * @throws Exception
     */
    public static void createCSVForModels(CSVFileManager file, Sampler sampler) throws Exception {
        OutputSpacePoint headerPoint = file.getOutputSpacePoints().get(0);
        outputPrintStream.println("# Created: " + new Date());
        outputPrintStream.println("# Active sampler: "+sampler.getClass().toString());
        outputPrintStream.println("# Runtime options:");
        for (Option p : cmd.getOptions()) {
            outputPrintStream.println("#\t" + p.getLongOpt() + ":\t" + cmd.getOptionValue(p.getLongOpt()));
        }

        for (String k : headerPoint.getInputSpacePoint().getKeysAsCollection()) {
            outputPrintStream.print(k + "\t");
        }
        outputPrintStream.print(headerPoint.getKey() + "\t");

        for (Model m : models) {
            outputPrintStream.print(m.getClass().toString().substring(m.toString().lastIndexOf(".") + 7) + "\t");
        }
        outputPrintStream.println();

        for (OutputSpacePoint p : file.getOutputSpacePoints()) {
            outputPrintStream.print(p.getInputSpacePoint().toStringCSVFormat() + "\t");       // input space point
            outputPrintStream.format("%.4f\t", p.getValue());
            for (Model m : models) {
                outputPrintStream.format("%.4f\t", m.getPoint(p.getInputSpacePoint()).getValue());
            }
            outputPrintStream.println();
        }
        outputPrintStream.println();
        outputPrintStream.println();
    }
}
