/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.beans.containers;

import java.util.LinkedList;

/**
 *
 * @author giannis
 */
public class ValueList {
    
    private LinkedList<Double> values;

    public ValueList() {
        this.values = new LinkedList<>();
    }

    
    public LinkedList<Double> getValues() {
        return values;
    }

    public void setValues(LinkedList<Double> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return this.values.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
