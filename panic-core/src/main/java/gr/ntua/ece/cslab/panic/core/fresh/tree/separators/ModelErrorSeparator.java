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

import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

/**
 * Separator that looks to partition the space in such a way that
 * the error of the two trained models is minimized.
 * Created by Giannis Giannakopoulos on 2/24/16.
 */
public class ModelErrorSeparator extends Separator{
    /**
     * Default constructor
     *
     * @param original the original node to separate
     */
    public ModelErrorSeparator(DecisionTreeLeafNode original) {
        super(original);
    }

    @Override
    protected double estimate(CandidateSolution pair) {
        int pointsThreshold = this.original.getDeploymentSpace().getDimensionality();
        if(pair.getLeftList().size() <= pointsThreshold ||pair.getRightList().size()<= pointsThreshold)
            return Double.NEGATIVE_INFINITY;

        double mse = 0.0;
        mse += CrossValidation.meanSquareError(LinearRegression.class, pair.getLeftList());
        mse += CrossValidation.meanSquareError(LinearRegression.class, pair.getRightList());
        return -mse;
    }
}
