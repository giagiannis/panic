/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.beans.lists;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import gr.ntua.ece.cslab.panic.beans.api.ApplicationInfo;

/**
 *
 * @author Giannis Giannakopoulos
 */
@XmlRootElement
public class ApplicationInfoList{
    private List<ApplicationInfo> applications;

    public ApplicationInfoList() {
    }
    
    
    public List<ApplicationInfo> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationInfo> applications) {
        this.applications = applications;
    }
    
    
}
