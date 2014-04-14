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

import gr.ntua.ece.cslab.panic.server.containers.beans.InputSpacePoint;
import gr.ntua.ece.cslab.panic.server.containers.beans.OutputSpacePoint;
import java.util.LinkedList;
import java.util.List;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Abstract class implementing multiple methods used for Weka classifiers.
 * @author Giannis Giannakopoulos
 */
public abstract class AbstractWekaModel implements Model {

    protected List<OutputSpacePoint> pointsSampled;
    protected Classifier classifier;
    
    public AbstractWekaModel() {
        this.pointsSampled = new LinkedList<>();
    }
    
    @Override
    public void feed(OutputSpacePoint point) throws Exception{
        this.feed(point, true);
    }

    @Override
    public void feed(OutputSpacePoint point, boolean retrain) throws Exception{
        this.pointsSampled.add(point);
        if(retrain)
            this.train();
    }

    @Override
    public void train() throws Exception {
        this.classifier.buildClassifier(getInstances(pointsSampled));
    }

    @Override
    public OutputSpacePoint getPoint(InputSpacePoint point) throws Exception {
        OutputSpacePoint result = new OutputSpacePoint();
        result.setInputSpacePoint(point);
        result.setValue("objective", this.classifier.classifyInstance(convertPointToInstance(point)));
        return result;
    }
    
    /**
     * Converts an output space point to a Weka instance
     * @param point
     * @return 
     */
    public static Instance convertPointToInstance(OutputSpacePoint point) {
        Instance inst = new  Instance(point.getInputSpacePoint().numberDimensions()+1);
        int index = 0;
        for(String k:point.getInputSpacePoint().getKeysAsCollection()){
            Attribute att = new Attribute(k, index++);
            inst.setValue(att, point.getInputSpacePoint().getValue(k));
        }
        inst.setValue(new Attribute(point.getKey(), index++), point.getValue());
        return inst;
    }
    
    /**
     * Converts an input space point to a Weka instance.
     * @param point
     * @return 
     */
    public static Instance convertPointToInstance(InputSpacePoint point) {
        Instance inst = new  Instance(point.numberDimensions()+1);
        int index = 0;
        for(String k:point.getKeysAsCollection()){
            Attribute att = new Attribute(k, index++);
            inst.setValue(att, point.getValue(k));
        }
        inst.setMissing(index);
        return inst;
    }

    /**
     * Creates a new dataset out of a OutputSpacePoint list.
     * @param points
     * @return 
     */
    protected static Instances getInstances(List<OutputSpacePoint> points) {
        
        OutputSpacePoint first = points.get(0);
        Instance inst = convertPointToInstance(first);
        FastVector att  = new FastVector(first.getInputSpacePoint().numberDimensions()+1);
        int index=0;
        for(String s:first.getInputSpacePoint().getKeysAsCollection())
            att.addElement(new Attribute(s, index++));
        att.addElement(new Attribute(first.getKey(), index++));
        
        Instances instances = new Instances("instances", att, first.getInputSpacePoint().numberDimensions()+1);
        for(OutputSpacePoint p : points)
            instances.add(convertPointToInstance(p));
        instances.setClassIndex(instances.numAttributes()-1);
        return instances;
    }
}
