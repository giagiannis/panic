package gr.ntua.ece.cslab.panic.core.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Giannis Giannakopoulos on 12/14/15.
 */
public abstract class AbstractAnalyzer {

    private List<OutputSpacePoint> pointsToAnalyze, normalizedPoints;

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
     * Normalize all the train pointsToAnalyze so that all the input dimensions are packed into [0.0, 1.0]
     */
    public void normalizePoints() {
        this.normalizedPoints = new LinkedList<>();
        Map<String, Map<String, Double>> foo = new TreeMap<>();
        for(String key: this.pointsToAnalyze.get(0).getInputSpacePoint().getKeysAsCollection()) {
            foo.put(key, this.findMinMaxElements(this.pointsToAnalyze, key));
        }

        for(OutputSpacePoint p:this.getPointsToAnalyze()) {
            OutputSpacePoint normalizedPoint = new OutputSpacePoint();
            InputSpacePoint inputSpacePoint = new InputSpacePoint();
            for(String s: p.getInputSpacePoint().getKeysAsCollection()) {
                Double max = foo.get(s).get("max");
                Double min = foo.get(s).get("min");
                Double oldValue = p.getInputSpacePoint().getValue(s);
                inputSpacePoint.addDimension(s, (oldValue-min)/(max-min));
            }
            normalizedPoint.setInputSpacePoint(inputSpacePoint);
            normalizedPoint.setKey(p.getKey());
            normalizedPoint.setValue(p.getValue());
            this.normalizedPoints.add(normalizedPoint);
        }
    }

    public abstract void analyze();


    // aux methods
    private Map<String, Double> findMinMaxElements(List<OutputSpacePoint> points, String key) {
        Map<String,Double> results = new TreeMap<>();
        results.put("min", Double.MAX_VALUE);
        results.put("max", Double.MIN_VALUE);

        for(OutputSpacePoint p:points) {
            InputSpacePoint in = p.getInputSpacePoint();
            if(in.getValue(key)<results.get("min"))
                results.put("min",in.getValue(key));
            if(in.getValue(key)>results.get("max"))
                results.put("max",in.getValue(key));
        }
        return results;
    }

}