package gr.ntua.ece.cslab.panic.core.samplers.utils;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class returns all the acceptable border points combinations, based on an
 * input dataset.
 * 
 * @author Giannis Giannakopoulos
 */
public class BorderPointsEstimator {
    
    private Map<String, List<Double>> ranges;
    private Double[] max, min;
    private String[] dimensions;
    private int currentIndex = 0;

    
    // Constructor, Getters and Setters
    public BorderPointsEstimator() {
        this.currentIndex = 0;
    }

    public Map<String, List<Double>> getRanges() {
        return ranges;
    }

    public void setRanges(Map<String, List<Double>> ranges) {
        this.ranges = ranges;
    }
    
    
    // Interface methods for class interaction
    
    /**
     * Method used to calculate the number of points to return, etc. Call this 
     * before launching the sampler.
     */
     public void estimatePoints() {
        // associate each dimension with an ID
        this.dimensions = new String[this.ranges.size()];
        this.max = new Double[this.dimensions.length];
        this.min = new Double[this.dimensions.length];
        
        int i=0;
        for(String d : this.ranges.keySet())
            this.dimensions[i++] = d;
        
        // estimate min and max elements 
        for(i=0;i<this.dimensions.length;i++) {
            double minV = Double.MAX_VALUE, maxV = Double.MIN_VALUE;
            for(Double v : this.ranges.get(this.dimensions[i])) {
                minV = (v<minV?v:minV);
                maxV = (v>maxV?v:maxV);
            }
            max[i] = maxV;
            min[i] = minV;
        }
    }
    /**
     * Bulk export of border InputSpacePoints.
     * @return 
     */
    public List<InputSpacePoint> getBorderPoints() {
        List<InputSpacePoint> results  = new LinkedList<>();
        
        int oldIndex = this.currentIndex;
        this.currentIndex = 0;
        while(this.hasMorePoints())
            results.add(this.getBorderPoint());
        this.currentIndex = oldIndex;
        
        return results;
    }
    
    /**
     * Interactive export of InputSpacePoints
     * @return 
     */
    public InputSpacePoint getBorderPoint() {
        InputSpacePoint point = new InputSpacePoint();
        for(int i=0;i<this.dimensions.length;i++) {
            boolean flag = this.analyzeIndex(this.currentIndex, i);
            point.addDimension(this.dimensions[i], (flag?max[i]:min[i]));
        }
        this.currentIndex+=1;
        return point;
    }
    
    public boolean hasMorePoints() {
        return (this.currentIndex<Math.pow(2, this.dimensions.length));
    }
    // Auxiliary methods
    /**
     * Analyzes the specified index into its binary representation and returns
     * true (1) if the max value should be used for the InputSpacePoint in the
     * specified dimension.
     * @param index is the index to analyze
     * @param position is the respective dimension
     * @return 
     */
    protected boolean analyzeIndex(int index, int position) {
        String binaryRepresentation = Integer.toBinaryString(index);
        Integer zerosNeeded = this.dimensions.length - binaryRepresentation.length();
        String paddedRepr;
        if(zerosNeeded>0){
            paddedRepr = String.format("%0"+zerosNeeded+"d",0) + binaryRepresentation;
        } else if(zerosNeeded == 0) {
            paddedRepr = binaryRepresentation;
        } else {
            return false;
        }
        return paddedRepr.charAt((paddedRepr.length()-1) - (position))=='1';
//        return true;
    }
    
    public static void main(String[] args) {
        BorderPointsEstimator p = new BorderPointsEstimator();
        
        CSVFileManager file = new CSVFileManager();
        file.setFilename(args[0]);
        p.setRanges(file.getDimensionRanges());
        p.estimatePoints();
        
        System.out.println(p.getBorderPoint());
        System.out.println(p.getBorderPoint());
        System.out.println("");
        System.out.println(p.getBorderPoints());        
        System.out.println("");
        System.out.println(p.getBorderPoint());
        System.out.println(p.getBorderPoint());
    }
}
