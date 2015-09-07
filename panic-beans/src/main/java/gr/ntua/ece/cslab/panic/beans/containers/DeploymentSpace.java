package gr.ntua.ece.cslab.panic.beans.containers;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Giannis Giannakopoulos
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DeploymentSpace {
    private final HashMap<String, ValueList> ranges;
    public DeploymentSpace() {
        this.ranges = new HashMap<>();
    }
    
    public void addValue(String key, Double value) {
        if(!this.ranges.containsKey(key) || this.ranges.get(key)==null)
            this.ranges.put(key, new ValueList());
        this.ranges.get(key).getValues().add(value);
    }

    public HashMap<String, ValueList> getRanges() {
        return ranges;
    }

    @Override
    public String toString() {
        return this.ranges.toString();
    }
}
