package beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
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

import DTO.ACLMessageDTO;
import agent.Collector;
import agent.Predictor;
import data.Data;
import data.NetworkData;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;

@Path("/agents")
@LocalBean
@Singleton
@Startup
public class AgentBean {

	@EJB
	Data data; // data for agents and agent types

	@EJB
	NetworkData networkData; // data for nodes

	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgentsClasses() {
		// return list of agent types
		System.out.println("***Agent types:---" + this.data.getAgentTypes());
		return Response.ok(this.data.getAgentTypes(), MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningAgents() {
		ArrayList<Agent> retVal = new ArrayList<>(); 

		for (Agent agent : data.getRunningAgents()) {
			retVal.add(agent);
		}

		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAgents() {
		ArrayList<Agent> retVal = new ArrayList<>(); 

		for (Agent agent : data.getAgents()) {
			retVal.add(agent);
		}

		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Path("/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {
		//creates new agent type if it doesn't already exist
		AgentType agentType = this.data.createAgentType(type);
		
		//creates new agent if it doesn't already exist
		Agent agent = this.data.createAgent(agentType, name);
		
		if(agent == null)
			return Response.status(Response.Status.BAD_REQUEST).entity("Agent can't be created (unknown type).").build();

		if(!agent.getId().getType().getName().equals(agentType.getName())) 
			return Response.status(Response.Status.BAD_REQUEST).entity("Agent already exists, but it's a different type.").build();
		
		return Response.ok(agent, MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("/running/{aid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopAgent(@PathParam("aid") String aid) {
		Agent agent = this.data.getAgentByName(aid);
		
		if(agent == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Agent with given name doesn't exist.").build();
		}
		
		AID agentAID = agent.getId();
		
		if(this.data.stopRunningAgent(agentAID))
			return Response.ok().build();
		
		return Response.status(Response.Status.BAD_REQUEST).entity("Agent has already been stopped.").build();
	}

	

}
