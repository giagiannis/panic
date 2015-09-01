/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.client;

import gr.ntua.ece.cslab.panic.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.metrics.GlobalMetrics;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import gr.ntua.ece.cslab.panic.core.utils.DatabaseClient;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author giannis
 */
public class ExecutionThread extends Thread {

    private Sampler sampler;
    private int experimentId;
    private double samplingRate;
    private CSVFileManager file;
    private DatabaseClient dbClient;
    private Model[] models;
    private List<InputSpacePoint> picked;

    public ExecutionThread() {
    }

    public ExecutionThread(Sampler s, int experimentId, double samplingRate, CSVFileManager file, DatabaseClient dbClient, Model[] models) {
        this.sampler = s;
        this.experimentId = experimentId;
        this.samplingRate = samplingRate;
        this.file = file;
        this.dbClient = dbClient;
        this.models = models;
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        try {
            System.out.format("Sampler: %s\n", sampler.getClass().getSimpleName());
//        Benchmark.instantiateModels();
//            instantiateSamplers();

            // model initialization
            for (Model m : models) {
                m.configureClassifier();
            }

            // samplers initialization
            sampler.setSamplingRate(samplingRate);
            sampler.setDimensionsWithRanges(file.getDimensionRanges());
            sampler.configureSampler();

            // models training
            picked = new LinkedList<>();
            while (sampler.hasMore()) {
                InputSpacePoint nextSample = sampler.next();
                picked.add(nextSample);
                OutputSpacePoint out = file.getActualValue(nextSample);
                for (Model m : models) {
                    m.feed(out, false);
                }
                if (sampler instanceof AbstractAdaptiveSampler) {
                    ((AbstractAdaptiveSampler) sampler).addOutputSpacePoint(out);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trainAndFlushToDB() throws Exception {

        for (Model m : models) {
            m.train();
        }

        // write results to DB
        System.out.format("\tFlushing results to database... ");
        int index = sampler.getClass().getCanonicalName().lastIndexOf('.');
        String samplerShortName = sampler.getClass().getCanonicalName().substring(index + 1);
        dbClient.insertSampledPoints(experimentId, samplerShortName, picked);

        for (Model m : models) {
            index = m.getClass().getCanonicalName().lastIndexOf('.');
            String modelShortName = m.getClass().getCanonicalName().substring(index + 1);

            dbClient.insertModelPredictions(experimentId, modelShortName, samplerShortName, m.getPoints(file.getInputSpacePoints()));
            GlobalMetrics metrics = new GlobalMetrics(file.getOutputSpacePoints(), m, picked);

            dbClient.insertExperimentMetrics(experimentId, modelShortName, samplerShortName,
                    metrics.getMSE(), metrics.getAverageError(), metrics.getDeviation(), metrics.getR());
        }
        System.out.println("Done!");
    }

}
