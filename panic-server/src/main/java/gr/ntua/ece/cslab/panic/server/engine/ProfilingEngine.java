package gr.ntua.ece.cslab.panic.server.engine;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gr.ntua.ece.cslab.panic.beans.api.ApplicationInfo;
import gr.ntua.ece.cslab.panic.beans.api.ProfilingJobInfo;
import gr.ntua.ece.cslab.panic.beans.api.ProfilingJobType;
import gr.ntua.ece.cslab.panic.server.engine.executors.ProfilingExecutor;

public class ProfilingEngine {
	
	private ExecutorService executorService;
	private HashMap<String, ProfilingExecutor> executorList;
	
	public ProfilingEngine() {
		int numberOfExecutors = 2;
//		if(ServerStaticComponents.properties.containsKey("engine.executors.max"))
//			numberOfExecutors = new Integer(ServerStaticComponents.properties.getProperty("engine.executors.max"));
		executorService  = Executors.newFixedThreadPool(numberOfExecutors);
		this.executorList = new HashMap<String, ProfilingExecutor>();
	}
	
	/**
	 * Creates a new profiling job. The profiling job is submitted to the system, and 
	 * the UUID of the job is returned by the method. 
	 * @param profilingJobInfo
	 */
	public String submitJob(ApplicationInfo applicationInfo, ProfilingJobInfo profilingJobInfo){
		ProfilingExecutor executor = new ProfilingExecutor();
		executor.setApplicationInfo(applicationInfo);
		executor.setProfilingJobInfo(profilingJobInfo);
		executor.setId(UUID.randomUUID().toString());
		this.executorService.execute(executor);
		this.executorList.put(executor.getId(), executor);
		return executor.getId();
		
	}
	
	/**
	 * Returns the state of profiling job.
	 * @param uuid
	 */
	public void getJobState(String uuid) {
		
	}
	
	/**
	 * Returns the model created by a profiling job.
	 * @param uuid
	 * @param modelType
	 */
	public void getModel(String uuid, String modelType) {
		
	}
	
	public static void main(String[] args) {
		ProfilingEngine engine = new ProfilingEngine();
		
		ApplicationInfo appInfo = new ApplicationInfo();
		ProfilingJobInfo profilingInfo = new ProfilingJobInfo();
//		profilingInfo.setModelName("gr.ntua.ece.cslab.panic.core.models.MPPerceptron");
		profilingInfo.setModelName("gr.ntua.ece.cslab.panic.core.models.RandomCommittee");
		profilingInfo.setSamplerName("gr.ntua.ece.cslab.panic.core.samplers.special.BiasedPCASampler");
		profilingInfo.setType(ProfilingJobType.BATCH_MODELING);
		engine.submitJob(appInfo, profilingInfo);
		
	}

}
