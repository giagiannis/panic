package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.Distributions;
import gr.ntua.ece.cslab.panic.core.samplers.utils.LoadingsAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This samplers utilizes a space-covering sampling technique to find relationships
 * between the dimensions of the input space and then orders the points by importance.
 * @author Giannis Giannakopoulos
 */
public class TotalOrderingSampler extends AbstractAdaptiveSampler {
    
    private AbstractSampler firstPhaseSampler;
    private boolean inFirstPhase=true;
    private String[] dimensionOrdering;
    
    private List<InputSpacePoint> futureSamples;
            
            
    public TotalOrderingSampler() {
        super();
        this.inFirstPhase = true;
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.firstPhaseSampler = new LatinHypercubeSampler();
        this.firstPhaseSampler.setDimensionsWithRanges(this.ranges);
        this.firstPhaseSampler.setSamplingRate(this.samplingRate/4.0);
        this.firstPhaseSampler.configureSampler();
        this.futureSamples = new LinkedList<>();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        if(this.firstPhaseSampler.hasMore()) {
            return this.firstPhaseSampler.next();
        } else if(this.inFirstPhase){
            // triger PCA and change inFirstPhase flag
            this.inFirstPhase = false;
            this.analyzeData();
        }
        System.err.format("Has more: %s, listSize: %d\n", this.hasMore(), this.futureSamples.size());
        return this.futureSamples.remove(0);
    }
    
    
    private void analyzeData() {
        PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
        analyzer.setInputData(this.outputSpacePoints);
        try {
            analyzer.calculateVarianceMatrix();
            analyzer.calculateCorrelationMatrix();
        } catch (Exception ex) {
            Logger.getLogger(TotalOrderingSampler.class.getName()).log(Level.SEVERE, null, ex);
        }
        analyzer.calculateBaseWithVarianceMatrix();
        LoadingsAnalyzer loadingsAnalyzer=analyzer.getLoadingsAnalyzer(2);
        this.dimensionOrdering=loadingsAnalyzer.getInputDimensionsOrder(analyzer.getPCWeights());
        
        int numberOfSamplesToPick = (int)Math.ceil(this.samplingRate*this.maxChoices)-this.pointsPicked+1;
        System.err.println("I need "+numberOfSamplesToPick+" more points");
        double pivot=((double) this.maxChoices)/numberOfSamplesToPick;
        
        for(double i=0.0; i<=this.maxChoices-1;i+=pivot) {
            int fromIndex = (int) Math.round(i), toIndex = (int) Math.round(i+pivot);
            InputSpacePoint point = this.getPointById(Distributions.getRandomIndex(fromIndex, toIndex));
            this.futureSamples.add(point);
        }
        Collections.shuffle(this.futureSamples);
    }

    /**
     * The points are reordered according to the dimensions ordering returned by 
     * the principal components.
     * @param id
     * @return 
     */
    @Override
    protected InputSpacePoint getPointById(int id) {
        InputSpacePoint point = new InputSpacePoint();
        for(int index=this.dimensionOrdering.length-1;index>=0;index--) {
            String key = this.dimensionOrdering[index];
            int modulo = id % this.ranges.get(key).size();
            point.addDimension(key, this.ranges.get(key).get(modulo));
            id = id/this.ranges.get(key).size();
        }
        return point;
    }
}
