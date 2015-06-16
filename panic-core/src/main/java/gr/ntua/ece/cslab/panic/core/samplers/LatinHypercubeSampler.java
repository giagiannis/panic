package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Sampler implementing the Latin Hypercube Sampling methodology.
 * @author Giannis Giannakopoulos
 */
public class LatinHypercubeSampler  extends AbstractSampler {

    private Map<String, List<Double>> hyperCubeValues;
    private Random random;
    @Override
    public void configureSampler() {
        super.configureSampler();
        this.hyperCubeValues=this.createHyperCubeValues();
        this.random = new Random();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sample = new InputSpacePoint();
        for(String s:this.hyperCubeValues.keySet()) {
            List<Double> list = this.hyperCubeValues.get(s);
            sample.addDimension(s, list.remove(random.nextInt(list.size())));
        }
        
        return sample;
    }
    
    
    private Map<String, List<Double>> createHyperCubeValues() {
        int slots = (int) Math.floor(this.samplingRate*this.maxChoices);
        HashMap<String, List<Double>> result = new HashMap<>();
        for(String s : this.ranges.keySet()) {
            result.put(s, this.replicateValues(this.ranges.get(s), slots));
        }
        return result;
    }
    
    private List<Double> replicateValues(List<Double> initial, int target) {
        List<Double> result = new ArrayList<>(target);
        double scalingFactor = (target*1.0)/(initial.size()*1.0);
        for(int i=0;i<target;i++) {
            int index = (int) Math.round(i/scalingFactor);
            index=(index==initial.size()?index-1:index);
            result.add(initial.get(index));
        }
        return result;
    }
}
