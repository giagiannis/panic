package gr.ntua.ece.cslab.panic.server.engine.threads;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.lists.OutputSpacePointList;
import gr.ntua.ece.cslab.panic.core.models.Model;
import gr.ntua.ece.cslab.panic.server.engine.JobType;

/**
 * Thread that is used to model a set of performance points.
 * @author Giannis Giannakopoulos
 *
 */
public class ModelingExecutor extends ProfilingExecutor {
	private Model model;
	private OutputSpacePointList samples;
	
	public ModelingExecutor() {
		super();
		this.jobType = JobType.BATCH_MODELING;
	}

	public Model getModel() {
		return model;
	}

	/**
	 * The model should be previously configured prior to setting.
	 * @param model
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	public OutputSpacePointList getSamples() {
		return samples;
	}

	public void setSamples(OutputSpacePointList samples) {
		this.samples = samples;
	}
	
	@Override
	public void run() {
		super.run();
		for(OutputSpacePoint p : this.samples.getList()) {
			try {
				this.model.feed(p, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			this.model.train();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
