package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.DatasetCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Giannis Giannakopoulos on 12/15/15.
 */
public class TreePartitioningSamplerTest {
    private List<OutputSpacePoint> dataPoints;
    private HashMap<String, List<Double>> ranges;

    @Before
    public void setUp() throws Exception {
        DatasetCreator datasetCreator = new DatasetCreator();
        datasetCreator.setNumberOfDimensions(3);
        datasetCreator.createDataset();

        dataPoints = datasetCreator.getDataPoints();
        ranges = datasetCreator.getDimensionsWithRanges();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testUnbiasedSampler() {
        TreePartitioningSampler sampler = new TreePartitioningSampler();
        sampler.setDimensionsWithRanges(ranges);
        sampler.setSamplingRate(0.1);
        sampler.configureSampler();
        InputSpacePoint in = sampler.next();
        int count = 0;
        while(sampler.hasMore() && in!=null) {
            in = sampler.next();
            count++;
        }
        assertTrue(count>0);
    }
}