/*
 * Copyright 2014 Gianis Giannakopoulos.
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

package gr.ntua.ece.cslab.panic.beans.api;

import java.io.Serializable;
import gr.ntua.ece.cslab.panic.beans.lists.OutputSpacePointList;

/**
 * This class contains information about the profiling job that is submitted to the profiling engine. 
 * This struct does not fully describe the profiling scenario: information regarding the application 
 * is also needed.  
 * @author Giannis Giannakopoulos
 */
public class ProfilingJobInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String modelName;
    private String samplerName;
    private Double samplingRate;
    private Integer maxDeployments;
    private OutputSpacePointList list;
    private ProfilingJobType type;
    private ProfilingJobStatus status;
    
    public ProfilingJobInfo() {
    	
	}
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getSamplerName() {
		return samplerName;
	}
	public void setSamplerName(String samplerName) {
		this.samplerName = samplerName;
	}
	public Double getSamplingRate() {
		return samplingRate;
	}
	public void setSamplingRate(Double samplingRate) {
		this.samplingRate = samplingRate;
	}
	public Integer getMaxDeployments() {
		return maxDeployments;
	}
	public void setMaxDeployments(Integer maxDeployments) {
		this.maxDeployments = maxDeployments;
	}
	public OutputSpacePointList getList() {
		return list;
	}
	public void setList(OutputSpacePointList list) {
		this.list = list;
	}
	public ProfilingJobType getType() {
		return type;
	}
	public void setType(ProfilingJobType type) {
		this.type = type;
	}
	public ProfilingJobStatus getStatus() {
		return status;
	}
	public void setStatus(ProfilingJobStatus status) {
		this.status = status;
	}
    
    
    
    
    
}
