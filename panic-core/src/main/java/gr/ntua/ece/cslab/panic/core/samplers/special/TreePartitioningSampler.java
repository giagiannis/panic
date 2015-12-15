package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;

/**
 * Created by Giannis Giannakopoulos on 12/15/15.
 */
public class TreePartitioningSampler extends AbstractAdaptiveSampler {

    private RegionTree tree;
    private AbstractSampler sampler;

    public TreePartitioningSampler() {
        super();
        this.tree = new RegionTree();
    }

    @Override
    public void configureSampler() {
        super.configureSampler();
        this.tree.addChild(null, this.ranges);

        // external properties



        // sampler initial configuration
        this.sampler = new LatinHypercubeSampler();
        this.tree.next();
        this.sampler.setDimensionsWithRanges(this.tree.getCurrent().getRegion());
        this.sampler.setSamplingRate(this.samplingRate);
        this.sampler.configureSampler();
    }

    @Override
    public InputSpacePoint next() {
        super.next();
        if(!this.sampler.hasMore()) {// reconfiguration is needed

        }

        return this.sampler.next();
    }
}
