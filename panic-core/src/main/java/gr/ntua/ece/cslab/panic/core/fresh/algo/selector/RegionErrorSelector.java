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

package gr.ntua.ece.cslab.panic.core.fresh.algo.selector;

import gr.ntua.ece.cslab.panic.core.fresh.algo.DTAlgorithm;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;

import java.util.Set;

/**
 * This class implements a selector that takes into consideration both the region and the error.
 * Created by Giannis Giannakopoulos on 2/29/16.
 */
public class RegionErrorSelector extends LeafSelector{

    private double errorCoefficient = 1.0, regionCoefficient = 1.0;
    private DecisionTreeLeafNode leaf = null;

    private double maxError, maxRegionSize;
    private double minError, minRegionSize;
    /**
     * Default constructor
     *
     * @param tree      the tree
     * @param forbidden set containing leaves that are not applicable
     */
    public RegionErrorSelector(DecisionTree tree, Set<String> forbidden) {
        super(tree, forbidden);
        this.maxError = 0.0;
        this.maxRegionSize = 0.0;
        for(DecisionTreeLeafNode l : this.tree.getLeaves()) {
            double currentError = DTAlgorithm.meanSquareError(l);
            double currentRegionSize = this.regionSize(l);
            this.maxError = (this.maxError<currentError?currentError:this.maxError);
            this.maxRegionSize = (this.maxRegionSize<currentRegionSize?currentRegionSize:this.maxRegionSize);
            this.minError = (this.minError > currentError?currentError:this.minError);
            this.minRegionSize= (this.minRegionSize> currentRegionSize?currentRegionSize:this.minRegionSize);
        }
    }

    public double getErrorCoefficient() {
        return errorCoefficient;
    }

    public void setErrorCoefficient(double errorCoefficient) {
        this.errorCoefficient = errorCoefficient;
    }

    public double getRegionCoefficient() {
        return regionCoefficient;
    }

    public void setRegionCoefficient(double regionCoefficient) {
        this.regionCoefficient = regionCoefficient;
    }

    @Override
    public DecisionTreeLeafNode getLeaf() {
        if(this.leaf==null) {
            this.leaf = this.selectLeaf();
        }
        return this.leaf;
    }

    private DecisionTreeLeafNode selectLeaf() {
//        System.out.println("Performing the shit!");
        double maxScore = 0;
        DecisionTreeLeafNode leaf = null;
        for(DecisionTreeLeafNode l : tree.getLeaves()) {
            double currentError = this.normalizeValue(DTAlgorithm.meanSquareError(l), this.minError, this.maxError);
            double currentRegion = this.normalizeValue(this.regionSize(l), this.minRegionSize, this.maxRegionSize);
//            System.out.format("leaf: %s:\t normalized error %.5f, normalized region %.5f, is blacklisted: %s\n",l.getId(), currentError, currentRegion, this.forbiddenTreePaths.contains(l.treePath()));
            double currentScore = this.errorCoefficient * currentError + this.regionCoefficient * currentRegion;
            if((!this.forbiddenTreePaths.contains(l.treePath())) && (currentScore>maxScore || leaf==null)) {
                leaf = l;
                maxScore = currentScore;
            }
        }
//        System.out.println("Chosen leaf: "+leaf.getId());
        return leaf;
    }

    private double regionSize(DecisionTreeLeafNode leaf) {
        double mul=1.0;
        for(String key : leaf.getDeploymentSpace().getRange().keySet()) {
            mul*=leaf.getDeploymentSpace().getRange().get(key).size();
        }
        return mul;
    }

    private double normalizeValue(double value, double minValue, double maxValue) {
        if(maxValue==minValue)
            return 1;
        return (value - minValue) / (maxValue - minValue);
    }
}
