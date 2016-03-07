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
import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeTestNode;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.*;

/**
 * Abstract class dictating the API of the Separator classes and holding the implementation
 * of the common functionality.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public abstract class Separator {
    protected DecisionTreeLeafNode original;
    protected DecisionTreeTestNode result;

    /**
     * Default constructor
     *
     * @param original the original point to separate
     */
    public Separator(DecisionTreeLeafNode original) {
        this.original = original;
    }

    /**
     * Returns the result of the separation
     *
     * @return the test node that came as a result from separation
     */
    public DecisionTreeTestNode getResult() {
        return result;
    }


    /**
     *
     */
    public void separate() {
        // get possible values of separation
        HashMap<String,Set<Double>> possibleValues = this.possibleValues(this.original.getPoints());

        // try all the possible values
        CandidateSolution best = this.findBestCandidatePair(possibleValues);

        // FIXME: sanity check of the solution is crucial here
//         setting result
        if (best != null) {
            int leftListSize = best.getLeftList().size(), rightListSize = rightListSize = best.getRightList().size();
            double mse1 = leftListSize * CrossValidation.meanSquareError(LinearRegression.class, best.getLeftList());
            double mse2 = rightListSize * CrossValidation.meanSquareError(LinearRegression.class, best.getRightList());
            double avgMSE = (mse1+mse2)/(leftListSize+rightListSize);
            double mseSum = CrossValidation.meanSquareError(LinearRegression.class, best.getOriginal());
            if(leftListSize+rightListSize != best.getOriginal().size()) {
                System.err.println("Separator.separate: xontri malakia edw mesa!");
                System.exit(1);
            }
            if(mseSum >= avgMSE) {
                this.result = new DecisionTreeTestNode(
                        best.getSeparationDimension(),
                        best.getSeparationValue(),
                        best.getOriginalDS(),
                        new DecisionTreeLeafNode(best.getLeftList(),best.getLeftDS()),
                        new DecisionTreeLeafNode(best.getRightList(), best.getRightDS()),
                        this.original.getId());
            } else {
//                System.err.println("Separator.separate");
//                System.err.println("Solution WAS found but it produced poor-er results!");
            }
        }
    }

    protected abstract double estimate(CandidateSolution pair);

    protected static class CandidateSolution {
        private final List<OutputSpacePoint> original, leftList, rightList;
        private final DeploymentSpace originalDS;
        private final DeploymentSpace leftDS, rightDS;
        private final String separationDimension;
        private final double separationValue;
        private double value;
        public CandidateSolution(List<OutputSpacePoint> original, String separationDimension, double separationValue, DeploymentSpace space) {
            this.original = original;
            this.leftList = new LinkedList<>();
            this.rightList = new LinkedList<>();
            this.separationDimension = separationDimension;
            this.separationValue = separationValue;
            this.originalDS = space;

            for (OutputSpacePoint o : original) {
                if (o.getInputSpacePoint().getValue(separationDimension) <= separationValue) {
                    this.leftList.add(o);
                } else {
                    this.rightList.add(o);
                }
            }

            // separate deployment spaces as well
            LinkedList<Double> leftL = new LinkedList<>(), rightL = new LinkedList<>();
            for(Double d:this.originalDS.getRange().get(this.separationDimension)) {
                if(d <= separationValue)
                    leftL.add(d);
                else
                    rightL.add(d);
            }
            this.leftDS = this.originalDS.clone();
            this.leftDS.getRange().put(this.separationDimension, leftL);
            this.rightDS = this.originalDS.clone();
            this.rightDS.getRange().put(this.separationDimension, rightL);

        }

        public List<OutputSpacePoint> getLeftList() {
            return leftList;
        }

        public List<OutputSpacePoint> getRightList() {
            return rightList;
        }

        public List<OutputSpacePoint> getOriginal() {
            return original;
        }

        public String getSeparationDimension() {
            return separationDimension;
        }

        public double getSeparationValue() {
            return separationValue;
        }

        public DeploymentSpace getOriginalDS() {
            return originalDS;
        }

        public DeploymentSpace getLeftDS() {
            return leftDS;
        }

        public DeploymentSpace getRightDS() {
            return rightDS;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("(%s, %.5f)", this.separationDimension, this.separationValue);
        }
    }

    protected HashMap<String, Set<Double>> possibleValues(List<OutputSpacePoint> points) {
        HashMap<String, Set<Double>> possibleValues = new HashMap<>();
        for (OutputSpacePoint o : points) {
            for (String dimension : o.getInputSpacePoint().getKeysAsCollection()) {
                if (!possibleValues.containsKey(dimension)) {
                    possibleValues.put(dimension, new HashSet<>());
                }
                possibleValues.get(dimension).add(o.getInputSpacePoint().getValue(dimension));
            }
        }
        return possibleValues;
    }

    /**
     * The pair that maximizes the estimate function is chosen.
     * @param possibleValues
     * @return
     */
    protected CandidateSolution findBestCandidatePair(HashMap<String,Set<Double>> possibleValues) {
        double maxEstimation = Double.NEGATIVE_INFINITY;
        CandidateSolution best = null;
        for (String candidateDimension : possibleValues.keySet()) {
            for (Double candidateValue : possibleValues.get(candidateDimension)) {
                CandidateSolution candidatePair;
                candidatePair = new CandidateSolution(this.original.getPoints(), candidateDimension, candidateValue, this.original.getDeploymentSpace());
                double estimation = estimate(candidatePair);
                candidatePair.setValue(estimation);
                if (estimation > maxEstimation && this.solutionIsAccepted(candidatePair)) {
                    maxEstimation = estimation;
                    best = candidatePair;
                }
            }
        }
        return best;
    }

    protected boolean solutionIsAccepted(CandidateSolution solution) {
//        System.out.format("\t%s: %.5f, %d, %d\n",this.getClass().toString(), solution.getValue(), solution.getRightList().size(), solution.getLeftList().size());
        boolean accepted= solution.getLeftList().size()>solution.getOriginalDS().getRange().size();
        accepted &= solution.getRightList().size()>solution.getOriginalDS().getRange().size();
        return accepted;
    }
}
