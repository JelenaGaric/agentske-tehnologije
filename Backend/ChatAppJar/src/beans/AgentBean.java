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
		System.out.println("***Running agents:---" + this.data.getRunningAgents());
		ArrayList<Agent> retVal = new ArrayList<>(); // return list of agents which have been run

		for (Agent agent : data.getRunningAgents()) {
			retVal.add(agent);
		}

		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();

		// return list of agents which have been run
		// return Response.ok(this.getRunningAgents(),
		// MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Path("/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {
		AgentType agentType = this.data.getAgentType(type);

		if (agentType == null) {
			System.out.println("Type not found, creating new one...");
			agentType = new AgentType();
			agentType.setName(type);
			// default module
			agentType.setModule("lol-module");
			this.data.getAgentTypes().add(agentType);
		} else {
			System.out.println("Found type " + agentType.getName());
		}

		Agent agent = this.data.getAgentByName(name);

		if (agent == null) {
			System.out.println("Agent with given name not found, creating new one...");
			if (agentType.getName().equals("collector")) {
				agent = new Collector();
			} else if (agentType.getName().equals("predictor")) {
				agent = new Predictor();
			} else {
				System.out.println("Agent cannot be created.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			//izmijenjeno u this node umjesto master
			AID aid = new AID(this.networkData.getThisNode(), agentType);
			aid.setName(name);
			agent.setId(aid);

			this.data.getAgents().add(agent);
		} else {
			System.out.println("Found agent with name " + agent.getId().getName());
		}

		if (!this.data.getRunningAgents().contains(agent))
			this.data.getRunningAgents().add(agent);
		else
			System.out.println("The agent has already been run.");

		return Response.ok(agent, MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Path("/running/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopAgent(@PathParam("aid") String aid) {
		/*
		 * Agent retVal = new Agent(); String agentId = retVal.getId().toString();
		 * 
		 * if (agentId == aid) { this.data.getRunningAgents().remove(retVal);
		 * 
		 * }
		 * 
		 * return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
		 */
		return Response.ok().build();
	}

	

}
