package gr.ntua.ece.cslab.panic.core.fresh.tree.separators;

import gr.ntua.ece.cslab.panic.beans.containers.OutputSpacePoint;

import java.util.List;

/**
 * Class used to construct different separators.
 * Created by Giannis Giannakopoulos on 2/11/16.
 */
public class SeparatorFactory {

    public Separator create(String separatorType, List<OutputSpacePoint> points) {
        if(separatorType == null) {
            return null;
        }
        if(separatorType.equals("variance")) {
            return new VarianceSeparator(points);
        }
        return null;
    }
}
