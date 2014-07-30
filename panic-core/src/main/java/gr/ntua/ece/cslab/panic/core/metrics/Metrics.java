/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.ntua.ece.cslab.panic.core.metrics;

import gr.ntua.ece.cslab.panic.core.containers.beans.OutputSpacePoint;
import gr.ntua.ece.cslab.panic.core.models.Model;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class Metrics {
    
    private List<OutputSpacePoint> objective;
    private Model m;

    public Metrics() {
    }

    public Metrics(List<OutputSpacePoint> objective, Model m) {
        this.objective = objective;
        this.m = m;
    }
    
    public double getMSE(){
        double sum = 0.0;
        for(OutputSpacePoint p : objective) {
            try {
                sum+=Math.pow(p.getValue()-this.m.getPoint(p.getInputSpacePoint()).getValue(),2);
            } catch (Exception ex) {
                Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sum/objective.size();
    }
    
    public double getAverageError() {
        double sum = 0.0;
        for(OutputSpacePoint p : objective) {
            try {
                sum+=Math.abs(p.getValue()-this.m.getPoint(p.getInputSpacePoint()).getValue());
            } catch (Exception ex) {
                Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sum/objective.size();
    }
    
    public double getDeviation() {
        return Math.sqrt(this.getMSE());
    }
    
}
