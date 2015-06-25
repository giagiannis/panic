package gr.ntua.ece.cslab.panic.core.samplers.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Class that holds the loadings scores
 *
 * @author Giannis Giannakopoulos
 */
public class LoadingsAnalyzer {

    // rows represent input dimensions, columns represent PC
    private double[][] loadings;

    private int dimensions, components;
    private String[] labels;
    private double[] pcWeights;

    public LoadingsAnalyzer() {
    }

    public LoadingsAnalyzer(double[][] loadings) {
        this.setLoadings(loadings);
    }

    /**
     * Setter for the dimension names
     *
     * @param labels
     */
    public void setDimensionLabels(String[] labels) {
        this.labels = labels;
    }

    /**
     * Getter for the dimension labels
     *
     * @return
     */
    public String[] getDimensionLabels() {
        return labels;
    }

    /**
     * Sets the loadings. The rows represent different dimensions, whereas the
     * columns represent different Principal Components Order.
     *
     * @param loadings
     */
    public final void setLoadings(double[][] loadings) {
        this.loadings = loadings;
        this.dimensions = loadings.length;
        this.components = (this.dimensions > 0 ? loadings[0].length : 0);
    }

    /**
     * Returns the number of dimensions
     *
     * @return
     */
    public int getNumberOfDimensions() {
        return this.dimensions;
    }

    /**
     * Returns the number of components
     *
     * @return
     */
    public int getNumberOfComponents() {
        return this.components;
    }

    public double[] getPcWeights() {
        return pcWeights;
    }

    public void setPcWeights(double[] pcWeights) {
        this.pcWeights = pcWeights;
    }

    /**
     * Calculates and returns the distance matrix
     *
     * @return
     */
    public double[][] getDistanceMatric() {
        return this.getDistanceMatrix(null);
    }

    /*
     * Calculates and returns the  weighted distance matrix 
     * @return 
     */
    public double[][] getDistanceMatrix(double[] weights) {
        double[][] distances = new double[this.dimensions][this.dimensions];
        for (int i = 0; i < this.dimensions; i++) {
            for (int j = i; j < this.dimensions; j++) {
                distances[i][j] = this.getDistance(i, j, weights);
                distances[j][i] = distances[i][j];
            }
        }
        return distances;
    }

    /*
     * Calculates and returns the  weighted distance matrix 
     * @return 
     */
    public double[][] getFirstQuarterDistanceMatrix(double[] weights) {
        double[][] distances = new double[this.dimensions][this.dimensions];
        for (int i = 0; i < this.dimensions; i++) {
            for (int j = i; j < this.dimensions; j++) {
                distances[i][j] = this.getFirstQuarterDistance(i, j, weights);
                distances[j][i] = distances[i][j];
            }
        }
        return distances;
    }

    /*
     * Calculates and returns the  weighted similarity matrix 
     * @return 
     */
    public double[][] getSimilarityMatrix(double[] weights) {
        double[][] similarities = new double[this.dimensions][this.dimensions];
        for (int i = 0; i < this.dimensions; i++) {
            for (int j = i; j < this.dimensions; j++) {
                similarities[i][j] = this.getSimilarity(i, j, weights);
                similarities[j][i] = similarities[i][j];
            }
        }
        return similarities;
    }

    /**
     * Distance metric between two dimensions. * The distance metric used is the
     * Euclidian distance
     *
     * @param dimension1
     * @param dimension2
     * @return
     */
    public double getDistance(int dimension1, int dimension2) {
        return this.getDistance(dimension1, dimension2, null);
    }

    /**
     * Distance metric between the performance dimension and another dimension.
     * The distance metric used is the Euclidian distance
     *
     * @param dimension
     * @return
     */
    public double getDistance(int dimension) {
        return this.getDistance(dimension, this.dimensions - 1);
    }

    /**
     * Returns the weighted Euclidean distance. If the specified weights are
     * null, then the un-weighted distance is returned.
     *
     * @param dimension1
     * @param dimension2
     * @param weights
     * @return
     */
    public double getDistance(int dimension1, int dimension2, double[] weights) {
        double sum = 0.0;
        for (int j = 0; j < components; j++) {
            sum += ((weights == null ? 1 : weights[j]) * Math.pow(this.loadings[dimension1][j] - this.loadings[dimension2][j], 2));
//            System.err.println(sum);
        }
        return Math.sqrt(sum);
    }

    public double getDistance(int dimension, double[] weights) {
        return this.getDistance(dimension, this.dimensions - 1, weights);
    }

    /**
     * Returns the weighted Euclidean distance, after each dimensions is mapped
     * to the first quarter of the loading plot.
     *
     * @param dimension1
     * @param dimension2
     * @param weights
     * @return
     */
    public double getFirstQuarterDistance(int dimension1, int dimension2, double[] weights) {
        double sum = 0.0;
        for (int j = 0; j < components; j++) {
            sum += ((weights == null ? 1 : weights[j]) * Math.pow(Math.abs(this.loadings[dimension1][j]) - Math.abs(this.loadings[dimension2][j]), 2));
        }
        return Math.sqrt(sum);
    }

