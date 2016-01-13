package gr.ntua.ece.cslab.panic.core.eval;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.Model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 1/13/16.
 */
public class CrossValidation {

    public static double meanSquareError(Class<? extends Model> classType, List<OutputSpacePoint> trainedPoints) {
        if(trainedPoints.size()<1)
            return Double.NaN;
        double error = 0.0;
        for(int i=0;i<trainedPoints.size();i++) {
            try {
                Model m  = classType.newInstance();
                m.configureClassifier();
                List<OutputSpacePoint> trainingSet = new LinkedList<>(trainedPoints);
                OutputSpacePoint removed=trainingSet.remove(i);
                List<OutputSpacePoint> testSet = new LinkedList<>();
                testSet.add(removed);
                m.feed(trainingSet);
                m.train();
                error+=Metrics.meanSquareError(m, testSet);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return error/trainedPoints.size();
    }
}
