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

package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;

/**
 * This class returns all the acceptable border points combinations, based on an
 * input dataset.
 * 
 * @author Giannis Giannakopoulos
 */
public class BorderPointsSampler extends AbstractSampler {
    
    private Double[] max, min;
    private String[] dimensions;
    private int currentIndex = 0;

    
    // Constructor, Getters and Setters
    public BorderPointsSampler() {
        super();
        this.currentIndex = 0;
    }
    
    
    // Interface methods for class interaction
    
    /**
     * Method used to calculate the number of points to return, etc. Call this 
     * before launching the sampler.
     */
    @Override
     public void configureSampler() {
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
//    /**
//     * Bulk export of border InputSpacePoints.
//     * @return 
//     */
//    public List<InputSpacePoint> getBorderPoints() {
//        List<InputSpacePoint> results  = new LinkedList<>();
//        
//        int oldIndex = this.currentIndex;
//        this.currentIndex = 0;
//        while(this.hasMore())
//            results.add(this.next());
//        this.currentIndex = oldIndex;
//        
//        return results;
//    }
    
    /**
     * Interactive export of InputSpacePoints
     * @return 
     */
    @Override
    public InputSpacePoint next() {
        InputSpacePoint point = new InputSpacePoint();
        for(int i=0;i<this.dimensions.length;i++) {
            boolean flag = this.analyzeIndex(this.currentIndex, i);
            point.addDimension(this.dimensions[i], (flag?max[i]:min[i]));
        }
        this.currentIndex+=1;
        return point;
    }
    
    @Override
    public boolean hasMore() {
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
    
//    public static void main(String[] args) {
//        BorderPointsSampler p = new BorderPointsSampler();
//        CSVFileManager file = new CSVFileManager();
//        file.setFilename(args[0]);
//        p.setRanges(file.getDimensionRanges());
//        p.configureSampler();
//        
//        System.out.println(p.next());
//        System.out.println(p.next());
//        System.out.println("");
//        System.out.println(p.getBorderPoints());        
//        System.out.println("");
//        System.out.println(p.next());
//        System.out.println(p.next());
//    }
}
