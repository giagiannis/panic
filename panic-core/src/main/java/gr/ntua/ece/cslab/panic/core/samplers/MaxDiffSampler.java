package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.BagClassify;
import gr.ntua.ece.cslab.panic.core.models.Discretization;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This sampler samples the space according to the achieved values.
 *
 * FIXME: fill this javadoc here
 *
 * @author Giannis Giannakopoulos
 */
public class MaxDiffSampler extends AbstractAdaptiveSampler {

    private List<InputSpacePoint> inputSpacePointsPicked;
    private List<OutputSpacePoint> outputSpacePointsPicked;
    private Random rand;

    public MaxDiffSampler() {
        super();
        this.inputSpacePointsPicked = new LinkedList<>();
        this.outputSpacePointsPicked = new LinkedList<>();
        this.rand = new Random();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint result = null;
        if (this.pointsPicked <= Math.pow(2, this.ranges.size())) { //pick random
            result = this.getRandomPoint();
        } else {
            result = this.maxDiff();
        }
        this.inputSpacePointsPicked.add(result);
        return result;
    }

    private InputSpacePoint getRandomPoint() {
        InputSpacePoint p = this.getPointById(this.rand.nextInt(this.maxChoices));
        while (this.inputSpacePointsPicked.contains(p)) {
            p = this.getPointById(this.rand.nextInt(this.maxChoices));
        }
        return p;
    }

    
    /**
     * Find the points with the maximum differences and returns the intermediate point.
     * @return 
     */
    private InputSpacePoint maxDiff() {
        double max = Double.MIN_VALUE;
        OutputSpacePoint final1 = null, final2 = null;
        for(OutputSpacePoint p1: this.model.getOriginalPointValues()){
            for(OutputSpacePoint p2 : this.model.getOriginalPointValues()){
                double currentDistance = Math.abs(p2.getValue()-p1.getValue());
                if(currentDistance > max) {
                    max = currentDistance;
                    final1 = p1;
                    final2 = p2;
                }
            }
        }
        return this.getIntermediatePoint(final1.getInputSpacePoint(), final2.getInputSpacePoint());
    }
    
    /**
     * Returns the point between p1 and p2.
     * @param p1
     * @param p2
     * @return 
     */
    private InputSpacePoint getIntermediatePoint(InputSpacePoint p1, InputSpacePoint p2) {
        InputSpacePoint result = new InputSpacePoint();
        for(String s : p1.getKeysAsCollection()){
            double value = (p1.getValue(s) + p2.getValue(s))/2.0;
            result.addDimension(s, this.roundValue(s, value));
        }
        System.out.println(result);
        
        return result;
    }
    
    /**
     * Takes a value from the dimension with keyword key, and returns the closest applicable 
     * value to this dimension.
     * @param key
     * @param d
     * @return 
     */
    private double roundValue(String key, double d) {
        double distance = Double.MAX_VALUE;
        double cand = 0.0;
        for(double v : this.ranges.get(key)){
            if(v == d)
                return v;
            if(Math.abs(v-d) < distance) {
                distance = v-d;
                cand = v;
            }
        }
        return cand;
    }

    public static void main(String[] args) throws Exception {
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);
        MaxDiffSampler sampler = new MaxDiffSampler();
        sampler.setSamplingRate(0.2);
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.configureSampler();

        Model m = new BagClassify();
        m.configureClassifier();
        sampler.setModel(m);
        m.feed(file.getActualValue(sampler.next()), false);
        m.feed(file.getActualValue(sampler.next()), false);
        m.feed(file.getActualValue(sampler.next()), false);
        m.feed(file.getActualValue(sampler.next()), false);
        m.train();

        while (sampler.hasMore()) {
            InputSpacePoint pointSampled = sampler.next();
            if(pointSampled == null) {
                System.err.println("Breaking!");
                break;
            }
            OutputSpacePoint out = file.getActualValue(pointSampled);
            m.feed(out);
        }
    }
}
