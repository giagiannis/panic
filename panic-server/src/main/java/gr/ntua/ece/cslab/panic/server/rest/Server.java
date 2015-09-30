package gr.ntua.ece.cslab.panic.server.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("server/")
public class Server {
	
	@GET
	@Path("")
	public List<String> getModels() {
		return null;
	}
}
