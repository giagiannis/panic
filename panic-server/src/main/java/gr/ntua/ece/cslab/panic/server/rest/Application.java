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

import gr.ntua.ece.cslab.panic.server.shared.SystemLogger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Implements the application related API calls.
 * @author Giannis Giannakopoulos
 */

@Path("/application/")
public class Application {
    
    /**
     * Returns a JSON containing all the 
     * @return 
     */
    @GET
//    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String getApplications() {
        SystemLogger.get().info("request");
        return "Hello dummy!";
    }
    
    /**
     * Returns a JSON containing all the 
     * @param id
     * @return 
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getApplication(@PathParam("id") String id) {
        SystemLogger.get().info("request "+id);
        return "Hello dummy!";
    }
}
