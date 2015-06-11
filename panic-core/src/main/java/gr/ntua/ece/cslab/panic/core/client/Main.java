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
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import gr.ntua.ece.cslab.panic.core.utils.DatabaseClient;
import java.io.File;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is used as a benchmarking class, in order to compare different
 * modeling methods. The output is stored in a single CSV file.
 *
 * @author Giannis Giannakopoulos
 */
public class Main extends Benchmark {

    public static void main(String[] args) throws Exception {

        configure(args);        // instantiating models and samplers

        CSVFileManager file = new CSVFileManager();
        file.setFilename(inputFile);
        
        String fullPath = new File(inputFile).getCanonicalFile().getAbsolutePath() ;
        int experimentId = dbClient.insertExperiment(samplingRate, fullPath);
        
        for (Sampler s : samplers) {
            instantiateModels();
            instantiateSamplers();
            
            
            // model initialization
            for (Model m : models)
                m.configureClassifier();

            // samplers initialization
            s.setSamplingRate(samplingRate);
            s.setDimensionsWithRanges(file.getDimensionRanges());
            s.configureSampler();
            
            

            // models training
            List<InputSpacePoint> picked = new LinkedList<>();
            while (s.hasMore()) {
                InputSpacePoint nextSample = s.next();
                picked.add(nextSample);
                OutputSpacePoint out = file.getActualValue(nextSample);
                for (Model m : models) {
                    m.feed(out, false);
                }
                if( s instanceof AbstractAdaptiveSampler) {
                    ((AbstractAdaptiveSampler)s).addOutputSpacePoint(out);
                }
            }
            
            for (Model m : models) {
                m.train();
            }
            
            // write results to DB
            
            int index = s.getClass().getCanonicalName().lastIndexOf('.');
            String samplerShortName = s.getClass().getCanonicalName().substring(index+1);
            dbClient.insertSampledPoints(experimentId, samplerShortName, picked);
            
            
            for(Model m : models) {
                index = m.getClass().getCanonicalName().lastIndexOf('.');
                String modelShortName = m.getClass().getCanonicalName().substring(index+1);
                
                dbClient.insertModelPredictions(experimentId, modelShortName, samplerShortName, m.getPoints(file.getInputSpacePoints()));
                GlobalMetrics metrics = new GlobalMetrics(file.getOutputSpacePoints(), m, picked);
                
                dbClient.insertExperimentMetrics(experimentId, modelShortName, samplerShortName,
                        metrics.getMSE(), metrics.getAverageError(), metrics.getDeviation(), metrics.getR());
            }

        }
    }

}
