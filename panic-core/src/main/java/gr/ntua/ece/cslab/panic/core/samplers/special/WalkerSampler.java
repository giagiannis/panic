package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpaceStep;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.List;

/**
 * This class simulates a walk on a multi-dimensional grid.
 * @author Giannis Giannakopoulos
 */
public class WalkerSampler extends AbstractAdaptiveSampler {

    private InputSpacePoint currentPoint;
    private List<InputSpacePoint> visitedPoints;
    
    public WalkerSampler() {
    }
    
    /**
     * Method with which the pointer is moved into the multi dimensional space.
     * @param step 
     */
    public void walk(InputSpaceStep step) {
        
    }
    
    // Getters and Setters

    public InputSpacePoint getCurrentPoint() {
        return currentPoint;
    }
    
    
    public static void main(String[] args) {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);
        
        WalkerSampler sampler = new WalkerSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.configureSampler();
    }    
}
