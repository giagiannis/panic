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

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.eval.CrossValidation;
import gr.ntua.ece.cslab.panic.core.fresh.structs.DeploymentSpace;
import gr.ntua.ece.cslab.panic.core.fresh.tree.line.SplitLine;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeTestNode;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.*;
import java.util.stream.Collectors;

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
        // get possible values of separation - this returns a superset of the space of the points
        HashMap<String,Set<Double>> possibleValues = this.possibleValues(this.original.getPoints());

        // find the best two dimensions if axis parallel splits are used
        String[] dominantDimensions = this.findTwoBestDimensions(possibleValues);

        // find the best line for the previous two dimensions
        HashMap<String, Set<Double>> domDimensionValues = new HashMap<>();
        domDimensionValues.put(dominantDimensions[0], possibleValues.get(dominantDimensions[0]));
        domDimensionValues.put(dominantDimensions[1], possibleValues.get(dominantDimensions[1]));
        CandidateSolution best = this.getBestSolution(domDimensionValues);
        System.out.println(best);



        // try all the possible values
//        CandidateSolution best = this .findBestCandidatePair(possibleValues);


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
                        best.getSplitLine(),
                        best.getOriginalDS(),
                        new DecisionTreeLeafNode(best.getLeftList(),best.getLeftDS()),
                        new DecisionTreeLeafNode(best.getRightList(), best.getRightDS()));
//                        this.original.getId());
            } else {
//                System.err.println("Separator.separate");
//                System.err.println("Solution WAS found but it produced poor-er results!");
            }
        }
    }

    /**
     * Method that returns a list containing the names of the two most significant dimensions
     * of the data points.
     *
     * @param possibleValues
     * @return
     */
    protected String[] findTwoBestDimensions(HashMap<String,Set<Double>> possibleValues) {
        List<Map.Entry<String, Double>> dimensionScores = new LinkedList<>();

        for (String candidateDimension : possibleValues.keySet()) {
            for (Double candidateValue : possibleValues.get(candidateDimension)) {
                CandidateSolution candidatePair;

                InputSpacePoint p1 = new InputSpacePoint();
                p1.addDimension(candidateDimension, candidateValue);
                p1.addDimension("y", 0.0);

                InputSpacePoint p2 = new InputSpacePoint();
                p2.addDimension(candidateDimension, candidateValue);
                p2.addDimension("y", 1.0);

                SplitLine line = new SplitLine(p1,p2,candidateDimension, "y");

                candidatePair = new CandidateSolution(this.original.getPoints(), this.original.getDeploymentSpace(), line);
                double estimation = estimate(candidatePair);
                if (this.solutionIsAccepted(candidatePair)) {
                    dimensionScores.add(new AbstractMap.SimpleEntry<>(candidateDimension, estimation));
                }
            }
        }
        dimensionScores.sort((e1, e2) -> -e1.getValue().compareTo(e2.getValue()));
        String label1 = "", label2="";
        while (!dimensionScores.isEmpty()) {
            Map.Entry current = dimensionScores.remove(0);
            if(label1.equals("")) {
                label1 = (String) current.getKey();
            } else if(label2.equals("") && !((String)current.getKey()).equals(label1)) {
                label2 = (String) current.getKey();
            } else {
                break;
            }
        }
        String[] result = new String[2];
        result[0] = label1;
        result[1] = label2;
        return result;
    }

    protected CandidateSolution getBestSolution(HashMap<String, Set<Double>> values) {
        List<InputSpacePoint> points = new LinkedList<>();
        for(String s : values.keySet()) {
            if(!points.isEmpty()) {
                List<InputSpacePoint> newPoints = new LinkedList<>();
                for(Double d : values.get(s)) {
                    for(InputSpacePoint old : points) {
                        InputSpacePoint clone = old.getClone();
                        clone.addDimension(s,d);
                        newPoints.add(clone);
                    }
                }
                points = newPoints;
            } else {
                for(Double d : values.get(s)) {
                    InputSpacePoint p = new InputSpacePoint();
                    p.addDimension(s, d);
                    points.add(p);
                }
            }
        }
        String[] dimensionLabels = points.get(0).getKeysAsCollection().toArray(new String[2]);
        Set<String> serializedLines = new HashSet<>();
        double bestScore = Double.NEGATIVE_INFINITY;
        CandidateSolution best = null;
        for(InputSpacePoint p1 : points) {
            for(InputSpacePoint p2 : points) {
                if(!p1.equals(p2)) {
                    SplitLine line = new SplitLine(p1, p2, dimensionLabels[0], dimensionLabels[1]);
                    if(!serializedLines.contains(line.toString())) {
                        serializedLines.add(line.toString());

                        CandidateSolution solution = new CandidateSolution(this.original.getPoints(), this.original.getDeploymentSpace(), line);
                        double estimation = this.estimate(solution);
                        if(bestScore <= estimation && this.solutionIsAccepted(solution)) {
                            best = solution;
                            bestScore = estimation;
                        }
                    }
                }
            }
        }
        System.out.println("Lines checked: "+serializedLines.size());
        return best;
    }

    protected abstract double estimate(CandidateSolution pair);

    protected static class CandidateSolution {
        private final List<OutputSpacePoint> original, leftList, rightList;
        private final DeploymentSpace originalDS;
        private final DeploymentSpace leftDS, rightDS;
        private final SplitLine splitLine;
        private double value;

        public CandidateSolution(List<OutputSpacePoint> original, DeploymentSpace space, SplitLine splitLine) {
            this.original = original;
            this.originalDS = space;
            this.splitLine = splitLine;

            this.leftList = original.parallelStream().filter(a->this.splitLine.lessOrEqual(a.getInputSpacePoint())).collect(Collectors.toList());
            this.rightList= original.parallelStream().filter(a->!this.splitLine.lessOrEqual(a.getInputSpacePoint())).collect(Collectors.toList());

            this.leftDS = new DeploymentSpace(this.originalDS.getPoints().parallelStream().filter(a->this.getSplitLine().lessOrEqual(a)).collect(Collectors.toSet()));
            this.rightDS = new DeploymentSpace(this.originalDS.getPoints().parallelStream().filter(a->!this.getSplitLine().lessOrEqual(a)).collect(Collectors.toSet()));
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

        public DeploymentSpace getOriginalDS() {
            return originalDS;
        }


        public DeploymentSpace getLeftDS() {
            return leftDS;
        }

        public DeploymentSpace getRightDS() {
            return rightDS;
        }

        public SplitLine getSplitLine() {
            return splitLine;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("(%s)", this.splitLine);
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

    protected boolean solutionIsAccepted(CandidateSolution solution) {
        boolean accepted= solution.getLeftList().size()>solution.getOriginalDS().getDimensionality();
        accepted &= solution.getRightList().size()>solution.getOriginalDS().getDimensionality();
        return accepted;
    }
}
