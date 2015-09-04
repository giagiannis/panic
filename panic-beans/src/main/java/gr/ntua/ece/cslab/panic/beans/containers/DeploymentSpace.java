package gr.ntua.ece.cslab.panic.beans.containers;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class DeploymentSpace extends HashMap<String, LinkedList<Double>>{

    public DeploymentSpace() {
        super();
    }
    
    public void addValue(String key, Double value) {
        if(!this.containsKey(key) || this.get(key)==null)
            this.put(key, new LinkedList<Double>());
        this.get(key).add(value);
    }
    
    
    public static void main(String[] args) {
        DeploymentSpace foo = new DeploymentSpace();
        foo.addValue("hello", 1.0);
        foo.addValue("hello", 2.0);
        foo.addValue("hello", 3.0);
        System.out.println(foo);
    }
}
