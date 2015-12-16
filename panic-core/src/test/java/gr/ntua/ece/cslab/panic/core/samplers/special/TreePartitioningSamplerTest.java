package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.DatasetCreator;
import gr.ntua.ece.cslab.panic.core.analyzers.RegressionAnalyzer;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
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

    @Test
    public void testTreeParsing() {
        TreePartitioningSampler.SpecificRegionTreeNode node = new TreePartitioningSampler.SpecificRegionTreeNode();
        node.setRegion(this.ranges);
        node.setAnalyzer(new RegressionAnalyzer());
        RegionTree tree = new RegionTree();
        tree.addChild(node);

        tree.next();
        TreePartitioningSampler.SpecificRegionTreeNode f = (TreePartitioningSampler.SpecificRegionTreeNode) tree.getCurrent();
        assertTrue(f.getAnalyzer()!=null);
    }
}