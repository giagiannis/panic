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

package gr.ntua.ece.cslab.panic.core.fresh.tree.separators;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.util.*;

/**
 * Separators that targets to separate the original leaf node wrt minimizing the variance of the output variable.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class VarianceSeparator extends Separator {

    public VarianceSeparator(DecisionTreeLeafNode original) {
        super(original);
    }

    protected double estimate(CandidatePair solution) {
        double eval = 0.0;
        eval += solution.getOriginal().size() * this.variance(solution.getOriginal());
        eval -= solution.getLeftList().size() * this.variance(solution.getLeftList());
        eval -= solution.getRightList().size() * this.variance(solution.getRightList());
        return eval;
    }


    private double variance(List<OutputSpacePoint> list) {
        double variance = 0.0;
        double mean = this.mean(list);
        for(OutputSpacePoint p :list) {
            double diff = p.getValue() - mean;
            variance = diff*diff;
        }
        if(list.size()>0) {
            variance/=list.size();
        }
        return variance;
    }

    private double mean(List<OutputSpacePoint> list) {
        double mean = 0.0;
        for(OutputSpacePoint p: list) {
            mean += p.getValue();
        }
        if(list.size()>0) {
            mean/=list.size();
        }
        return  mean;
    }
}
