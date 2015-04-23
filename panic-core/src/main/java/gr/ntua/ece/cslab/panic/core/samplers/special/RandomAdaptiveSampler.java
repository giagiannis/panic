package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import java.util.Random;

/**
 * This class is yet another implementation of the typical Greedy Adaptive Sampling
 * algorithm (as described in the PANIC paper), enhanced with exploration features. 
 * At each call, the sampler may return a random point with a specific probability.
 * @author Giannis Giannakopoulos
 */
public class RandomAdaptiveSampler extends AbstractAdaptiveSampler {
    
    private double exploreRatio;
    private final Random random;
    private int phasesThreshold;

    public RandomAdaptiveSampler() {
        random = new Random();
    }

    
    @Override
    public InputSpacePoint next() {
        super.next();
        if(this.pointsPicked <= this.phasesThreshold) {  // return a border point
            
        }
        if(this.random.nextDouble() < exploreRatio) { // pick a random point
            
        } else {    // pick midpoint of difference
            
        }
        
        return null;
    }
}
