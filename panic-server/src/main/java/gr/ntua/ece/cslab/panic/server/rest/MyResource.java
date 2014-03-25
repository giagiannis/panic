/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.ntua.ece.cslab.panic.server.rest;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MediaType;

/**
 *
 * @author giannis
 */
@Path("/resource")
public class MyResource {
    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getResource(@PathParam("id") int id){
//        MyPOJO obj = new MyPOJO(id, "Helloworld");
//        return obj;
        return new MyPOJO(id, id+":"+(id*2)).getJSON().toString(4);
    }
    
    @POST
    @Path("/post/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postResource(String jsonRequest,@PathParam("id") int id) {
        System.out.println(jsonRequest);
        return new MyPOJO(id, id+":"+(id*2)).getJSON().toString(4);
    }
}
