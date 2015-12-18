package gr.ntua.ece.cslab.panic.core.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.*;

/**
 * Created by Giannis Giannakopoulos on 12/14/15.
 */
public class RegressionAnalyzer extends AbstractAnalyzer {

    private Map<String, Double> coefficients;

    /**
     * Default constructor
     */
    public RegressionAnalyzer() {
        super();
    }

    public Map<String, Double> getCoefficients() {
        return coefficients;
    }

    /**
     * Trains a Linear Regression model and evaluates its coefficients. When this method is terminated, the coefficients
     * can be obtained. The model is trained with the normalized values, so the normalization method must run first
     */
    @Override
    public void analyze() {
        try {
            if (this.getNormalizedPoints() == null || this.getNormalizedPoints().isEmpty()) {

                this.coefficients = this.trainLinearRegressionClassifier(this.getPointsToAnalyze());

            } else {
                this.coefficients = this.trainLinearRegressionClassifier(this.getNormalizedPoints());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return an array of the dimensions ordered in descending order of significance.
     * @return
     */
    public String[] getDimensionOrdering() {
        return this.getDimensionOrdering(true);
    }

    /**
     * Return an array of the dimensions, ordered by their significance with configurable order (asc vs desc).
     * @return
     */
    public String[] getDimensionOrdering(boolean descendingOrder) {
        String[] keys = new String[this.coefficients.size()-1];
        LinkedList<Map.Entry<String, Double>> entries = new LinkedList<>(this.coefficients.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if(o1.getValue()>o2.getValue())
                    return 1;
                else if (o1.getValue()<o2.getValue())
                    return -1;
                else
                    return 0;
            }
        });
        Iterator<Map.Entry<String, Double>> it = (descendingOrder?entries.descendingIterator():entries.iterator());
        Integer index = 0;
        while(it.hasNext()) {
            Map.Entry<String, Double> en = it.next();
            if(!en.getKey().equals("c")) {
                keys[index++] = en.getKey();
            }
        }
        return keys;
    }

    // trains a linear classifier and returns the params of its model
    private Map<String, Double> trainLinearRegressionClassifier(List<OutputSpacePoint> points) throws Exception {
        this.coefficients = new TreeMap<>();
        LinearRegression regression = new LinearRegression();
        regression.configureClassifier();
        regression.feed(points);
        regression.train();
        String output = regression.getClassifier().toString();
        System.out.println(output);
        output = output.replace('\n', ' ');
        String array[] = output.split("=")[1].split("\\+");
        /*
        FIXME
        When the model is not trained appropriately, some input variables are hidden...
        Pad with zeroes
         */
        for (String c : array) {
            String temp[] = c.split("\\*");
            if (temp.length > 1)
                coefficients.put(temp[1].trim(), new Double(temp[0]));
            else
                coefficients.put("c", new Double(temp[0]));
        }
        return coefficients;
    }
}
