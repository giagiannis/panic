package gr.ntua.ece.cslab.panic.server.engine.threads;

import gr.ntua.ece.cslab.panic.server.engine.JobStatus;
import gr.ntua.ece.cslab.panic.server.engine.JobType;

public class ProfilingExecutor extends Thread{
	
	protected String threadId;
	protected JobType jobType;
	protected JobStatus jobStatus;
	
	public ProfilingExecutor() {
		
	}
	
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public JobType getJobType() {
		return jobType;
	}
	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}
	
	public JobStatus getStatus() {
		return this.jobStatus;
	}
	
}
