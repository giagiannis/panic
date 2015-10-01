package gr.ntua.ece.cslab.panic.server.engine;

import java.io.Serializable;

import gr.ntua.ece.cslab.panic.beans.lists.OutputSpacePointList;

/**
 * Info class, providing the necessary information into the 
 * @author Giannis Giannakopoulos
 *
 */
public class ProfilingEngineJobInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private JobType jobType;
	private OutputSpacePointList samples;
	private String modelName, samplerName;
	private double samplingRate;
	
	public ProfilingEngineJobInfo() {
		
	}
	
	public JobType getJobType() {
		return jobType;
	}
	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}
	public OutputSpacePointList getSamples() {
		return samples;
	}
	public void setSamples(OutputSpacePointList samples) {
		this.samples = samples;
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
	public double getSamplingRate() {
		return samplingRate;
	}
	public void setSamplingRate(double samplingRate) {
		this.samplingRate = samplingRate;
	}
	
	

}
