/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.beans.lists;

import gr.ntua.ece.cslab.panic.beans.rest.ApplicationInfo;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Giannis Giannakopoulos
 */
@XmlRootElement
public class ApplicationInfoList{
    private LinkedList<ApplicationInfo> applications;

    public ApplicationInfoList() {
    }
    
    
    public LinkedList<ApplicationInfo> getApplications() {
        return applications;
    }

    public void setApplications(LinkedList<ApplicationInfo> applications) {
        this.applications = applications;
    }
    
    
}
