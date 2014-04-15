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
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
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
public class CompareModelsBenchmark extends Benchmark {

    public static void main(String[] args) throws Exception {

        long benchmarkStart = System.currentTimeMillis();
        configure(args);        // instantiating models and samplers

        for (Sampler s : samplers) {
            instantiateModels();
            instantiateSamplers();
            for (Model m : models) {
                m.configureClassifier();
            }

            CSVFileManager file = new CSVFileManager();
            file.setFilename(inputFile);

            // samplers initialization
            s.setSamplingRate(samplingRate);
            s.setDimensionsWithRanges(file.getDimensionRanges());

            s.configureSampler();

            // models training
            int i = 1;
            System.out.println("Sampler:\t"+s.getClass().toString());
            while (s.hasMore()) {
                InputSpacePoint nextSample = s.next();
                OutputSpacePoint out = file.getActualValue(nextSample);
                System.out.format("\t#%d point picked %s\n", i++, nextSample.toString());
                for (Model m : models) {
                    m.feed(out, false);
                }
            }
            System.out.print("Training models...\t\t");
            long start = System.currentTimeMillis();
            for (Model m : models) {
                m.train();
            }
            System.out.format("Done! [%d ms]\n", System.currentTimeMillis()-start);

            // models are created and the results are printed...
            System.out.print("Classifying instances...\t");
            start = System.currentTimeMillis();
            createCSVForModels(file, s);
            System.out.format("Done! [%d ms]\n", System.currentTimeMillis()-start);
        }
        System.out.println("Flushing output stream...");
        outputPrintStream.flush();
        System.out.format("Benchmark finished! [took %d ms]\n", System.currentTimeMillis()-benchmarkStart);

        // destroying and closing objects
        if (outputPrintStream != System.out) {
            outputPrintStream.close();
        }
    }

}
