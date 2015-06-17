package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.PrincipalComponentsAnalyzer;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This samplers utilizes a space-covering sampling technique to find relationships
 * between the dimensions of the input space and then orders the points by importance.
 * @author Giannis Giannakopoulos
 */
public class CompleteOrderingSampler extends AbstractAdaptiveSampler {
    
    private AbstractSampler firstPhaseSampler;
    private boolean inFirstPhase=true;
    public CompleteOrderingSampler() {
        super();
        this.inFirstPhase = true;
//        this.outputSpacePoints = new LinkedList<>();
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.firstPhaseSampler = new LatinHypercubeSampler();
        this.firstPhaseSampler.setDimensionsWithRanges(this.ranges);
        this.firstPhaseSampler.setSamplingRate(this.samplingRate/4.0);
        this.firstPhaseSampler.configureSampler();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint point = null;
        if(this.firstPhaseSampler.hasMore()) {
            point=this.firstPhaseSampler.next();
        } else if(this.inFirstPhase){
            // triger PCA and change inFirstPhase flag
            this.inFirstPhase = false;
            this.analyzeData();
        }
        System.err.println(point);
        return point;
    }
    
    
    private void analyzeData() {
        System.err.println("Analyzing data");
        PrincipalComponentsAnalyzer analyzer = new PrincipalComponentsAnalyzer();
        System.err.println(this.outputSpacePoints);
        analyzer.setInputData(this.outputSpacePoints);
        try {
            analyzer.calculateVarianceMatrix();
            analyzer.calculateCorrelationMatrix();
            analyzer.calculateBaseWithVarianceMatrix();
        } catch (Exception ex) {
            Logger.getLogger(CompleteOrderingSampler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addOutputSpacePoint(OutputSpacePoint outputSpacePoint) {
        super.addOutputSpacePoint(outputSpacePoint); //To change body of generated methods, choose Tools | Templates.
        System.err.println("Calling function!");
    }
    
    
}
