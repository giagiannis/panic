/*
 * Copyright 2014 Giannis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.ntua.ece.cslab.panic.server.models;

import gr.ntua.ece.cslab.panic.server.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.server.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.server.utils.CSVFileManager;
import java.util.Collections;
import java.util.List;
import weka.classifiers.functions.MultilayerPerceptron;

/**
 *
 * Multi layer perceptron, as implement by WEKA.
 * @author Giannis Giannakopoulos
 */
public class MLPerceptron extends AbsractWekaModel {
    
    public MLPerceptron() {
        super();
        classifier = new MultilayerPerceptron();
    }
    
    @Override
    public void configureClassifier() {
        //no configuration for now
    }
    
    public static void main(String[] args) throws Exception {
        MLPerceptron classifier = new MLPerceptron();
        CSVFileManager loader = new CSVFileManager();
        loader.setFilename(args[0]);
        loader.setNumberOfInputDimensions(1);
        loader.setOutputDimensionIndex(1);
        
        List<OutputSpacePoint> data = loader.getOutputSpacePoints();
        Collections.shuffle(data);
        List<OutputSpacePoint> train = data.subList(0, 10);
        for(OutputSpacePoint po : train) {
            classifier.feed(po, true);
            for(InputSpacePoint p: loader.getInputSpacePoints())
                System.out.println(classifier.getPoint(p));
        }
//        classifier.train();

    }
}
