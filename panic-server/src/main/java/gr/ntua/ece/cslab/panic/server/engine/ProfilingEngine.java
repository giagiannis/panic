package gr.ntua.ece.cslab.panic.server.engine;

public interface ProfilingEngine {
	
	/**
	 * Creates a new profiling job. 
	 * @param job
	 */
	public void submitJob(ProfilingEngineJob job);
	
	/**
	 * Returns the state of profiling job.
	 * @param uuid
	 */
	public void getJobState(String uuid);
	
	/**
	 * Returns the model created by a profiling job.
	 * @param uuid
	 * @param modelType
	 */
	public void getModel(String uuid, String modelType);

}
