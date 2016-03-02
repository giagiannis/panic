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

package gr.ntua.ece.cslab.panic.core.fresh.evaluation;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.fresh.tree.DecisionTree;
import gr.ntua.ece.cslab.panic.core.fresh.tree.nodes.DecisionTreeLeafNode;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;
import gr.ntua.ece.cslab.panic.core.models.Model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Giannis Giannakopoulos on 2/29/16.
 */
public class Metrics {
    public static double getMSE(DecisionTree tree, List<OutputSpacePoint> testPoints) {
        HashMap<String, Model> models = createModels(tree);
        double sum = 0.0;
        for(OutputSpacePoint p : testPoints) {
            double predicted = 0;
            try {
                predicted = models.get(tree.getLeaf(p).getId()).getPoint(p.getInputSpacePoint()).getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            double actual = p.getValue();
            sum +=(predicted-actual)*(predicted-actual);
        }
        return sum/testPoints.size();
    }

    public static double getRSquared(DecisionTree tree, List<OutputSpacePoint> testPoints) {
        HashMap<String, Model> models = createModels(tree);
        double mean  =testPoints.stream().mapToDouble(OutputSpacePoint::getValue).average().getAsDouble();
        double ssTotal = testPoints.stream().mapToDouble(OutputSpacePoint::getValue).map(a->(a-mean)*(a-mean)).sum();
//        List<OutputSpacePoint> predicted = predictions(tree, testPoints.stream().map(OutputSpacePoint::getInputSpacePoint).collect(Collectors.toList()));
//        double ssReg = predicted.stream().mapToDouble(OutputSpacePoint::getValue).map(a->(a-mean)*(a-mean)).sum();
        double ssReg = 0.0;
        for(OutputSpacePoint o : testPoints) {
            try {
                double actual = o.getValue();
                double predicted = models.get(tree.getLeaf(o).getId()).getPoint(o.getInputSpacePoint()).getValue();
                ssReg+= (actual - predicted)*(actual - predicted);
            } catch (Exception e) {

            }
        }
        return 1.0 - (ssReg/ssTotal);
    }

    private static HashMap<String, Model> createModels(DecisionTree tree) {
        HashMap<String, Model> models = new HashMap<>();
        for(DecisionTreeLeafNode l : tree.getLeaves()) {
            LinearRegression model  = new LinearRegression();
            model.configureClassifier();
            try {
                model.feed(l.getPoints());
                model.train();
            } catch (Exception e) {
                e.printStackTrace();
            }
            models.put(l.getId(), model);
        }
        return models;
    }

    private static List<OutputSpacePoint> predictions(DecisionTree tree, List<InputSpacePoint> actual) {
        HashMap<String, Model> models = createModels(tree);
        List<OutputSpacePoint> predictions = new LinkedList<>();
        for(InputSpacePoint i : actual) {
            Model m = models.get(tree.getLeaf(i).getId());
            try {
                predictions.add(m.getPoint(i));;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return predictions;
    }
}
