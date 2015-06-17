package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.GridSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class implements a Grid sampler that dynamically adjusts  the number of
 * values chosen per dimension. The calculation of the number of values needed for
 * each dimensions is conducted through Principal Components Analysis and Factor Analysis
 * and the Grid is implemented through the Grid Sampler
 *
 * @author Giannis Giannakopoulos
 * @see GridSampler
 */
public class DimensionWeightSampler extends AbstractAdaptiveSampler {

    private Integer firstPhaseThreshold=0;
    private final AbstractSampler firstPhaseSampler;

    private GridSampler gridSampler;

    
    // constructors getters and setters
    /**
     * Default constructor
     */
    public DimensionWeightSampler() {
        this.outputSpacePoints = new LinkedList<>();
        this.firstPhaseSampler = new GridSampler();
//        this.firstPhaseSampler.set
    }

    /**
     * Returns the first phase threshold number. By default this is equal to 
     * 2^(number of dimensions). 
     * @return 
     */
    public Integer getFirstPhaseThreshold() {
        return firstPhaseThreshold;
    }

    /**
     * Sets the first phase threshold number.
     * @param firstPhaseThreshold 
     */
    public void setFirstPhaseThreshold(Integer firstPhaseThreshold) {
        this.firstPhaseThreshold = firstPhaseThreshold;
    }
    
    
    // public sampler interface
    @Override
    public InputSpacePoint next() {
        super.next();
        if (this.pointsPicked <= this.firstPhaseThreshold && this.firstPhaseSampler.hasMore()) {
            return this.firstPhaseSampler.next();
        }
        if (this.gridSampler == null || !this.gridSampler.hasMore()) {
            this.gridSampler = this.configureGridSampler();
        }
        return this.gridSampler.next();
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        if(this.firstPhaseThreshold==0)
            this.firstPhaseThreshold = (int) Math.round(Math.pow(2, this.ranges.size()));
        this.firstPhaseSampler.setDimensionsWithRanges(this.ranges);
        this.firstPhaseSampler.setSamplingRate(this.samplingRate/2);
        this.firstPhaseSampler.configureSampler();
    }

    
    // util methods
    
    /**
     * Configures the grid sampler. Conducts the PC or Factor Analysis, 
     * and estimates the weights for the Grid.
     * @return 
     */
    private GridSampler configureGridSampler() {

        // execute PCA analysis
        PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
        analyzer.setInputData(this.outputSpacePoints);
        try {
            analyzer.calculateVarianceMatrix();
            analyzer.calculateCorrelationMatrix();
        } catch (Exception e) {
            Logger.getLogger(DimensionWeightSampler.class.toString()).severe(e.getMessage());
        }
        analyzer.calculateBaseWithVarianceMatrix();
        int pcs = 2;

        // create the loadings double[][]
        double[][] loadings = new double[analyzer.getRank()][pcs];
        String[] labels = analyzer.getLabels();
        
        for (int i = 0; i < analyzer.getRank(); i++) {
            for (int j = 0; j < pcs; j++) {
                loadings[i][j] = analyzer.getLoading(j, i);
            }
        }

        // instantiate LoadingsAnalyzer
        LoadingsAnalyzer loadingsAnalyzer = new LoadingsAnalyzer(loadings);
        loadingsAnalyzer.setDimensionLabels(labels);
        double[] pcWeights = analyzer.getPCWeights();

        // creates the weights for each dimension
        GridSampler sampler = new GridSampler();
        sampler.setDimensionsWithRanges(ranges);
        sampler.setSamplingRate(this.samplingRate);
        
        System.out.println(loadingsAnalyzer.toStringDistanceMatrix(pcWeights));
        System.out.println(loadingsAnalyzer.toString());

        HashMap<String, Double> weights = new HashMap<>();
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
}
