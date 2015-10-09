package gr.ntua.ece.cslab.panic.server.engine.executors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import gr.ntua.ece.cslab.panic.beans.api.ApplicationInfo;
import gr.ntua.ece.cslab.panic.beans.api.ProfilingJobInfo;
import gr.ntua.ece.cslab.panic.beans.api.ProfilingJobType;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.ValueList;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.core.samplers.Sampler;

public class ProfilingExecutor implements Runnable {

	private String id;
	private ApplicationInfo applicationInfo;
	private ProfilingJobInfo profilingJobInfo;

	private Model model;
	private Sampler sampler;

	private static Logger logger = Logger.getLogger(ProfilingExecutor.class.getName());

	public ProfilingExecutor() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public void setApplicationInfo(ApplicationInfo applicationInfo) {
		this.applicationInfo = applicationInfo;
	}

	public ProfilingJobInfo getProfilingJobInfo() {
		return profilingJobInfo;
	}

	public void setProfilingJobInfo(ProfilingJobInfo profilingJobInfo) {
		this.profilingJobInfo = profilingJobInfo;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Sampler getSampler() {
		return sampler;
	}

	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}

	@Override
	public void run() {
		if (this.profilingJobInfo.getType() == ProfilingJobType.BATCH_MODELING) {
			this.batchModeling();
		} else if (this.profilingJobInfo.getType() == ProfilingJobType.BATCH_PROFILING) {
			this.batchProfiling();
		} else {
			logger.severe("Do not know what to do!");
		}
	}

	// profiling-modeling methods
	private void batchModeling() {
		logger.info("Starting batch modeling");
		this.initializeModel();
		try {
			for (OutputSpacePoint p : this.profilingJobInfo.getList().getList()) {
				this.model.feed(p, false);
			}
			this.model.train();
		} catch (Exception e) {
			StringWriter strWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(strWriter);
			e.printStackTrace(writer);
			logger.severe(strWriter.toString());
		}
		logger.info("Batch modeling completed");
	}
	
	private void batchProfiling() {
		this.initializeModel();
		this.initializeSampler();
		
	}

	// aux methods
	private void initializeModel() {
		logger.info("Initializing model");
		String modelName = this.profilingJobInfo.getModelName();
		try {
			model = (Model) Class.forName(modelName).getConstructor().newInstance();
			model.configureClassifier();
			logger.info("Model initialized");
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			StringWriter strWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(strWriter);
			e.printStackTrace(writer);
			logger.severe(strWriter.toString());
		}
	}

	private void initializeSampler() {
		 logger.info("Initializing model and sampler");
		 String samplerName = this.getProfilingJobInfo().getSamplerName();
		 try {
		 sampler = (Sampler)
		 Class.forName(samplerName).getConstructor().newInstance();
		 HashMap<String, List<Double>> ranges = new HashMap<String,
		 List<Double>>();
		 for(Entry<String, ValueList> e: 
			 this.applicationInfo.getDeploymentSpace().getRanges().entrySet()) {
		 ranges.put(e.getKey(), e.getValue().getValues());
		 }
		 sampler.setDimensionsWithRanges(ranges);
		
		 sampler.configureSampler();
		 logger.info("Sampler initialized");
		 } catch (InstantiationException | IllegalAccessException |
		 IllegalArgumentException | InvocationTargetException
		 | NoSuchMethodException | SecurityException | ClassNotFoundException
		 e) {
		 logger.severe(e.getCause().toString());
		 }
	}
}
