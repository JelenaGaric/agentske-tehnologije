package beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.ACLMessage;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.User;

@Path("/master")
@LocalBean
@Stateful
public class MasterBean extends AgentCenter{
	
	
	
	
	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgentsClasses() {
		ArrayList<AgentType> retVal = new ArrayList<>();			//return list of agent types
	    return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningAgents() {
		ArrayList<Agent> retVal = new ArrayList<>();		//return list of agents which have been run
	    return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {
	    Agent retVal = new Agent();					//return agent which has been run
		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("/agents/running/{aid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopAgent(@PathParam("aid") String aid) throws JMSException {
		Agent retVal = new Agent();					//return agent which has been stopped
		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messages")
	public Response sendACLMessage(ACLMessage aclMessage) {
		return Response.ok("Ok.", MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerformatives() {
		ArrayList<String> retVal = new ArrayList<>();		//return list of peformatives from enum
	    return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
}
