package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.BorderPointsEstimator;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is yet another implementation of the typical Greedy Adaptive
 * Sampling algorithm (as described in the PANIC paper), enhanced with
 * exploration features. At each call, the sampler may return a random point
 * with a specific probability.
 *
 * @author Giannis Giannakopoulos
 */
public class RandomAdaptiveSampler extends AbstractAdaptiveSampler {

    private double exploreRatio;
    private int firstPhaseThreshold;
    
    private final Random random;
    private final List<InputSpacePoint> chosenPoints;
    private final Map<InputSpacePoint, Double> performance;
    private final BorderPointsEstimator borderPointsEstimator;

    
    // CONSTRUCTORS, GETTERS AND SETTERS
    public RandomAdaptiveSampler() {
        this.random = new Random();
        this.chosenPoints = new LinkedList<>();
        this.performance = new HashMap<>();
        this.borderPointsEstimator = new BorderPointsEstimator();
    }

    public double getExploreRatio() {
        return exploreRatio;
    }

    public void setExploreRatio(double exploreRatio) {
        this.exploreRatio = exploreRatio;
    }

    public int getFirstPhaseThreshold() {
        return firstPhaseThreshold;
    }

    public void setFirstPhaseThreshold(int firstPhaseThreshold) {
        this.firstPhaseThreshold = firstPhaseThreshold;
    }
    
    // SAMPLING METHODS
    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sampleReturned;
        if(this.pointsPicked<=this.firstPhaseThreshold && this.borderPointsEstimator.hasMorePoints()) {
//            Logger.getLogger(this.getClass().getName()).info("Returning Border Point");
            sampleReturned = this.borderPointsEstimator.getBorderPoint();
        } else {
            if(this.random.nextDouble() <= this.exploreRatio) {
                System.err.println("2nd Phase: Exploring");
                sampleReturned = this.getRandomPoint();
            } else {
                System.err.println("2nd Phase: Getting point");
                sampleReturned = this.getSteepestPoint();
                // return the next steepest point
            }
        }
        if(sampleReturned!=null) 
            this.chosenPoints.add(sampleReturned);
        return sampleReturned;
    }
    
    protected InputSpacePoint getRandomPoint() {
        InputSpacePoint randomPoint = new InputSpacePoint();
        for(Map.Entry<String, List<Double>> e : this.ranges.entrySet()) {
            int index = this.random.nextInt(e.getValue().size());
            randomPoint.addDimension(e.getKey(), e.getValue().get(index));
        }
        if(this.chosenPoints.contains(randomPoint))
            randomPoint = this.getRandomPoint();
        return randomPoint;
    }
    
    protected InputSpacePoint getSteepestPoint() {
        return null;
    }
    
    // TUNERS AND OTHER METHODS
    public void addOutputSpacePoint(OutputSpacePoint out) {
        if(out!=null && !this.performance.containsKey(out.getInputSpacePoint())) {
            this.performance.put(out.getInputSpacePoint(), out.getValue());
        } else {
            System.err.println("Performance point already there!!!");
        }
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.borderPointsEstimator.setRanges(ranges);
        this.borderPointsEstimator.estimatePoints();
    }
    
    
    
    public static void main(String[] args) {
        System.out.println("Random Adaptive Sampler");
        
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);
        
        
        RandomAdaptiveSampler sampler = new RandomAdaptiveSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setExploreRatio(0.99);
        sampler.setFirstPhaseThreshold(4);
        sampler.setSamplingRate(0.2);
        sampler.configureSampler();
        
        while(sampler.hasMore()) {
            InputSpacePoint sample =sampler.next();
            System.out.println(sample);
            OutputSpacePoint out = file.getActualValue(sample);
            sampler.addOutputSpacePoint(out);
        }
    }
}
