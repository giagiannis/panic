package gr.ntua.ece.cslab.panic.core.partitioners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

public abstract class AbstractPartitioner {
	

    protected HashMap<String, List<Double>> ranges,
            lowerRegion,
            higherRegion;

	public AbstractPartitioner() {
		this.higherRegion = new HashMap<>();
		this.lowerRegion = new HashMap<>();
	}
	
	public HashMap<String, List<Double>> getLowerRegion() {
		return this.lowerRegion;
	}
	
	public HashMap<String, List<Double>> getHigherRegion() {
		return this.higherRegion;
	}
	
	public HashMap<String, List<Double>> getRanges() {
		return ranges;
	}
	
	public void setRanges(HashMap<String, List<Double>> ranges) {
		this.ranges = ranges;
	}
	
	public abstract void configurePartitioner();
	
    // public static methods
    public static boolean pointInRange(Map<String, List<Double>> range, InputSpacePoint point) {
//        System.out.format("Received value (%s, %s)\n", (range == null), (point == null));
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
//            System.out.format("%s, %s\n",(range==null), (p==null));
//            System.out.format("%s, %s\n",(range==null), (p.getInputSpacePoint()==null));
            boolean valu = pointInRange(range, p.getInputSpacePoint());
            if (pointInRange(range, p.getInputSpacePoint())) {
                result.add(p);
            }
        }
        return result;
    }

}