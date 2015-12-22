package gr.ntua.ece.cslab.panic.core;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.*;

/**
 * Created by Giannis Giannakopoulos on 12/15/15.
 */
public class LinearDatasetCreator {
    private Random random;
    private List<OutputSpacePoint> dataPoints;
    private Map<String, Double> dataCoefficients;

    private Integer numberOfDimensions;

    public LinearDatasetCreator() {
        this.random = new Random();
    }

    public List<OutputSpacePoint> getDataPoints() {
        return dataPoints;
    }

    public Map<String, Double> getDataCoefficients() {
        return dataCoefficients;
    }

    public Integer getNumberOfDimensions() {
        return numberOfDimensions;
    }

    public void setNumberOfDimensions(Integer numberOfDimensions) {
        this.numberOfDimensions = numberOfDimensions;
    }

    public void createDataset() {
        // setting data coefficients
        this.dataCoefficients = new TreeMap<>();
        double[] coefficients = new double[numberOfDimensions];
        coefficients[0] = 1.0;
        this.dataCoefficients.put(this.getDimensionsId(0), 1.0);
        for (int j = 1; j < numberOfDimensions; j++) {
            coefficients[j] = 2 * coefficients[j - 1] / 3.0;
            this.dataCoefficients.put(this.getDimensionsId(j), coefficients[j]);
        }

        // calculating ranges
        HashMap<String, List<Double>> ranges = new HashMap<>();
        for (int j = 0; j < numberOfDimensions; j++) {
            Integer cardinality = this.random.nextInt(10) + 5;
            ranges.put(this.getDimensionsId(j), this.createListOfValues(cardinality));
        }

        // generating the actual data points
        List<InputSpacePoint> list1 = new LinkedList<>();
        for (Double d : ranges.get(this.getDimensionsId(0))) {
            InputSpacePoint p = new InputSpacePoint();
            p.addDimension(this.getDimensionsId(0), d);
            list1.add(p);
        }
        for (int j = 1; j < this.getNumberOfDimensions(); j++) {
            List<InputSpacePoint> list2 = new LinkedList<>();
            for (InputSpacePoint i : list1) {
                for (Double d : ranges.get(this.getDimensionsId(j))) {
                    InputSpacePoint clone = i.getClone();
                    clone.addDimension(this.getDimensionsId(j), d);
                    list2.add(clone);
                }
            }
            list1 = list2;
        }
        this.dataPoints = new LinkedList<>();
        for (InputSpacePoint i : list1)
            this.dataPoints.add(this.evaluatePoint(i));
    }

    public HashMap<String, List<Double>> getDimensionsWithRanges() {
        HashMap<String, List<Double>> ranges = new HashMap<>();
        InputSpacePoint in = this.getDataPoints().get(0).getInputSpacePoint();
        for (String s : in.getKeysAsCollection()) {
            ranges.put(s, this.getValueList(s));
        }
        return ranges;
    }

    public OutputSpacePoint evaluatePoint(InputSpacePoint point) {
        OutputSpacePoint out = new OutputSpacePoint();
        double sum = 0.0;
        for (int j = 0; j < numberOfDimensions; j++) {
            String id = this.getDimensionsId(j);
            sum += this.dataCoefficients.get(id) * point.getValue(id);
        }
        out.setInputSpacePoint(point);
        out.setKey("y");
        out.setValue(sum);
        return out;
    }

    private List<Double> getValueList(String dimension) {
        Set<Double> set = new HashSet<>();
        for (OutputSpacePoint p : this.dataPoints) {
            set.add(p.getInputSpacePoint().getValue(dimension));
        }
        return new LinkedList<>(set);
    }

    private List<Double> createListOfValues(Integer count) {
        double ampl = this.random.nextInt(10)+5;
        List<Double> list = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            list.add(this.random.nextGaussian()*ampl);
        }
        return list;
    }

    private String getDimensionsId(int id) {
        return String.format("x%05d", id);
    }
}