    public double getFirstQuarterDistance(int dimension, double[] weights) {
        return this.getFirstQuarterDistance(dimension, this.dimensions - 1, weights);
    }

    /**
     * Similarity metric between two dimensions. The similarity metric used is
     * the cosine similarity.
     *
     * @param dimension1
     * @param dimension2
     * @return
     */
    public double getSimilarity(int dimension1, int dimension2) {
        return this.getSimilarity(dimension1, dimension2, null);
    }

    /**
     * Similarity metric between the performance dimension and another
     * dimension. The similarity metric used is the cosine similarity.
     *
     * @param dimension
     * @return
     */
    public double getSimilarity(int dimension) {
        return this.getSimilarity(dimension, this.dimensions - 1);
    }

    public double getSimilarity(int dimension1, int dimension2, double[] weights) {
        double sum = 0.0;
        double norm1 = this.getNorm(dimension1);
        double norm2 = this.getNorm(dimension2);
        if (norm1 == 0 || norm2 == 0) {
            throw new IllegalArgumentException("Division with zero!");
        }
        for (int j = 0; j < components; j++) {
            sum += (weights == null ? 1 : weights[j]) * (this.loadings[dimension1][j] * this.loadings[dimension2][j]);
        }
        return sum / (this.getNorm(dimension1) * this.getNorm(dimension2));
    }

    private double getNorm(int dimension) {
        double sum = 0.0;
        for (int j = 0; j < components; j++) {
            sum += (this.loadings[dimension][j] * this.loadings[dimension][j]);
        }
        return Math.sqrt(sum);
    }

    /**
     * Returns the dimensions of the input space ordered according to the their
     * distance from the output dimension.
     *
     * @param weights
     * @return
     */
    public String[] getInputDimensionsOrder(double[] weights) {
        String[] dimensionsKeys = new String[dimensions - 1];

        TreeMap<Double, List<String>> treeMap = new TreeMap<>();
        for (int i = 0; i < dimensionsKeys.length; i++) {
            Double d = this.getDistance(i, weights);
            if (!treeMap.containsKey(d)) {
                treeMap.put(d, new LinkedList<String>());
            }
            treeMap.get(d).add(this.labels[i]);
        }
        int index = 0;
        for (Double d : treeMap.keySet()) {
            for (String s : treeMap.get(d)) {
                dimensionsKeys[index++] = s;
            }
        }
        return dimensionsKeys;
    }

    public String[] getInputDimensionsOrder() {
        String[] dimensionsKeys = new String[dimensions - 1];
        TreeMap<Double, List<String>> treeMap = new TreeMap<>();
        for (int i = 0; i < dimensionsKeys.length; i++) {
            Double d = this.getDistance(i, pcWeights);
            if (!treeMap.containsKey(d)) {
                treeMap.put(d, new LinkedList<String>());
            }
            treeMap.get(d).add(this.labels[i]);
        }
        int index = 0;
        for (Double d : treeMap.keySet()) {
            for (String s : treeMap.get(d)) {
                dimensionsKeys[index++] = s;
            }
        }
        return dimensionsKeys;
    }

    // toString methods
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        int rows = loadings.length;
        int cols = (loadings.length > 0 ? loadings[0].length : 0);
        for (int i = 0; i < rows; i++) {
            strBuilder.append(labels[i]);
            strBuilder.append("\t");
            for (int j = 0; j < cols; j++) {
                strBuilder.append(String.format("%.5f\t", loadings[i][j]));
            }
            if (i != rows - 1) {
                strBuilder.append("\n");
            }
        }
        return strBuilder.toString();
    }

    /**
     * Returns a String containing the distance matrix.
     *
     * @param weights
     * @return
     */
    public String toStringDistanceMatrix(double[] weights) {
        return this.toStringMatrix(this.getFirstQuarterDistanceMatrix(weights));
    }

    /**
     * Returns a String containing the similarity matrix.
     *
     * @param weights
     * @return
     */
    public String toStringSimilarityMatrix(double[] weights) {
        return this.toStringMatrix(this.getSimilarityMatrix(weights));
    }

    private String toStringMatrix(double[][] matrix) {
        StringBuilder strBuilder = new StringBuilder();
        int rows = loadings.length;
        int cols = rows;

        strBuilder.append("\t");
        for (String s : labels) {
            strBuilder.append(s);
            strBuilder.append("\t");
        }
        strBuilder.append("\n");
        for (int i = 0; i < rows; i++) {
            strBuilder.append(labels[i]);
            strBuilder.append("\t");
            for (int j = 0; j < cols; j++) {
                strBuilder.append(String.format("%.2f\t", matrix[i][j]));
            }
            if (i != rows - 1) {
                strBuilder.append("\n");
            }
        }
        return strBuilder.toString();
    }
}
