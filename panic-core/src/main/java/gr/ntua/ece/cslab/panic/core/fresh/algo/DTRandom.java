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

package gr.ntua.ece.cslab.panic.core.fresh.algo;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.metricsource.MetricSource;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.AbstractSampler;
import gr.ntua.ece.cslab.panic.core.fresh.samplers.SamplerFactory;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.separators.SeparatorFactory;

/**
 * DTRandom algorithm works as follows:<br/>
 * samples the space in a random way, and then creates a decision tree
 * Created by Giannis Giannakopoulos on 2/19/16.
 */
public class DTRandom extends DTAlgorithm {


    public DTRandom(int deploymentBudget, String samplerType, MetricSource source, String separatorType) {
        super(deploymentBudget, samplerType, source, separatorType);
    }

    @Override
    public void run() {
        SamplerFactory factory = new SamplerFactory();
        AbstractSampler sampler = factory.create(this.samplerType, this.space, this.deploymentBudget);
        sampler.configureSampler();
        while(sampler.hasMore()) {
            InputSpacePoint in = sampler.next();
            this.tree.addPoint(this.source.getPoint(in));
        }

        boolean someoneReplaced = true;
        while(someoneReplaced) {
            ReplacementCouples couples = new ReplacementCouples();
            for(DecisionTreeLeafNode l : this.tree.getLeaves()) {
                SeparatorFactory factory1 = new SeparatorFactory();
                factory1.create(this.separatorType, l);
            }
        }

    }
}
