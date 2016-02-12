package gr.ntua.ece.cslab.panic.core.fresh.tree.separators;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class VarianceSeparator extends Separator {
    private String dimension;
    private double value;

    public VarianceSeparator() {
    }

    public VarianceSeparator(List<OutputSpacePoint> points) {
        super(points);
    }

    @Override
    public void configure() {
        // TODO: calculate the most suitable dimension/value
        // this.dimension, this.value must be defined here
    }

    @Override
    public Groups compute(OutputSpacePoint point) {
        return (point.getInputSpacePoint().getValue(this.dimension)<= this.value?Groups.LEFT:Groups.RIGHT);
    }

    private double getVariance(List<OutputSpacePoint> points) {
        double mean = this.getMean(points);
        double variance = 0.0;
        for(OutputSpacePoint p : points) {
            variance+= (p.getValue() - mean)*(p.getValue() - mean);
        }
        if(points.size()>0) {
            variance /= points.size();
        }
        return variance;
    }

    private double getMean(List<OutputSpacePoint> points) {
        double mean = 0.0;
        for(OutputSpacePoint p : points) {
            mean += p.getValue();
        }
        if(this.points.size()>0) {
            mean /= this.points.size();
        }
        return mean;
    }
}
