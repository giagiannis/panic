package gr.ntua.ece.cslab.panic.core.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 12/14/15.
 */

public class RegressionAnalyzerTest {

    private RegressionAnalyzer analyzer;
    private List<OutputSpacePoint> trainPoints;
    private Random random;


    @Before
    public void setUp() throws Exception {
        this.trainPoints = new ArrayList<>();
        this.random = new Random();
        this.analyzer = new RegressionAnalyzer();
        this.createTestDataset(this.random.nextInt(80)+20, this.random.nextInt(9)+1);
        this.analyzer.setPointsToAnalyze(this.trainPoints);
    }

    @After
    public void tearDown() throws Exception {
        // no need to destruct anything
    }

    @Test
    public void testnormalizePoints() throws Exception {
        this.analyzer.normalizePoints();
        List<OutputSpacePoint> normalizedPoints = this.analyzer.getNormalizedPoints();
        assertNotNull(normalizedPoints);
        assertTrue(normalizedPoints.size()==this.analyzer.getPointsToAnalyze().size());
        for(OutputSpacePoint p: normalizedPoints) {
            InputSpacePoint i = p.getInputSpacePoint();
            for(String s:p.getInputSpacePoint().getKeysAsCollection()) {
                assertTrue(i.getValue(s) <= 1.0);
                assertTrue(i.getValue(s) >= 0.0);
            }
        }
    }


    @Test
    public void testAnalyze() throws Exception {
        this.analyzer.normalizePoints();
        this.analyzer.analyze();
    }

    // aux methods
    private void createTestDataset(int numberOfPoints, int numberOfDimensions) {
        double[] coefficients = new double[numberOfDimensions];
        coefficients[0] = 1.0;
        for(int j=1;j<numberOfDimensions;j++) {
            coefficients[j] = 2*coefficients[j-1]/3.0;
        }

        for(int i=0;i<numberOfPoints;i++) {
            InputSpacePoint point = new InputSpacePoint();
            Double sum = 0.0;
            for(int j=0;j<numberOfDimensions;j++) {
                point.addDimension("x"+j,this.random.nextDouble()*(this.random.nextInt(10)+1));
                sum += coefficients[j] * point.getValue("x"+j);
            }
            OutputSpacePoint outPoint = new OutputSpacePoint();
            outPoint.setInputSpacePoint(point);
            outPoint.setKey("y");
            outPoint.setValue(sum);
            this.trainPoints.add(outPoint);
        }

    }
}