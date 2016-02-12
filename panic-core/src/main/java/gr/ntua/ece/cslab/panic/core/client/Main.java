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
package gr.ntua.ece.cslab.panic.core.client;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.metrics.GlobalMetrics;
import gr.ntua.ece.cslab.panic.core.models.EnsembleMetaModel;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.samplers.special.BiasedPCASampler;
import gr.ntua.ece.cslab.panic.core.samplers.special.RandomPartitioningSampler;
import gr.ntua.ece.cslab.panic.core.samplers.special.TreePartitioningSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;

import java.io.File;
import java.util.HashMap;
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

        String fullPath = new File(inputFile).getCanonicalFile().getAbsolutePath();
        int experimentId = dbClient.insertExperiment(samplingRate, fullPath, configurations.toString());

        System.out.format("Experiment id: %d\n", experimentId);
//        List<ExecutionThread> threads = new LinkedList<>();
        List<HashMap<String, List<Double>>> leafRegions = null;
        for (Sampler s : samplers) {
            instantiateModels();
//            Model[] localModels = new Model[models.length];
//            System.arraycopy(models, 0, localModels, 0, models.length);
//            ExecutionThread t = new ExecutionThread(s, experimentId, samplingRate, file, dbClient, localModels);
//            threads.add(t);

            System.out.format("Sampler: %s\n", s.getClass().getSimpleName());
            instantiateModels();
//            instantiateSamplers();

            // model initialization
            for (Model m : models) {
                m.configureClassifier();
            }

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
                if (s instanceof AbstractAdaptiveSampler) {
                    ((AbstractAdaptiveSampler) s).addOutputSpacePoint(out);
                }
            }

            for (Model m : models) {
                RegionTree tree = null;
                if (m instanceof EnsembleMetaModel ) {
                    if(s instanceof BiasedPCASampler) {
                        tree = ((BiasedPCASampler) s).getRegionTree();
                    } else if (s instanceof RandomPartitioningSampler) {
                        tree = ((RandomPartitioningSampler) s).getRegionTree();
                    } else if (s instanceof TreePartitioningSampler) {
                        tree = ((TreePartitioningSampler) s).getRegionTree();
                    } else {
                        tree = null;
                    }
                }
                if (tree != null && m instanceof EnsembleMetaModel) {
                    ((EnsembleMetaModel) m).setRegions(tree.getLeafRegions());
//                    System.out.println("Leaf regions: "+tree.getLeafRegions().size());
                    for(RegionTreeNode n :tree.getDFSOrdering()) {
                        for(int i=0;i<n.getLevel();i++)
                            System.out.print("\t");
                        System.out.println(n);
                    }
                } else {
                    System.err.println("Leaf regions is null or model not EnsembleModel");
                }
                m.train();
            }

            // write results to DB
            System.out.format("\tFlushing results to database... ");
            int index = s.getClass().getCanonicalName().lastIndexOf('.');
            String samplerShortName = s.getClass().getCanonicalName().substring(index + 1);
            if (saveSamples) {
                dbClient.insertSampledPoints(experimentId, samplerShortName, picked);
            }

            for (Model m : models) {
                index = m.getClass().getCanonicalName().lastIndexOf('.');
                String modelShortName = m.getClass().getCanonicalName().substring(index + 1);

                if (savePredictions) {
                    dbClient.insertModelPredictions(experimentId, modelShortName, samplerShortName, m.getPoints(file.getInputSpacePoints()));
                }

                GlobalMetrics metrics = new GlobalMetrics(file.getOutputSpacePoints(), m, picked);
                dbClient.insertExperimentMetrics(experimentId, modelShortName, samplerShortName,
                        metrics.getMSE(), metrics.getAverageError(), metrics.getDeviation(), metrics.getR());
            }
            System.out.println("Done!");
        }
//        for(HashMap h: leafRegions) {
//        	System.err.println(h);
//        }
    }

}
