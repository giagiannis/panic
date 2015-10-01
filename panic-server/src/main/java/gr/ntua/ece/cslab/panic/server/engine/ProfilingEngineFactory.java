package gr.ntua.ece.cslab.panic.server.engine;

import java.util.LinkedList;
import java.util.List;

import gr.ntua.ece.cslab.panic.beans.lists.OutputSpacePointList;
import gr.ntua.ece.cslab.panic.server.engine.threads.ModelingExecutor;
import gr.ntua.ece.cslab.panic.server.engine.threads.ProfilingExecutor;

public class ProfilingEngineFactory implements ProfilingEngine {

	private List<ProfilingExecutor> executors; 
	public ProfilingEngineFactory() {
		this.executors = new LinkedList<ProfilingExecutor>();
	}
	
	@Override
	synchronized public void submitJob(ProfilingEngineJobInfo job) {
		if(job.getJobType() == JobType.BATCH_MODELING)  {
			this.initializeModelingExecutor(job);
		}
	
		
	}

	@Override
	public void getJobState(String uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getModel(String uuid, String modelType) {
		// TODO Auto-generated method stub
		
	}
	
	private void initializeModelingExecutor(ProfilingEngineJobInfo job) {
		ModelingExecutor executor = new ModelingExecutor();
		OutputSpacePointList samples = job.getSamples();
		
		executor.setSamples(samples);
		
		this.executors.add(executor);
	}
	
}
