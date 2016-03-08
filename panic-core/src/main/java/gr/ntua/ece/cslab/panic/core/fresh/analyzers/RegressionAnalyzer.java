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

package gr.ntua.ece.cslab.panic.core.fresh.analyzers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.LinearRegression;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 3/8/16.
 */
public class RegressionAnalyzer extends Analyzer {
    public RegressionAnalyzer(List<OutputSpacePoint> samples) {
        super(samples);
    }

    @Override
    public void analyze() {
//        List<OutputSpacePoint> data = this.normalizedData(samples);
        List<OutputSpacePoint> data = samples;

        LinearRegression regression = new LinearRegression();
        regression.configureClassifier();
        String keyName = data.get(0).getKey();
        Collection<String> inputDimensions = data.get(0).getInputSpacePoint().getKeysAsCollection();
        for(String s : inputDimensions) {
            this.distances.get(keyName).put(s, 0.0);
        }
        try {
            regression.feed(data);
            regression.train();
            String output = regression.getClassifier().toString().replace("\n","").replace("\t","").replace(" ","");
            String equation = output.split("=")[1];
            String[] parts = equation.split("\\+");
            for(String s : parts) {
                String[] splittedPart = s.trim().split("\\*");
                String key = "constant";
                if(splittedPart.length>1) {
                    key = s.split("\\*")[1];
                }
                Double coefficient = 0.0;
                try{
                    coefficient = new Double(s.split("\\*")[0]);
                } catch (NumberFormatException e) {
//                    System.out.println("Tried to split: "+s);
                }
                this.distances.get(keyName).put(key, Math.abs(coefficient));
            }

//            System.out.format("%s", equation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<OutputSpacePoint> normalizedData(List<OutputSpacePoint> original) {
        Collection<String> keys =  original.get(0).getInputSpacePoint().getKeysAsCollection();
        HashMap<String, Double> max = new HashMap<>(), min = new HashMap<>(), avg = new HashMap<>();
        for(String s : keys) {
            max.put(s,original.parallelStream().mapToDouble(a->a.getInputSpacePoint().getValue(s)).max().getAsDouble());
            min.put(s, original.parallelStream().mapToDouble(a->a.getInputSpacePoint().getValue(s)).min().getAsDouble());
            avg.put(s, original.parallelStream().mapToDouble(a->a.getInputSpacePoint().getValue(s)).average().getAsDouble());
        }
        List<OutputSpacePoint> result = new LinkedList<>();
        for(OutputSpacePoint p : original) {
            OutputSpacePoint newOP = new OutputSpacePoint();
            newOP.setKey(p.getKey());
            newOP.setValue(p.getValue());
            InputSpacePoint in = p.getInputSpacePoint().getClone();
            for(String dim : keys) {
                double originalValue = in.getValue(dim);
                double newValue = 0;
                if(min.get(dim)!=max.get(dim)) {
                    newValue = (originalValue - min.get(dim)) / (max.get(dim) - min.get(dim));
                }
                in.addDimension(dim, newValue);
            }
            newOP.setInputSpacePoint(in);
            result.add(newOP);
        }
        return result;
    }
}
