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
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is used as a benchmarking class, in order to compare different
 * modeling methods. The output is stored in a single csv file.
 *
 * @author Giannis Giannakopoulos
 */
public class Main extends Benchmark {

    public static void main(String[] args) throws Exception {

        long benchmarkStart = System.currentTimeMillis();
        configure(args);        // instantiating models and samplers

        for (Sampler s : samplers) {
            instantiateModels();
            instantiateSamplers();
            for (Model m : models) {
                m.configureClassifier();
            }
            
            if(s instanceof AbstractAdaptiveSampler) {
                AbstractAdaptiveSampler temp = (AbstractAdaptiveSampler) s;
                temp.setModel(models[0]);
            }

            CSVFileManager file = new CSVFileManager();
            file.setFilename(inputFile);

            // samplers initialization
            s.setSamplingRate(samplingRate);
            s.setDimensionsWithRanges(file.getDimensionRanges());

            s.configureSampler();

            // models training
            int i = 1;
            List<InputSpacePoint> picked = new LinkedList<>();
            System.out.println("Sampler:\t"+s.getClass().toString());
            while (s.hasMore()) {
                InputSpacePoint nextSample = s.next();
                picked.add(nextSample);
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
            createCSVForModels(file, s, picked);
            System.out.format("Done! [%d ms]\n", System.currentTimeMillis()-start);
            
            reportOnMetrics(file, s);
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
