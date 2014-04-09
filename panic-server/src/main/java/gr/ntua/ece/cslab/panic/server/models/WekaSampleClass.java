/*
 * Copyright 2014 Giannis Giannakopoulos.
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

import weka.classifiers.functions.IsotonicRegression;
import weka.core.Instance;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class WekaSampleClass extends AbsractModel{

    private IsotonicRegression classifier;

    
    public WekaSampleClass() {
        
    }    
    
    public void train() throws Exception {
        classifier = new IsotonicRegression();
        classifier.buildClassifier(this.instancesForTraining);
        
    }
    
    // the following methods only work for a single dimension input space
    public double getAppxValue(int x) throws Exception {
        Instance instance = new Instance(2);
        instance.setValue(0, x);
        instance.setMissing(1);
        return classifier.classifyInstance(instance);
    }
    
    public double getActualValue(int x) throws Exception {
        return this.instances.instance(x).value(1);
    }
    
    public double[] getKeys() {
        double[] keys = new double[this.instances.numInstances()];
        for(int i=0;i<keys.length;i++){
            keys[i] = this.instances.instance(i).value(0);
        }
        return keys;
    }
    
    public double[] getRealValues() {
        double[] realValues = new double[this.instances.numInstances()];
        for(int i=0;i<realValues.length;i++){
            realValues[i] = this.instances.instance(i).value(1);
        }
        return realValues;
    }
    
    public double[] getAppxValues() throws Exception {
        double[] keys = this.getKeys();
        double[] values = new double[keys.length];
        for(int i=0;i<keys.length;i++){
            Instance inst = new Instance(this.instances.instance(0).numAttributes());
            inst.setValue(0, keys[i]);
            values[i] = this.classifier.classifyInstance(inst);
        }
        return values;
    }
    
    public static void main(String[] args) throws Exception {
        if(args.length<2){
            System.err.println("2 arguments needed: <input file> <sampling ratio>");
            System.exit(1);
        }
        String filename = args[0];
        
        Double sampling = new Double(args[1]);
        WekaSampleClass my = new WekaSampleClass();
        my.setInputFile(filename);
        my.sampleDataSet(sampling);
        my.train();
        double[] keys = my.getKeys(), actual = my.getRealValues(), appx = my.getAppxValues();
        for(int i=0;i<=100;i++) {
            System.out.format("%.0f\t%.2f\t%.2f\n", keys[i], actual[i], appx[i]);
        }
    }
}
