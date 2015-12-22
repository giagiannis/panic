package gr.ntua.ece.cslab.panic.core.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.*;

/**
 * Created by Giannis Giannakopoulos on 12/14/15.
 */
public abstract class AbstractAnalyzer {

    private List<OutputSpacePoint> pointsToAnalyze, normalizedPoints;
    private Map<String, Map<String, Double>> minMaxElements;

    public AbstractAnalyzer() {

    }

    public List<OutputSpacePoint> getPointsToAnalyze() {
        return pointsToAnalyze;
    }

    public void setPointsToAnalyze(List<OutputSpacePoint> pointsToAnalyze) {
        this.pointsToAnalyze = pointsToAnalyze;
    }

    public List<OutputSpacePoint> getNormalizedPoints() {
        return normalizedPoints;
    }

    /**
     * Receives a normalized point and returns the original point.
     *
     * @return
     */
    public OutputSpacePoint getOriginalPoint(OutputSpacePoint normalized) {
        OutputSpacePoint outputSpacePoint = new OutputSpacePoint();
        InputSpacePoint inputSpacePoint = new InputSpacePoint();
        for (String s : normalized.getInputSpacePoint().getKeysAsCollection()) {
            Double max = this.minMaxElements.get(s).get("max");
            Double min = this.minMaxElements.get(s).get("min");
            Double normalizedValue = normalized.getInputSpacePoint().getValue(s);
            inputSpacePoint.addDimension(s, normalizedValue*(max-min) + min);
        }
        outputSpacePoint.setInputSpacePoint(inputSpacePoint);
        outputSpacePoint.setKey(normalized.getKey());
        outputSpacePoint.setValue(normalized.getValue());
        return outputSpacePoint;
    }

    /**
     * Receives an original point and returns the normalized points.
     *
     * @param p
     * @return
     */
    public OutputSpacePoint getNormalizedPoint(OutputSpacePoint p) {
        OutputSpacePoint normalizedPoint = new OutputSpacePoint();
        InputSpacePoint inputSpacePoint = new InputSpacePoint();
        for (String s : p.getInputSpacePoint().getKeysAsCollection()) {
            Double max = this.minMaxElements.get(s).get("max");
            Double min = this.minMaxElements.get(s).get("min");
            Double oldValue = p.getInputSpacePoint().getValue(s);
            inputSpacePoint.addDimension(s, (oldValue - min) / (max - min));
        }
        normalizedPoint.setInputSpacePoint(inputSpacePoint);
        normalizedPoint.setKey(p.getKey());
        normalizedPoint.setValue(p.getValue());
        return normalizedPoint;
    }

    /**
     * Normalize all the train pointsToAnalyze so that all the input dimensions are packed into [0.0, 1.0]
     */
    public void normalizePoints() {
        this.normalizedPoints = new LinkedList<>();
        this.minMaxElements = new TreeMap<>();
        for (String key : this.pointsToAnalyze.get(0).getInputSpacePoint().getKeysAsCollection()) {
            this.minMaxElements.put(key, this.findMinMaxElements(this.pointsToAnalyze, key));
        }
        for (OutputSpacePoint p : this.getPointsToAnalyze()) {
            this.normalizedPoints.add(this.getNormalizedPoint(p));
        }
    }
//
//    /**
//     * This method receives coefficients c0,..,cn and returns C1,..,Cn where:
//     * C1 = |c1|/(|c0|+..+|cn|)
//     * @param coefficients
//     * @return
//     */
//    public Map<String, Double> asPortion(Map<String, Double> coefficients) {
//
//    }

    public abstract void analyze() throws Exception;


    // aux methods
    private Map<String, Double> findMinMaxElements(List<OutputSpacePoint> points, String key) {
        Map<String, Double> results = new TreeMap<>();
        results.put("min", Double.MAX_VALUE);
        results.put("max", Double.MIN_VALUE);

        for (OutputSpacePoint p : points) {
            InputSpacePoint in = p.getInputSpacePoint();
            if (in.getValue(key) < results.get("min"))
                results.put("min", in.getValue(key));
            if (in.getValue(key) > results.get("max"))
                results.put("max", in.getValue(key));
        }
        return results;
    }

}