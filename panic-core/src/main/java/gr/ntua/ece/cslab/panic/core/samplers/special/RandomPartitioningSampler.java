/*
 * Copyright 2016 Giannis Giannakopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package gr.ntua.ece.cslab.panic.core.samplers.special;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.partitioners.AbstractPartitioner;
import gr.ntua.ece.cslab.panic.core.partitioners.RandomPartitioner;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractAdaptiveSampler;
import gr.ntua.ece.cslab.panic.core.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.samplers.LatinHypercubeSampler;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTree;
import gr.ntua.ece.cslab.panic.core.samplers.utils.RegionTreeNode;


/**
 * Sampler used to partition
 * @author Giannis Giannakopoulos
 */
public class RandomPartitioningSampler extends AbstractAdaptiveSampler {

    private AbstractSampler sampler;
    private AbstractPartitioner partitioner;
    private final RegionTree regionTree;
    
    // constructors, getters and setters
    public RandomPartitioningSampler() {
        super();
        this.configurationsParameters.put("tree.length", "Length of the tree to be created");
        this.regionTree = new RegionTree();
    }

    public RegionTree getRegionTree() {
        return regionTree;
    }
    
    @Override
    public void configureSampler() {
        super.configureSampler();
        
        // sampler configuration
        this.sampler = new LatinHypercubeSampler();
        this.sampler.setDimensionsWithRanges(this.ranges);
        this.sampler.setPointsToPick(this.pointsToPick);
        this.sampler.configureSampler();
        
        this.partitionSpace();
    }

    @Override
    public InputSpacePoint next() {
        super.next(); //To change body of generated methods, choose Tools | Templates.
        return this.sampler.next();
    }
    
    private void partitionSpace() {
        int treeLength = new Integer(this.configuration.get("tree.length"));
        
        this.regionTree.addChild(this.ranges);
        this.regionTree.next();
        RegionTreeNode current = this.regionTree.getCurrent();
        while(current.getLevel()<treeLength-1) {
            // partitioner configuration
            this.partitioner = new RandomPartitioner();
            this.partitioner.setRanges(current.getRegion());
            this.partitioner.configurePartitioner();
            
            this.regionTree.addChild(this.partitioner.getHigherRegion());
            this.regionTree.addChild(this.partitioner.getLowerRegion());
            
            this.regionTree.next();
            current = this.regionTree.getCurrent();
        }
    }
    
}
