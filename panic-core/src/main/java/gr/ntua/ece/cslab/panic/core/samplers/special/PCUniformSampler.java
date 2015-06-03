/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.BorderPointsEstimator;
import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a PC-based Uniform sampler. The idea is that each
 * dimension is prioritized and the dimensions that appear to have the highest
 * importance are sampled more often than the others.
 *
 * @author Giannis Giannakopoulos
 */
public class PCUniformSampler extends AbstractAdaptiveSampler {
    
    private Integer firstPhaseThreshold;
    private BorderPointsEstimator borderPointSampler;
    private List<OutputSpacePoint> outputSpacePoints;
    
    private PrincipalComponentsAnalyzer analyzer;
    
    public PCUniformSampler() {
        this.outputSpacePoints = new LinkedList<>();
    }

    public Integer getFirstPhaseThreshold() {
        return firstPhaseThreshold;
    }

    public void setFirstPhaseThreshold(Integer firstPhaseThreshold) {
        this.firstPhaseThreshold = firstPhaseThreshold;
    }

    @Override
    public InputSpacePoint next() {
//        super.next();
        if(this.pointsPicked<this.firstPhaseThreshold && this.borderPointSampler.hasMorePoints()) {
            return this.borderPointSampler.getBorderPoint();
        } else
            return null;
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.borderPointSampler = new BorderPointsEstimator();
        this.borderPointSampler.setRanges(this.ranges);
        this.borderPointSampler.estimatePoints();
    }
    
    public void analyze() throws Exception {
        
        // execute PCA analysis
        this.analyzer = new PrincipalComponentsAnalyzer();
        this.analyzer.setInputData(this.outputSpacePoints);
        this.analyzer.calculateVarianceMatrix();
        this.analyzer.calculateCorrelationMatrix();
//        this.analyzer.calculateBaseWithVarianceMatrix();
        this.analyzer.calculateBaseWithCorrelationMatrix();
//        this.analyzer.calculateBaseWithDataMatrix();
        
        // check how many principal components are needed
        int pcs=0;
        while(this.analyzer.getEigenValueAggregatedScore(pcs)<0.999)
            pcs++;
        pcs++;
        
        // create the loadings double[][]
        double[][] loadings = new double[this.analyzer.getRank()][pcs];
        String[] labels = new String[this.analyzer.getRank()];
        
        int index=0;
        Iterator<String> it= this.outputSpacePoints.get(0).getInputSpacePoint().getKeysAsCollection().iterator();
        while(it.hasNext())
            labels[index++]=it.next();
        labels[index++] = this.outputSpacePoints.get(0).getKey();
        for(int i=0;i<this.analyzer.getRank();i++)
            for(int j=0;j<pcs;j++) 
                loadings[i][j] = this.analyzer.getLoading(j, i);
        
        LoadingsAnalyzer loadingsAnalyzer = new LoadingsAnalyzer(loadings);
        loadingsAnalyzer.setDimensionLabels(labels);
        System.out.println(loadingsAnalyzer);
        System.err.println("Distance matrix");
        System.err.println(loadingsAnalyzer.toStringDistanceMatrix(this.analyzer.getPCWeights(pcs)));
        
        System.err.println("Similarity matrix");
        System.err.println(loadingsAnalyzer.toStringSimilarityMatrix(this.analyzer.getPCWeights(pcs)));
        
    }
    
    public void addOutputSpacePoint(OutputSpacePoint point) {
        this.outputSpacePoints.add(point);
    }
    

    public static void main(String[] args) throws Exception {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        PCUniformSampler sampler = new PCUniformSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setSamplingRate(.0);
        sampler.setFirstPhaseThreshold(8);
        sampler.configureSampler();
        
        List<OutputSpacePoint> points = new LinkedList<>();
        for(int i=0;i<8;i++)
            points.add(file.getActualValue(sampler.next()));
        
//        System.err.println(points);
        for(OutputSpacePoint p:points)
            sampler.addOutputSpacePoint(p);
        sampler.analyze();
//        System.out.println("");
        
//
//        List<OutputSpacePoint> points = new LinkedList<>();
//        while (sampler.hasMore()) {
//            InputSpacePoint sample = sampler.next();
//            points.add(file.getActualValue(sample));
//        }
        
//
//        PrincipalComponentsAnalyzer comps = new PrincipalComponentsAnalyzer();
//        comps.setInputData(points);
//        comps.calculateVarianceMatrix();
//        comps.calculateCorrelationMatrix();
//
//        comps.calculateBaseWithCorrelationMatrix();
//
//        System.err.println("EigenValues info");
//        for (int i = 0; i < comps.getRank(); i++) {
//            System.err.format("%d\t%.5f\t%.5f\t%.5f\n", i, comps.getEigenValue(i), comps.getEigenValueScore(i), comps.getEigenValueAggregatedScore(i));
//        }
//
//        for (int i = 0; i < comps.getRank(); i++) {
//            System.out.print((i + 1) + "\t");
//            for (int j = 0; j < 2; j++) {
//                System.out.print(comps.getEigenVector(j).getData()[i] + "\t");
//            }
//            System.out.println("");
//        }
//
//        System.out.println("");
//        System.out.println("");
//
//        for (OutputSpacePoint p : points) {
//            System.out.println(comps.getEigenSpacePoint(p, 2).toStringCSV());
//        }

    }

}
