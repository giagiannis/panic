package gr.ntua.ece.cslab.panic.core.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    public void analyze() throws Exception {
        if(this.getNormalizedPoints()==null || this.getNormalizedPoints().isEmpty()) {
            this.coefficients = this.trainLinearRegressionClassifier(this.getPointsToAnalyze());
        } else {
            this.coefficients = this.trainLinearRegressionClassifier(this.getNormalizedPoints());
        }
    }

    // trains a linear classifier and returns the params of its model
    private Map<String, Double> trainLinearRegressionClassifier(List<OutputSpacePoint> points) throws Exception{
        Map<String, Double> coefficients = new TreeMap<>();
        LinearRegression regression = new LinearRegression();
        regression.configureClassifier();
        regression.feed(points);
        regression.train();
        String output = regression.getClassifier().toString();
        output = output.replace('\n', ' ');
        String array[] = output.split("=")[1].split("\\+");
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
