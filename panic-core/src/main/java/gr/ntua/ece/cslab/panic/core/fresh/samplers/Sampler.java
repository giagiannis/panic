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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.core.fresh.samplers;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.samplers.ASampler;

import java.util.*;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class Sampler {

    /**
     * The point should not belong into those points.
     */
    protected Set<InputSpacePoint> forbiddenPoints;
    /**
     * HashMap representing the allowed values for each dimension.
     */
    protected HashMap<String, List<Double>> ranges;
    /**
     * Number indicating the portion of points to be returned by the sampler,
     * when compared to the entire domain.
     */
    protected Double samplingRate;
    /**
     * Number of points picked up to this point. Each "next" call should call the
     * super method next, which updates the variable.
     */
    protected int pointsPicked;
    /**
     * This number indicates the cardinality of the input domain space.
     */
    protected int maxChoices;
    
    
    protected int pointsToPick;
    
    protected static HashMap<String, String> configurationsParameters;
    
    
    protected HashMap<String, String> configuration;

    public Sampler() {
        this.ranges = new HashMap<>();
        this.samplingRate = 0.0;
        this.pointsPicked = 0;
        this.pointsToPick = 0;
        this.configuration =  new HashMap<>();
        this.configurationsParameters = new HashMap<>();

    }

    public Set<InputSpacePoint> getForbiddenPoints() {
        return forbiddenPoints;
    }


    public void setForbiddenPoints(List<OutputSpacePoint> forbiddenPoints) {
        this.forbiddenPoints = new HashSet<>();
        for(OutputSpacePoint p : forbiddenPoints) {
            this.forbiddenPoints.add(p.getInputSpacePoint());
        }
    }

    public void setDimensionsWithRanges(HashMap<String, List<Double>> ranges) {
        this.ranges = ranges;
        for (String s : this.ranges.keySet()) {      // sort each dimension ranges
            Collections.sort(this.ranges.get(s));
        }
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }
    
    public void setPointsToPick(Integer numberOfPoints) {
        this.pointsToPick = numberOfPoints;
    }

    public boolean hasMore() {
        if(this.samplingRate!=0) {
            return this.pointsPicked < (int) Math.floor(this.maxChoices * this.samplingRate);
        } else {
            return this.pointsPicked < this.pointsToPick;
        }
    }

    public InputSpacePoint next() {
        this.pointsPicked++;
        return null;
    }

    public HashMap<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(HashMap<String, String> configuration) {
        this.configuration = configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = new HashMap<>();
        String[] confArray = configuration.split(",");
        for(String conf:confArray) {
            String[] data = conf.split("=");
            this.configuration.put(data[0], data[1]);
        }
    }

    public void configureSampler() {
        this.maxChoices = 1;
        for (String s : this.ranges.keySet()) {
            this.maxChoices *= this.ranges.get(s).size();
        }
//        System.out.format("Sampling rate %.5f, Points to pick %d\n", this.samplingRate,this.pointsToPick);
        if(this.samplingRate>0.0 && this.samplingRate<= 1.0)
            this.pointsToPick = (int)Math.floor(this.samplingRate*this.maxChoices);
        else if(this.pointsToPick > 0) {
            this.samplingRate = (1.0*this.pointsToPick)/this.maxChoices;
        } else {
            System.err.println("Neither sampling rate nor pointsToPick is set");
            System.exit(1);
        }
    }

    /**
     * Method which maps every multidimensional point to a single dimension.
     * This dimension is referenced by an Integer id. The objective of the
     * function is to convert ids to InputSpacePoints.
     *
     * @param id
     * @return
     */
    protected InputSpacePoint getPointById(int id) {
        int identifier = id;
        InputSpacePoint point = new InputSpacePoint();
        for (String s : this.ranges.keySet()) {
            int index = identifier % this.ranges.get(s).size();
            identifier /= this.ranges.get(s).size();
            point.addDimension(s, this.ranges.get(s).get(index));
        }
        return point;
    }
    
    public HashMap<String, String> getConfigurationsParameters() {
		return configurationsParameters;
	}

}
