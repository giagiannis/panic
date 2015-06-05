package gr.ntua.ece.cslab.panic.core.samplers;

import gr.ntua.ece.cslab.panic.core.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.utils.CSVFileManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This sampler creates a multi-dimensional grid using weights.
 * @author Giannis Giannakopoulos
 */
public class GridSampler extends  AbstractSampler {
    
    /**
     * the raw coefficients as inserted by the user
     */
    private HashMap<String, Double> coefficients;
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
    public HashMap<String, Double> getCoefficients() {
        return coefficients;
    }

    /**
     * Set the weights of choice for each dimension.
     * @param coefficients 
     */
    public void setWeights(HashMap<String, Double> coefficients) {
        this.coefficients = coefficients;
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
        // if the coefficients have not been initialized, initialize them to 1.0
        if(this.coefficients.isEmpty()){
            for(String s:this.ranges.keySet())
                this.coefficients.put(s, 1.0);
        }
        super.configureSampler();
        HashMap<String, Double> cardinalities=this.calculateCardinalitiesPerDimension();
        HashMap<String, List<Double>> values = this.calculateValuesPerDimension(cardinalities);
        List<InputSpacePoint> gridPoints = this.powerset(values);
        this.samples = this.removeForbiddenPoints(gridPoints);
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
        Random random = new Random();
        for(Map.Entry<String, List<Double>> e:this.ranges.entrySet()) 
            rangesClone.put(e.getKey(), new LinkedList<>(e.getValue()));
        
        
        for(String s:cardinalities.keySet()) {
            double pivot=1.0/(cardinalities.get(s)/this.ranges.get(s).size());
            int start = random.nextInt(this.ranges.get(s).size());
            for(double i=0;i<this.ranges.get(s).size();i+=pivot){
                if (!valuesPerDimension.containsKey(s)) {
                    valuesPerDimension.put(s, new LinkedList<Double>());
                }
                int index = ((int) Math.round(i) + start) % this.ranges.get(s).size();
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
        int pointsTotal = (int) Math.round(this.maxChoices*this.samplingRate);
        double product = 1.0;
        for(Map.Entry<String, Double> e : this.coefficients.entrySet()) {
            Double value = e.getValue();
            product *= value;
        }
        for(Map.Entry<String, List<Double>> e: this.ranges.entrySet()) {
            int dimensionCardinality = e.getValue().size();
            product *= dimensionCardinality;
        }
        
        double globalCoefficient = Math.pow(pointsTotal/product, 1.0/this.coefficients.size());
        for(Map.Entry<String, Double> e : this.coefficients.entrySet()) {
            String key = e.getKey();
            Double value = e.getValue();
            Double points = value*globalCoefficient*this.ranges.get(key).size();
            cardinalitiesPerDimension.put(key, points);
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
}

