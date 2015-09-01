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

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 *
 * @author Giannis Giannakopoulos
 */
@Path("/profiling/")
public class Profiling {
    
    // returns samples of the specific profiling scenario
    @GET
    @Path("{id}/samples/")
    public void getSamples(String id) {
        
    }
    
    // returns the approximate points either for a specific point or for
    // all the deployment space
    @GET
    @Path("{id}/model/")
    public void getModeledPoints(String id) {
        
    }
    
    // insert new sample(s) to the profiling
    @PUT
    @Path("{id}/samples/")
    public void insertSamples(String id) {
        
    }
    
    
}
