package gr.ntua.ece.cslab.panic.core.samplers.partitioners;

import gr.ntua.ece.cslab.panic.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.OutputSpacePoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * RangeBisectionPartitioner this partitioner splits the range into half by
 * bisecting the most important dimension.
 *
 * @author Giannis Giannakopoulos
 */
public class RangeBisectionPartitioner {

    private HashMap<String, List<Double>> ranges,
            lowerRegionRanges,
            higherRegionRanges;
    private String dimensionKey;

    // constructors, getters and setters
    public RangeBisectionPartitioner() {
    }

    public RangeBisectionPartitioner(HashMap<String, List<Double>> ranges) {
        this.ranges = ranges;
    }

    public Map<String, List<Double>> getRanges() {
        return ranges;
    }

    public void setRanges(HashMap<String, List<Double>> ranges) {
        this.ranges = ranges;
    }

    public String getDimensionKey() {
        return dimensionKey;
    }

    public void setDimensionKey(String dimensionKey) {
        this.dimensionKey = dimensionKey;
    }

    public HashMap<String, List<Double>> getLowerRegionRanges() {
        return lowerRegionRanges;
    }

    public HashMap<String, List<Double>> getHigherRegionRanges() {
        return higherRegionRanges;
    }

    // public interface
    /**
     * Configures and estimates the new ranges
     */
    public void configure() {
        this.higherRegionRanges = new HashMap<>();
        this.lowerRegionRanges = new HashMap<>();

        for (String key : this.ranges.keySet()) {
            this.higherRegionRanges.put(key, this.ranges.get(key));
            this.lowerRegionRanges.put(key, this.ranges.get(key));
        }
        List<Double> examinedList = this.ranges.get(dimensionKey);
        if(this.getLeftSublist(examinedList).size()>0)
            this.lowerRegionRanges.put(dimensionKey, this.getLeftSublist(examinedList));
        else 
            this.lowerRegionRanges = null;
        if(this.getRightSublist(examinedList).size()>0)
            this.higherRegionRanges.put(dimensionKey, this.getRightSublist(examinedList));
        else
            this.higherRegionRanges = null;
    }

    // private methods
    private Double getMedianElement(List<Double> list) {
        Collections.sort(list);
        double median = list.get(list.size() / 2);
        return median;
    }

    private List<Double> getLeftSublist(List<Double> list) {
        int mean = list.size()/2;
        List<Double> result = new LinkedList<>();
        for(int i=0;i<mean;i++)
            result.add(list.get(i));
        return result;
    }

    private List<Double> getRightSublist(List<Double> list) {
        int mean = list.size()/2;
        List<Double> result = new LinkedList<>();
        for(int i=mean;i<list.size();i++)
            result.add(list.get(i));
        return result;
    }

    // public static methods
    public static boolean pointIsInRange(Map<String, List<Double>> range, InputSpacePoint point) {
        if(range==null)
            return false;
        for (String s : point.getKeysAsCollection()) {
            double value = point.getValue(s);
            int dimensionsCardinality = range.get(s).size();
            double min = range.get(s).get(0);
            double max = range.get(s).get(dimensionsCardinality - 1);
            if (value > max || value < min) {
                return false;
            }
        }
        return true;
    }

    public static List<OutputSpacePoint> filterPoints(List<OutputSpacePoint> points, Map<String, List<Double>> range) {
//        System.out.format("Points: %s\n", points.toString());
        List<OutputSpacePoint> result = new LinkedList<>();
        for (OutputSpacePoint p : points) {
//            System.err.println(range);
            if(range==null ){
                System.err.println("Null range???");
            }
            if(points == null) {
                System.err.println("Null points???");
            }
            if (pointIsInRange(range, p.getInputSpacePoint())) {
                result.add(p);
            }
        }
        return result;
    }
}
