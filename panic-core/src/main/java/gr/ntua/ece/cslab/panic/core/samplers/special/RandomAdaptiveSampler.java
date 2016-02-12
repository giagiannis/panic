/*
 * Copyright 2016 Giannis Giannakopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.metrics.GlobalMetrics;
import gr.ntua.ece.cslab.panic.core.models.MLPerceptron;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.BorderPointsSampler;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class is yet another implementation of the typical Greedy Adaptive
 * Sampling algorithm (as described in the PANIC paper), enhanced with
 * exploration features. At each call, the sampler may return a random point
 * with a specific probability.
 *
 * @author Giannis Giannakopoulos
 */
public class RandomAdaptiveSampler extends AbstractAdaptiveSampler {

    private double exploreRatio=0.1;
    private int firstPhaseThreshold;

    private final Random random;
    private final List<InputSpacePoint> chosenPoints;
    private final List<OutputSpacePoint> valuesReceived;
    private final BorderPointsSampler firstPhaseSampler;
    private final Map<InputSpacePoint, Set<InputSpacePoint>> forbiddenCombinations;

    // CONSTRUCTORS, GETTERS AND SETTERS
    public RandomAdaptiveSampler() {
        this.random = new Random();
        this.chosenPoints = new LinkedList<>();
        this.firstPhaseSampler = new BorderPointsSampler();
        this.valuesReceived = new LinkedList<>();
        this.forbiddenCombinations = new HashMap<>();
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

    public List<InputSpacePoint> getChosenPoints() {
        return chosenPoints;
    }

    
    // SAMPLING METHODS
    @Override
    public InputSpacePoint next() {
        super.next();
        InputSpacePoint sampleReturned;
        if (this.pointsPicked <= this.firstPhaseThreshold && this.firstPhaseSampler.hasMore()) {
//            Logger.getLogger(this.getClass().getName()).info("Returning Border Point");
            sampleReturned = this.firstPhaseSampler.next();
        } else {
            if (this.random.nextDouble() <= this.exploreRatio) {
//                System.err.println("2nd Phase: Exploring");
                sampleReturned = this.getRandomPoint();
            } else {
//                System.err.println("2nd Phase: Getting point");
                sampleReturned = this.getSteepestPoint();
                if(this.chosenPoints.contains(sampleReturned))
                    sampleReturned = this.getRandomPoint();
                // return the next steepest point
            }
        }
        if (sampleReturned != null) {
            this.chosenPoints.add(sampleReturned);
        }
        return sampleReturned;
    }

    protected InputSpacePoint getRandomPoint() {
        InputSpacePoint randomPoint = new InputSpacePoint();
        for (Map.Entry<String, List<Double>> e : this.ranges.entrySet()) {
            int index = this.random.nextInt(e.getValue().size());
            randomPoint.addDimension(e.getKey(), e.getValue().get(index));
        }
        if (this.chosenPoints.contains(randomPoint)) {
            randomPoint = this.getRandomPoint();
        }
        return randomPoint;
    }

    protected InputSpacePoint getSteepestPoint() {
        List<InputSpacePoint> l = this.getSteepestPair();
        return this.getMidpoint(l.get(0), l.get(1));
    }

    // function used to return two points that present the highest difference
    // in performance
    private List<InputSpacePoint> getSteepestPair() {
        List<InputSpacePoint> pair = new LinkedList<>();
        InputSpacePoint a = null, b = null;
        Double maxD = Double.MIN_VALUE;
        for (OutputSpacePoint p1 : this.valuesReceived) {
            Set<InputSpacePoint> forbiddenCombos = this.forbiddenCombinations.get(p1.getInputSpacePoint());
            for (OutputSpacePoint p2 : this.valuesReceived) {
                if (forbiddenCombos == null || !forbiddenCombos.contains(p2.getInputSpacePoint())) {
                    Double d = Math.abs(p1.getValue() - p2.getValue());
                    if (d > maxD) {
                        maxD = d;
                        a = p1.getInputSpacePoint();
                        b = p2.getInputSpacePoint();
                    }
                }
            }
        }
        pair.add(a);
        pair.add(b);
        if (!this.forbiddenCombinations.containsKey(a)) {
            this.forbiddenCombinations.put(a, new HashSet<InputSpacePoint>());
        }
        if (!this.forbiddenCombinations.containsKey(b)) {
            this.forbiddenCombinations.put(b, new HashSet<InputSpacePoint>());
        }
        this.forbiddenCombinations.get(a).add(b);
        this.forbiddenCombinations.get(b).add(a);
        return pair;
    }

    private InputSpacePoint getMidpoint(InputSpacePoint a, InputSpacePoint b) {
        InputSpacePoint point = new InputSpacePoint();
        for (String s : a.getKeysAsCollection()) {
            int index1, index2;
            List<Double> currentRange = this.ranges.get(s);
            for (index1 = 0; index1 < currentRange.size(); index1++) {
                if (currentRange.get(index1).equals(a.getValue(s))) {
                    break;
                }
            }
            for (index2 = 0; index2 < currentRange.size(); index2++) {
                if (currentRange.get(index2).equals(b.getValue(s))) {
                    break;
                }
            }
            point.addDimension(s, currentRange.get((index1 + index2) / 2));
        }
        return point;
    }

    // TUNERS AND OTHER METHODS

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.firstPhaseSampler.setDimensionsWithRanges(ranges);
        this.firstPhaseSampler.configureSampler();
        this.firstPhaseThreshold = (int) Math.floor((Math.pow(2, this.ranges.size())));
    }

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.err.println("Please provide:\n"
                    + "\tinput file\n"
                    + "\texplore ratio\n"
                    + "\tfirst phase thresold\n"
                    + "\tsampling rate");
            System.exit(1);;
        }
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);

        RandomAdaptiveSampler sampler = new RandomAdaptiveSampler();
        sampler.setDimensionsWithRanges(file.getDimensionRanges());
        sampler.setExploreRatio(new Double(args[1]));
        sampler.setFirstPhaseThreshold(new Integer(args[2]));
        sampler.setSamplingRate(new Double(args[3]));
        sampler.configureSampler();
        
        Model m = new MLPerceptron();
        m.configureClassifier();
        
        while (sampler.hasMore()) {
            InputSpacePoint sample = sampler.next();
            System.err.println(sample);
            OutputSpacePoint out = file.getActualValue(sample);
            sampler.addOutputSpacePoint(out);
            m.feed(out);
        }
        GlobalMetrics metrics = new GlobalMetrics(file.getOutputSpacePoints(), (Model) m, sampler.getChosenPoints());
        System.out.format("%.5f %.5f %.5f %.5f\n", metrics.getAverageError(), metrics.getDeviation(), metrics.getMSE(), metrics.getR());
    }
}
