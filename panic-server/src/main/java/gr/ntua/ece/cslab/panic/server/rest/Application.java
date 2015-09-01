/*
 * Copyright 2015 giannis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.ntua.ece.cslab.panic.server.rest;

import gr.ntua.ece.cslab.panic.beans.ApplicationInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;


/**
 *
 * @author Giannis Giannakopoulos
 */
@Path("application/")
public class Application {
    // get the list of applications
    @GET
    public void getApplications() {
        
    }
    
    // add new application
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public String newApplication(ApplicationInfo application) {
        System.err.println(application.getName());
        return "all good";
    }
    
    // return an application for a specific id
    @GET
    @Path("{id}/")
    public ApplicationInfo getApplication(@PathParam("id") String id) {
        ApplicationInfo info = new ApplicationInfo();
        info.setId(id);
        info.setName("paparia mantoles");
        info.setStatus("Whatever");
        return info;
    }
    
    // return the deployment space of a specific application
    @GET
    @Path("{id}/deployment/")
    public void getDeploymentSpace(@PathParam("id") String id) {
        
    }
    
    // set the deployment space of an application
    @PUT
    @Path("{id}/deployment/")
    public void setDeploymentSpace(@PathParam("id") String id) {
        
    }
    
    // batch train
    @PUT
    @Path("{id}/batch/")
    public void batchTrain(@PathParam("id") String id) {
        
    }
    
    // FUTURE PLANS -  used for online usage
    
    // start profiling for online usage
    @POST
    @Path("{id}/start/")
    public void startProfiling() {
        
    }
    
    // stop profiling for online usage
    @POST
    @Path("{id}/stop/")
    public void stopProfiling() {
        
    }
}
