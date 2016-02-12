package gr.ntua.ece.cslab.panic.core.fresh.tree.separators;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.List;

/**
 * Abstract class dictating the API of the Separator classes and holding the implementation
 * of the common functionality. The idea is that 
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public abstract class Separator {
    protected List<OutputSpacePoint> points;
    public enum Groups{LEFT, RIGHT};

    public Separator() {
        super();
    }

    public Separator(List<OutputSpacePoint> points) {
        this.setPoints(points);
    }

    public void setPoints(List<OutputSpacePoint> points) {
        this.points = points;
    }

    /**
     * This method makes the necessary computations BEFORE the separation takes place
     */
    public abstract void configure();

    /**
     * Method that each separator should override and determines whether the specified object
     * belongs to the LEFT or the RIGHT group
     */
    public abstract Groups compute(OutputSpacePoint point);
}
