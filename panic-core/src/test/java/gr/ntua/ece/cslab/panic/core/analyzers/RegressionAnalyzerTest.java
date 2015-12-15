package gr.ntua.ece.cslab.panic.core.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 12/14/15.
 */

public class RegressionAnalyzerTest {

    private RegressionAnalyzer analyzer;
    private List<OutputSpacePoint> trainPoints;
    private Random random;
    private Map<String, Double> dataCoefficients;


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

    // test that the points are normalized correctly
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

    // test that the analysis is correct
    @Test
    public void testAnalyzeNormalizedPoints() throws Exception {
        this.analyzer.normalizePoints();
        this.analyzer.analyze();
        Map<String, Double> map = this.analyzer.getCoefficients();
        assertTrue(map!=null);
        assertTrue(map.size() == (this.trainPoints.get(0).getInputSpacePoint().numberDimensions()+1));
    }

    // the analyzed coefficients must be identical to the original ones
    @Test
    public void testAnalyzeOriginalPoints() throws Exception {
        this.analyzer.analyze();
        Map<String, Double> map = this.analyzer.getCoefficients();
        for(String key:this.dataCoefficients.keySet()) {
            Double diff = Math.abs(this.dataCoefficients.get(key) -map.get(key));
            assertTrue(diff<0.001);
        }
    }

    @Test
    public void testPointConversion() throws Exception {
        this.analyzer.normalizePoints();

        // normalized -> original -> normalized
        OutputSpacePoint p1 = this.analyzer.getNormalizedPoints().get(0);
        OutputSpacePoint o = this.analyzer.getOriginalPoint(p1);
        OutputSpacePoint p2 = this.analyzer.getNormalizedPoint(o);
        assertTrue(p1.getInputSpacePoint().equals(p2.getInputSpacePoint()));
        assertTrue(p1.getValue() == p2.getValue());

        // original -> normalized -> original
        OutputSpacePoint o1 = this.analyzer.getPointsToAnalyze().get(0);
        OutputSpacePoint p = this.analyzer.getOriginalPoint(o1);
        OutputSpacePoint o2 = this.analyzer.getNormalizedPoint(p);
        assertTrue(o1.getInputSpacePoint().equals(o2.getInputSpacePoint()));
        assertTrue(o1.getValue() == o2.getValue());
    }

    // aux methods
    private void createTestDataset(int numberOfPoints, int numberOfDimensions) {
        this.dataCoefficients = new TreeMap<>();
        double[] coefficients = new double[numberOfDimensions];
        coefficients[0] = 1.0;
        String id=String.format("x%05d",0);
        this.dataCoefficients.put(id, 1.0);
        for(int j=1;j<numberOfDimensions;j++) {
            id=String.format("x%05d",j);
            coefficients[j] = 2*coefficients[j-1]/3.0;
            this.dataCoefficients.put(id, coefficients[j]);
        }

        for(int i=0;i<numberOfPoints;i++) {
            InputSpacePoint point = new InputSpacePoint();
            Double sum = 0.0;
            for(int j=0;j<numberOfDimensions;j++) {
                id=String.format("x%05d",j);
                point.addDimension(id ,this.random.nextGaussian()*(this.random.nextInt(10)+1));
                sum += this.dataCoefficients.get(id) * point.getValue(id);
            }
            OutputSpacePoint outPoint = new OutputSpacePoint();
            outPoint.setInputSpacePoint(point);
            outPoint.setKey("y");
            outPoint.setValue(sum);
            this.trainPoints.add(outPoint);
        }

    }
}