package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.beans.OutputSpacePoint;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements various method from the Sampler interface, used as a base class
 * for the implementation of various adaptive sampling methods.
 * @author Giannis Giannakopoulos
 */
public abstract class AbstractAdaptiveSampler extends AbstractSampler{
    
    protected List<OutputSpacePoint> outputSpacePoints;
    
    /**
     * Default constructor.
     */
    public AbstractAdaptiveSampler() {
        this.outputSpacePoints = new LinkedList<>();
    }

    public List<OutputSpacePoint> getOutputSpacePoints() {
        return outputSpacePoints;
    }

    public void setOutputSpacePoints(List<OutputSpacePoint> outputSpacePoints) {
        this.outputSpacePoints = outputSpacePoints;
    }
    
    public void addOutputSpacePoint(OutputSpacePoint outputSpacePoint) {
        if(this.outputSpacePoints!=null) {
            this.outputSpacePoints.add(outputSpacePoint);
        }
    }

}
