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

import gr.ntua.ece.cslab.panic.beans.lists.ApplicationInfoList;
import gr.ntua.ece.cslab.panic.beans.rest.ApplicationInfo;
import gr.ntua.ece.cslab.panic.server.cache.ApplicationsCache;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Giannis Giannakopoulos
 */
@Path("application/")
public class ApplicationResource {

    // get the list of applications
    @GET
    public ApplicationInfoList getApplications() {
        ApplicationInfoList list = new ApplicationInfoList();
        list.setApplications(ApplicationsCache.getApplications());
        return list;
    }

    // add new application
    @PUT
    public ApplicationInfo newApplication(ApplicationInfo application) {
    	System.out.println(application);
        ApplicationsCache.insertApplication(application);
        return application;
    }

    // return an application for a specific id
    @GET
    @Path("{id}/")
    public ApplicationInfo getApplication(@PathParam("id") String id) {
    	ApplicationInfo a = ApplicationsCache.getApplication(id);
    	if(a!=null) {
    		return a;
    	} else {
    		throw new WebApplicationException(404);
    	}
    }
    
    // delete application
    @DELETE
    @Path("{id}/")
    public void deleteApplication(@PathParam("id") String id) {
    	System.err.println("Delete application called "+ id);
    	if(ApplicationsCache.deleteApplication(id)) {
    		System.err.println("Found");
    		throw new WebApplicationException(200);
    	} else {
    		System.err.println("Not Found");
    		throw new WebApplicationException(404);
    	}
    }

    // batch profile: models, samplers
    @POST
    @Path("{id}/batch-profile/")
    public void batchProfile(@PathParam("id") String id) {

    }
    
    
    @POST
    @Path("{id}/batch-train/")
    public void batchTraing(@PathParam("id") String id) {
        
    }
}
