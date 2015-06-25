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

    private Map<String, List<Integer>> hyperCubeIndices;
    private Random random;
    private Integer slots;
    
    @Override
    public void configureSampler() {
        super.configureSampler();
        this.hyperCubeIndices = this.createHyperCubeIndices();
        this.random = new Random();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sample = new InputSpacePoint();
        for(String s:this.hyperCubeIndices.keySet()) {
            List<Integer> indices = this.hyperCubeIndices.get(s);
            int index = indices.remove(this.random.nextInt(indices.size()));
            double value = this.pickPointUniformly(this.translateIndex(s, index));
            sample.addDimension(s, value);
        }
        return sample;
    }
    
    private Map<String, List<Integer>> createHyperCubeIndices() {
        this.slots = (int) Math.floor(this.pointsToPick);
        HashMap<String, List<Integer>> result = new HashMap<>();
        for(String s:this.ranges.keySet()) {
            result.put(s, new ArrayList<Integer>(slots));
            for(int i=0;i<slots;i++)
                result.get(s).add(i);
        }
        return result;
    }
    
    private List<Double> translateIndex(String dimensionKey, Integer index) {
        List<Double> wholeList = this.ranges.get(dimensionKey);
        int startPoint = (int) Math.floor(((double)index/(double)slots)*wholeList.size()),
            endPoint = (int) Math.floor(((double)(index+1.0)/(double)slots)*wholeList.size());
        if(startPoint==endPoint)
            endPoint++;
        return wholeList.subList(startPoint, endPoint);
    }
    
    private Double pickPointUniformly(List<Double> list) {
        return list.get(this.random.nextInt(list.size()));
    }
    
}
