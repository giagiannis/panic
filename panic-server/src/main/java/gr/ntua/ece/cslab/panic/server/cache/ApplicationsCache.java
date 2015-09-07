/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.ntua.ece.cslab.panic.server.cache;

import gr.ntua.ece.cslab.panic.beans.containers.DeploymentSpace;
import gr.ntua.ece.cslab.panic.beans.rest.ApplicationInfo;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Giannis Giannakopoulos
 */
public class ApplicationsCache {
    private static HashMap<String, ApplicationInfo> applications;
    
    public static void allocateCache() {
        applications = new HashMap<>();
        
        // debug
        ApplicationInfo info = new ApplicationInfo();
        info.setName("lorem ipsum");
        DeploymentSpace space = new DeploymentSpace();
        space.addValue("cores", 1.0);
        space.addValue("cores", 2.0);
        space.addValue("cores", 4.0);
        space.addValue("cores", 8.0);
        space.addValue("ram", 1.0);
        space.addValue("ram", 2.0);
        space.addValue("ram", 4.0);
        space.addValue("ram", 8.0);
        info.setDeploymentSpace(space);
        info.setId(UUID.randomUUID().toString());
        applications.put(info.getId(), info);
    }
    
    public static synchronized String insertApplication(ApplicationInfo app) {
        String uuid = UUID.randomUUID().toString();
        applications.put(uuid, app);
        return uuid;
    }
    
    public static ApplicationInfo getApplication(String id) {
        return applications.get(id);
    }
    
    public static List<ApplicationInfo> getApplications() {
        System.out.println(applications);
        return new LinkedList<>(applications.values());
    }
}
