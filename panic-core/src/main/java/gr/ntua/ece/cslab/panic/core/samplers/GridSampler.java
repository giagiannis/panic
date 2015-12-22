package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This sampler creates a multi-dimensional grid using weights.
 * @author Giannis Giannakopoulos
 */
public class GridSampler extends  AbstractSampler {
    
    /**
     * the raw coefficients as inserted by the user
     */
    private Map<String, Double> coefficients;
    /**
     * List of samples into the grid
     */
    private List<InputSpacePoint> samples;
    
    /**
     * List of points that should not be returned by the sampler.
     */
    private Set<InputSpacePoint> forbiddenPoints;

    // Constructors, Setters and Getters
    
    /**
     * Default constructor
     */
    public GridSampler() {
        this.coefficients = new HashMap<>();
    }

    
    /**
     * Returns the coefficient HashMap
     * @return 
     */
    public Map<String, Double> getCoefficients() {
        return coefficients;
    }

    /**
     * Set the weights of choice for each dimension.
     * @param coefficients 
     */
    public void setWeights(Map<String, Double> coefficients) {
        this.coefficients = coefficients;
        this.normalizeCoefficients();
    }
    
    /**
     * Set the weights of choice for each dimension. The respective IDs of 
     * each array correspond to the same dimensions.
     * @param coefficients
     * @param labels 
     */
    public void setWeights(String[] labels, double[] coefficients) {
        for(int i=0;i<coefficients.length;i++)
            this.coefficients.put(labels[i], coefficients[i]);
        this.normalizeCoefficients();
    }

    public Set<InputSpacePoint> getForbiddenPoints() {
        return forbiddenPoints;
    }

    public void setForbiddenPoints(Set<InputSpacePoint> forbiddenPoints) {
        this.forbiddenPoints = forbiddenPoints;
    }

    // Common sampler interface methods
    @Override
    public void configureSampler() {
        super.configureSampler();
        // if the coefficients have not been initialized, initialize them to 1.0
        if(this.coefficients.isEmpty()){
            for(String s:this.ranges.keySet())
                this.coefficients.put(s, 1.0);
        }
        HashMap<String, Double> cardinalities=this.calculateCardinalitiesPerDimension();
        HashMap<String, List<Double>> values = this.calculateValuesPerDimension(cardinalities);
//        System.out.println(values);
        List<InputSpacePoint> gridPoints = this.powerset(values);
//	System.out.println(gridPoints);
        gridPoints = this.removeForbiddenPoints(gridPoints);
        this.samples = this.removePoints(gridPoints);
    }
    
    @Override
    public InputSpacePoint next() {
        super.next();
        if(!this.samples.isEmpty())
            return this.samples.remove(0);
        return null;
    }
    
    @Override
    public boolean hasMore() {
        return !this.samples.isEmpty();
    }
    
    // private methods used internally by the sampler
    /**
     * calculates the discrete values for each dimension based on the cardinalities
     */
    private HashMap<String, List<Double>>  calculateValuesPerDimension(HashMap<String, Double> cardinalities) {
        HashMap<String, List<Double>> valuesPerDimension = new HashMap<>(), rangesClone = new HashMap<>();
        for(Map.Entry<String, List<Double>> e:this.ranges.entrySet()) 
            rangesClone.put(e.getKey(), new LinkedList<>(e.getValue()));
        
        
        for(String s:cardinalities.keySet()) {
            System.out.format("key: %s, cardinality: %s, ranges: %s\n", s, cardinalities, this.ranges.get(s));

            double pivot=1.0/((1.0*cardinalities.get(s))/this.ranges.get(s).size());
            double start = pivot/2.0;
            for(double i=0;i<this.ranges.get(s).size();i+=pivot){
                if (!valuesPerDimension.containsKey(s)) {
                    valuesPerDimension.put(s, new LinkedList<Double>());
                }
                int index = ((int) Math.round(i+start)) % this.ranges.get(s).size();
                valuesPerDimension.get(s).add(this.ranges.get(s).get(index));
            }
        }
        
        return valuesPerDimension;
    }
    
