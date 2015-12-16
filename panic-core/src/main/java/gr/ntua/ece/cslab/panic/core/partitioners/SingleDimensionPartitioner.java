package gr.ntua.ece.cslab.panic.core.partitioners;

//import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * RangeBisectionPartitioner this partitioner splits the range into half by
 * bisecting the most important dimension.
 *
 * @author Giannis Giannakopoulos
 */
public class SingleDimensionPartitioner extends AbstractPartitioner{
    private String dimensionKey;

    // constructors, getters and setters
    public SingleDimensionPartitioner() {
    	
    }
    
    public String getDimensionKey() {
        return dimensionKey;
    }

    public void setDimensionKey(String dimensionKey) {
        this.dimensionKey = dimensionKey;
    }


    // public interface
    /**
     * Configures and estimates the new ranges
     */
    public void configurePartitioner() {
        System.out.print("partitioning: (");
        for (String key : this.ranges.keySet()) {
            this.higherRegion.put(key, this.ranges.get(key));
            this.lowerRegion.put(key, this.ranges.get(key));
            System.out.format("%s:%d ",key,this.ranges.get(key).size());
        }
        System.out.println(")");

        List<Double> examinedList = this.ranges.get(dimensionKey);
        if(this.getLeftSublist(examinedList).size()>0)
            this.lowerRegion.put(dimensionKey, this.getLeftSublist(examinedList));
//        else
//            this.lowerRegion = null;
        if(this.getRightSublist(examinedList).size()>0)
            this.higherRegion.put(dimensionKey, this.getRightSublist(examinedList));
//        else
//            this.higherRegion = null;
    }

    // private methods
//    private Double getMedianElement(List<Double> list) {
//        Collections.sort(list);
//        double median = list.get(list.size() / 2);
//        return median;
//    }

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
}
