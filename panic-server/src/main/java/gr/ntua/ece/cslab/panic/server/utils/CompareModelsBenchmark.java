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

import gr.ntua.ece.cslab.panic.server.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.server.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.server.models.Model;
import gr.ntua.ece.cslab.panic.server.samplers.RandomSampler;
import gr.ntua.ece.cslab.panic.server.samplers.Sampler;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.reflections.Reflections;
/**
 * This class is used as a benchmarking class, in order to compare different
 * modeling methods. The output is stored in a single csv file.
 *
 * @author Giannis Giannakopoulos
 */
public class CompareModelsBenchmark {

    private static Options options;
    private static Class[] defaultModels = null;
    private static Class[] defaultSamplers = null;

    public static void cliOptionsSetup(String[] args) {
        options = new Options();
        
        options.addOption("h", "help", false, "prints this help message");
        
        options.addOption("o", "output", true, "define the output file\ndefault: stdout");
        options.getOption("o").setArgName("output");
        
        options.addOption("i", "input", true, "define the input file");
        options.getOption("i").setArgName("input");
        
        options.addOption("sr", "sampling-rate", true, "determines the sampling rate of the dataset (0.0 - 1.0)\ndefault: 0.2");
        options.getOption("sr").setArgName("rate");
        
        options.addOption("st", "sampling-type", true, "determines the sampling type\ndefault:random (only this is supported for now)");
        options.getOption("st").setArgName("type");
        
        options.addOption("m", "models", true, "define the models to use (if not defined, all the available models will be trained");
        options.getOption("m").setArgName("model1,model2");
        
        options.addOption(null, "list-models", false, "lists the available models");
        options.addOption(null, "list-samplers", false, "lists the available samplers");
        
    }
    
    public static void discoverModels() {
        List<Class> list = new ArrayList<>();
        Reflections reflections = new Reflections("gr.ntua.ece.cslab");
        for(Class c: reflections.getSubTypesOf(Model.class)){
            if(!c.getName().toLowerCase().contains("abstract")){
                list.add(c);
            }
        }
        defaultModels = new Class[list.size()];
        int i = 0;
        for(Class c : list)
            defaultModels[i++] = c;
    }
    
    public static void discoverSamplers() {
        List<Class> list = new ArrayList<>();
        Reflections reflections = new Reflections("gr.ntua.ece.cslab");
        for(Class c: reflections.getSubTypesOf(Sampler.class)){
            if(!c.getName().toLowerCase().contains("abstract")){
                list.add(c);
            }
        }
        defaultSamplers = new Class[list.size()];
        int i = 0;
        for(Class c : list)
            defaultSamplers[i++] = c;
    }
    
    public static void main(String[] args) throws Exception{
        discoverModels();
        discoverSamplers();
//        System.exit(1);
        String infile = null;
        PrintStream outputPrintStream = null;
        Double samplingRate = null;
        Sampler sampler = null;
        
        // cli arguments parsing
        cliOptionsSetup(args);
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, args);
        
        if(cmd.hasOption("h")) {
            HelpFormatter format = new HelpFormatter();
            format.printHelp(CompareModelsBenchmark.class.toString(), options);
            System.exit(0);
        }
        
        if(cmd.hasOption("list-models")) {
            for(Class c : defaultModels) {
                System.out.println(c.toString());
            }
            System.exit(1);
        }
        
        if(cmd.hasOption("list-samplers")) {
            for(Class c : defaultSamplers) {
                System.out.println(c.toString());
            }
            System.exit(1);
        }
        
        if(cmd.hasOption("i")){
            infile = cmd.getOptionValue("i");
        } else {
            System.err.println("input file not specified");
            System.exit(1);
        }
        
        if(cmd.hasOption("o")){
            outputPrintStream = new PrintStream(cmd.getOptionValue("o"));
        } else {
            outputPrintStream = System.out;
        }
        
        if(cmd.hasOption("sr")) {
            samplingRate = new Double(cmd.getOptionValue("sr"));
        } else
            samplingRate = 0.2;
        
        if(cmd.hasOption("st")) {
            Class samplerClass = Class.forName(cmd.getOptionValue("st"));
            sampler = (Sampler) samplerClass.getConstructor().newInstance();
        } else {
            sampler = new RandomSampler();
        }
        
        Class[] modelClasses = defaultModels;
        if(cmd.hasOption("m")) {
            String[] classNames = cmd.getOptionValue("m").split(",");
            modelClasses = new Class[classNames.length];
            for(int i=0;i<classNames.length;i++){
                modelClasses[i] = Class.forName(classNames[i]);
            }
        }




        // model initialization
        Model[] models = new Model[modelClasses.length];
        int i=0;
        for(Class c : modelClasses){
            models[i++] = (Model) c.getConstructor().newInstance();
        }
        
        for(Model m : models) {
            m.configureClassifier();
        }
        
        
        CSVFileManager file = new CSVFileManager();
        file.setFilename(infile);
        
        // sampler initialization
        sampler.setSamplingRate(samplingRate);
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        
        sampler.configureSampler();
        

        // model training
        i=1;
        while(sampler.hasMore()) {
            InputSpacePoint nextSample = sampler.next();
            
            OutputSpacePoint out = file.getActualValue(nextSample);
            System.out.format("%d round (%s)\n", i++, nextSample.toString());
            for(Model m : models) {
                m.feed(out, false);
            }
        }
        
        for(Model m : models)
            m.train();
        

        // models are created and the results are printed...
        createCSVFormat(outputPrintStream, models, file,samplingRate);
     
        
        // destroying and closing objects
        if(outputPrintStream != System.out)
            outputPrintStream.close();
        
    }
    
    public static void createCSVFormat(PrintStream outputPrintStream, Model[] models, CSVFileManager file, double sr) throws Exception {
        OutputSpacePoint headerPoint = file.getOutputSpacePoints().get(0);
        outputPrintStream.println("# Created: "+new Date());
        outputPrintStream.println("# Sampling rate: "+sr);
        outputPrintStream.println("# Sampling method: random");
        for(String k : headerPoint.getInputSpacePoint().getKeysAsCollection()) {
            outputPrintStream.print(k+"\t");
        }
        outputPrintStream.print(headerPoint.getKey()+"\t");
        
        for(Model m: models)
            outputPrintStream.print(m.getClass().toString().substring(m.toString().lastIndexOf("."))+"\t");
        outputPrintStream.println();
        
        
        for(OutputSpacePoint p : file.getOutputSpacePoints()){
            outputPrintStream.print(p.getInputSpacePoint().toStringCSVFormat()+"\t");       // input space point
            outputPrintStream.format("%.4f\t", p.getValue());
            for(Model m : models)
                outputPrintStream.format("%.4f\t", m.getPoint(p.getInputSpacePoint()).getValue());
            outputPrintStream.println();
        }

    }
}
