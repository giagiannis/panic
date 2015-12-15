package gr.ntua.ece.cslab.panic.core.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import gr.ntua.ece.cslab.panic.beans.containers.InputSpacePoint;
import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.partitioners.SplitByDimensionPartitioner;

public class EnsembleMetaModel implements Model {

	private List<Model> models;
	private List<HashMap<String, List<Double>>> regions;
	private int[] regionToModelMapping;

	private String className = "gr.ntua.ece.cslab.panic.core.models.IsoRegression";
	private List<OutputSpacePoint> sampledPoints;

	public EnsembleMetaModel() {
		this.models = new LinkedList<>();
		this.regions = new LinkedList<>();
		this.sampledPoints = new LinkedList<>();
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public List<HashMap<String, List<Double>>> getRegions() {
		return regions;
	}

	public void setRegions(List<HashMap<String, List<Double>>> regions) {
		this.regions = regions;
	}

	// implemented methods
	@Override
	public void feed(OutputSpacePoint point) throws Exception {
		this.feed(point, false);
	}

	@Override
	public void feed(OutputSpacePoint point, boolean retrain) throws Exception {
		this.sampledPoints.add(point);
		Model m = this.findModelByPoint(point.getInputSpacePoint());
		if(retrain && m!=null)
			m.train();
	}

	@Override
	public void feed(List<OutputSpacePoint> points) throws Exception {
		for(OutputSpacePoint p:points) {
			this.feed(p, false);
		}
	}

	@Override
	public void train() throws Exception {
		if(this.models.isEmpty()) {
			this.constructModels();
		}
		for (Model m:models) {
			m.train();
		}
	}

	@Override
	public OutputSpacePoint getPoint(InputSpacePoint point) throws Exception {
		Model m = this.findModelByPoint(point);
		return m.getPoint(point);
	}

	@Override
	public List<OutputSpacePoint> getPoints(List<InputSpacePoint> points) throws Exception {
		List<OutputSpacePoint> result = new LinkedList<>();
		for (InputSpacePoint p : points)
			result.add(this.getPoint(p));
		return result;
	}

	@Override
	public void configureClassifier() {

	}

	@Override
	public List<OutputSpacePoint> getOriginalPointValues() {
		return this.sampledPoints;
	}

	private Model findModelByPoint(InputSpacePoint point) {
		int modelIndex = 0;
		for (int i = 0; i < this.regions.size(); i++) {
			if (SplitByDimensionPartitioner.pointIsInRange(this.regions.get(i), point)) {
				modelIndex = this.regionToModelMapping[i];
			}
		}
		return (this.models.isEmpty()?null:this.models.get(modelIndex));
	}
	
	private void constructModels() throws Exception {
		// construct the models
		int size=(this.regions.size()>0?this.regions.size():1);
//		System.err.println("Constructing "+size+" models");
		this.regionToModelMapping = new int[size];
		for (int i = 0; i < size; i++) {
			Model m = null;
			try {
				m = (Model) Class.forName(this.className).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			this.models.add(i, m);
			this.regionToModelMapping[i] = i;
		}
		
		// feed them
		for(OutputSpacePoint p : this.sampledPoints) {
			Model m = this.findModelByPoint(p.getInputSpacePoint());
			m.feed(p,false);
		}
	}
}
