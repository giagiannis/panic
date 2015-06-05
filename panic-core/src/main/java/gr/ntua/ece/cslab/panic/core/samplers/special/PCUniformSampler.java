/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.GridSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.BorderPointsEstimator;
import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class implements a PC-based Uniform sampler. The idea is that each
 * dimension is prioritized and the dimensions that appear to have the highest
 * importance are sampled more often than the others.
 *
 * @author Giannis Giannakopoulos
 */
public class PCUniformSampler extends AbstractAdaptiveSampler {

    private Integer firstPhaseThreshold;
    private final BorderPointsEstimator borderPointSampler;

    private GridSampler gridSampler;

    public PCUniformSampler() {
        this.outputSpacePoints = new LinkedList<>();
        this.borderPointSampler = new BorderPointsEstimator();
    }

    public Integer getFirstPhaseThreshold() {
        return firstPhaseThreshold;
    }

    public void setFirstPhaseThreshold(Integer firstPhaseThreshold) {
        this.firstPhaseThreshold = firstPhaseThreshold;
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        if (this.pointsPicked <= this.firstPhaseThreshold && this.borderPointSampler.hasMorePoints()) {
            return this.borderPointSampler.getBorderPoint();
        }
        if (this.gridSampler == null || !this.gridSampler.hasMore()) {
            System.err.println("Time to analyze the data!!");
            this.gridSampler = this.configureGridSampler();
        }
        return this.gridSampler.next();
//        return null;
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.firstPhaseThreshold = (int) Math.round(Math.pow(2, this.ranges.size()));
        this.borderPointSampler.setRanges(this.ranges);
        this.borderPointSampler.estimatePoints();
    }

    private GridSampler configureGridSampler() {

        // execute PCA analysis
        PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
        analyzer.setInputData(this.outputSpacePoints);
        try {
            analyzer.calculateVarianceMatrix();
            analyzer.calculateCorrelationMatrix();
        } catch (Exception e) {
            Logger.getLogger(PCUniformSampler.class.toString()).severe(e.getMessage());
        }
        analyzer.calculateBaseWithVarianceMatrix();
//        this.analyzer.calculateBaseWithCorrelationMatrix();
//        this.analyzer.calculateBaseWithDataMatrix();

        // check how many principal components are needed
//        int pcs=0;
//        while(this.analyzer.getEigenValueAggregatedScore(pcs)<0.999)
//            pcs++;
//        pcs++;
        int pcs = 2;

        // create the loadings double[][]
        double[][] loadings = new double[analyzer.getRank()][pcs];
        String[] labels = new String[analyzer.getRank()];

        int index = 0;
        Iterator<String> it = this.outputSpacePoints.get(0).getInputSpacePoint().getKeysAsCollection().iterator();
        while (it.hasNext()) {
            labels[index++] = it.next();
        }
        labels[index++] = this.outputSpacePoints.get(0).getKey();
        for (int i = 0; i < analyzer.getRank(); i++) {
            for (int j = 0; j < pcs; j++) {
                loadings[i][j] = analyzer.getLoading(j, i);
            }
        }

        // instantiante LoadingsAnalyzer
        LoadingsAnalyzer loadingsAnalyzer = new LoadingsAnalyzer(loadings);
        loadingsAnalyzer.setDimensionLabels(labels);
        double[] pcWeights = analyzer.getPCWeights();

        // creates the weights for each dimension
        GridSampler sampler = new GridSampler();
        sampler.setDimensionsWithRanges(ranges);
        sampler.setSamplingRate(this.samplingRate);

        HashMap<String, Double> weights = new HashMap<>();
//        double[] weights = new double[labels.length];
        for (int i = 0; i < labels.length - 1; i++) {
            Double score = 1.0 / loadingsAnalyzer.getDistance(i, pcWeights);
            weights.put(labels[i], score);
        }
        sampler.setWeights(weights);

        Set<InputSpacePoint> forbidden = new HashSet<>();
        for (OutputSpacePoint p : this.outputSpacePoints) {
            forbidden.add(p.getInputSpacePoint());
        }

        sampler.setForbiddenPoints(forbidden);

        sampler.configureSampler();

        return sampler;
    }

    public static void main(String[] args) throws Exception {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        PCUniformSampler sampler = new PCUniformSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setSamplingRate(.2);
        sampler.configureSampler();

        while (sampler.hasMore()) {
            InputSpacePoint in = sampler.next();
            OutputSpacePoint out = file.getActualValue(in);
            sampler.addOutputSpacePoint(out);
            System.err.format("Input point: %s, Output point: %s\n", in.toString(), out.toString());
        }
//        List<OutputSpacePoint> points = new LinkedList<>();
//        for(int i=0;i<8;i++)
//            points.add(file.getActualValue(sampler.next()));
//        
////        System.err.println(points);
//        for(OutputSpacePoint p:points)
//            sampler.addOutputSpacePoint(p);
//        sampler.analyze();

    }

}
