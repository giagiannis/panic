package gr.ntua.ece.cslab.panic.core.fresh.tree.nodes;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.List;

/**
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class DecisionTreeLeafNode extends DecisionTreeNode {
    private final List<OutputSpacePoint> points;

    public DecisionTreeLeafNode(List<OutputSpacePoint> points) {
        this.points = points;
    }

    public List<OutputSpacePoint> getPoints() {
        return points;
    }
}