    /**
     * calculates the number of points returned for each dimension
     */
    private HashMap<String, Double> calculateCardinalitiesPerDimension() {
        HashMap<String, Double> cardinalitiesPerDimension = new HashMap<>();
//        double product = 1.0;
//        for(Map.Entry<String, Double> e : this.coefficients.entrySet()) {
//            Double value = e.getValue();
//            product *= value;
//        }
//        
//        System.out.format("Points to pick: %d, points total: %.0f\n", pointsToPick, maxChoices*product);
//        for(Map.Entry<String, List<Double>> e: this.ranges.entrySet()) {
//            int dimensionCardinality = e.getValue().size();
//            product *= dimensionCardinality;
//        }
//        System.out.format("Points to pick: %d, points total: %.0f\n", pointsTotal, product);
//        double globalCoefficient = Math.pow(pointsTotal/product, 1.0/this.coefficients.size());
//        double globalCoefficient = Math.pow((double)this.pointsToPick/(double)this.maxChoices, 1.0/this.coefficients.size());
//        for(Map.Entry<String, Double> e : this.coefficients.entrySet()) {
//            String key = e.getKey();
//            Double value = e.getValue();
//            if(this.ranges.get(key)==null){
//            	System.exit(1);
//            }
//            Double points = value*globalCoefficient;//*this.ranges.get(key).size();
//            System.out.println(e.getKey()+": "+points+ " points to pick");
//            cardinalitiesPerDimension.put(key, points);
//        }
        
        double valuePerDimension = Math.pow(this.pointsToPick, 1.0/this.ranges.size());
        for(String k : this.coefficients.keySet()) {
        	cardinalitiesPerDimension.put(k, this.coefficients.get(k)*valuePerDimension);
        }
        return cardinalitiesPerDimension;
    }
    
    
    /** 
     * returns all the acceptable input space points, constructed by the discrete 
     * values for each dimension
     */
    private List<InputSpacePoint> powerset(HashMap<String, List<Double>> values) {
        if(values.size()<1) {
            // Why on earth would the code end up here?
            System.err.println("Error!");
            System.exit(1);
            return null;
        } else if(values.size()>1) {
            Iterator<String> it=values.keySet().iterator();
            String key=it.next();
            List<Double> val = values.remove(key);
            List<InputSpacePoint> rec = this.powerset(values);
            List<InputSpacePoint> result = new LinkedList<>();
            for(InputSpacePoint isp:rec) {
                for(Double d:val) {
                    InputSpacePoint p=isp.getClone();
                    p.addDimension(key, d);
                    result.add(p);
                }
            }
            return result;
        } else {
            Iterator<String> it=values.keySet().iterator();
            String key=it.next();
            List<InputSpacePoint> list = new LinkedList<>();
            for(Double v : values.get(key)) {
                InputSpacePoint isp = new InputSpacePoint();
                isp.addDimension(key, v);
                list.add(isp);
            }
            return list;
        }
    }
    
    /**
     * Method getting as input a list of grid points and removes the points that
     * belong to the forbiddenPoints. It returns cleared list of points.
     * @param gridPoints
     * @return 
     */
    private List<InputSpacePoint> removeForbiddenPoints(List<InputSpacePoint> gridPoints) {
        if(forbiddenPoints==null || forbiddenPoints.isEmpty())
            return gridPoints;
        List<InputSpacePoint> result = new LinkedList<>();
        for(InputSpacePoint p : gridPoints) {
            if(!this.forbiddenPoints.contains(p))
                result.add(p);
        }
        return result;
    }
    
    
    /**
     * This function removes points from the samples in order to reach the 
     * desired number of points, set by the sampling rate.
     * @param gridPoints
     * @return 
     */
    private List<InputSpacePoint> removePoints(List<InputSpacePoint> gridPoints) {
        Collections.sort(gridPoints, new InputSpacePointsComparator(this.coefficients));
        int maxPointsPicked = (int) Math.floor(this.samplingRate*this.maxChoices);
        int formerSize = gridPoints.size();
        double rate = formerSize/(1.0*maxPointsPicked);
        List<InputSpacePoint> result = new LinkedList<>();
        for(double i=rate/2.0;i<formerSize;i+=rate) {
            int index = (int) Math.floor(i);
            result.add(gridPoints.get(index));
        }
        return result;
    }
    
    
    private class InputSpacePointsComparator implements Comparator<InputSpacePoint> {
        
        private final String[] orderedLabels;
        public InputSpacePointsComparator(Map<String, Double> coefficients) {
            this.orderedLabels = new String[coefficients.size()];
            TreeMap<Double, List<String>> tree = new TreeMap<>();
            for(Double val : coefficients.values()) 
                tree.put(val, new LinkedList<String>());
            for(String  k :coefficients.keySet())
                tree.get(coefficients.get(k)).add(k);
            int index=0;
            for(Double e:tree.descendingKeySet()) {
                List<String> dimensions = tree.get(e);
                for(String s:dimensions)
                    this.orderedLabels[index++] = s;
            }

        }

        @Override
        public int compare(InputSpacePoint o1, InputSpacePoint o2) {
            for(String s : this.orderedLabels) {
                if(o1.getValue(s)>o2.getValue(s)) {
                    return 1;
                } else if(o1.getValue(s)<o2.getValue(s)) {
                    return -1;
                }
            }
            return 0;
        }
        
    }
    
    private void normalizeCoefficients() {
//    	System.out.print("Initial coefficients were: "+this.coefficients);
    	double product = 1.0;
    	for(Double v:this.coefficients.values()) {
    			product*=v;
    	}
    	product=Math.pow(product, 1.0/this.coefficients.size());
    	for(Map.Entry<String, Double> kv : this.coefficients.entrySet()) {
    			this.coefficients.put(kv.getKey(), kv.getValue()/product);
    	}
//    	System.out.println("and became: "+this.coefficients);
    }
}
