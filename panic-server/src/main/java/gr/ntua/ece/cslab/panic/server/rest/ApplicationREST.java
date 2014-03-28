/*
 * Copyright 2014 Giannis Giannakopoulos.
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

import gr.ntua.ece.cslab.panic.server.containers.Application;
import gr.ntua.ece.cslab.panic.server.containers.beans.ApplicationInfoBean;
import gr.ntua.ece.cslab.panic.server.containers.beans.ProfilingBean;
import gr.ntua.ece.cslab.panic.server.shared.ApplicationList;
import gr.ntua.ece.cslab.panic.server.shared.SystemLogger;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Implements the application related API calls.
 * @author Giannis Giannakopoulos
 */

@Path("/application/")
public class ApplicationREST {

    // returns a list of the application alongside with their ids and names
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String,String> listApplications() {
        SystemLogger.get().info("listing available applications");
        return ApplicationList.getShortList();
    }
    
    // submit new application description - NOT deployment
    // the method returns the same JSON as the input, but the id attribute
    // is filled with the id
    @POST
    @Path("new-application/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInfoBean putApplication(ApplicationInfoBean appInfo) {
        Application app = new Application();
        app.setAppInfo(appInfo);
        String id = ApplicationList.add(app);
        ApplicationList.get(id).getAppInfo().setId(id);
        appInfo.setId(id);
        SystemLogger.get().info("new application with Id "+ appInfo.getId());
        return appInfo;
    }
    
    // submit profiling details for a specific application
    @POST
    @Path("{id}/add-profiling-details/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setProfilingInfo(@PathParam("id") String appId, ProfilingBean profilingDetails) {
        Application app = ApplicationList.get(appId);
        if(app == null)
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        app.setProfilingDetails(profilingDetails);
        SystemLogger.get().info("profiling details added for app with Id "+appId);
        return Response.status(Response.Status.OK).build();
    }
}
