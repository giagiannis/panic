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

package gr.ntua.ece.cslab.panic.core.models;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RandomCommittee is the implementation of an EoC algorithm. The underneath 
 * classifier is a MultiLayerPerceptron.
 * @author Giannis Giannakopoulos
 */
public class RandomCommittee extends AbstractWekaModel{
	
    public RandomCommittee() {
        super();
        this.classifier = new weka.classifiers.meta.RandomCommittee();
    }
    @Override
    public void configureClassifier() {
        String[] options = this.classifier.getOptions();
        
        int index=0;
        for(String s:options) {
            if(s.equals("-W"))
                break;
            index++;
        }
        
        String[] newOptions = new String[index+2];
        System.arraycopy(options, 0, newOptions, 0, newOptions.length);
        newOptions[index+1] = "weka.classifiers.functions.MultilayerPerceptron";
        try {
            this.classifier.setOptions(newOptions);
        } catch (Exception ex) {
            Logger.getLogger(RandomCommittee.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
