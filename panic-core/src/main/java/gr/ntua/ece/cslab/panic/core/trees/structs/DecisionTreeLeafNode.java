package gr.ntua.ece.cslab.panic.core.trees.structs;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.Model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 1/22/16.
 */
public class DecisionTreeLeafNode extends DecisionTreeNode {

    private List<OutputSpacePoint> points;
    private Model m;

    public DecisionTreeLeafNode() {
        super();
        this.points = new LinkedList<>();
    }

    public void add(OutputSpacePoint point) {
        this.points.add(point);
    }

    public boolean isEmpty() {
        return this.points.isEmpty();
    }
}
