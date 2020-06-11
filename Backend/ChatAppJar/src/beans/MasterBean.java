package beans;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
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

import DTO.PredictDTO;
import DTO.PredictResultDTO;
import data.Data;
import data.NetworkData;
import model.ACLMessage;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Path("/master")
@LocalBean
@Stateful
public class MasterBean extends AgentCenter{
	
	@EJB
	Data data; // data for agents and agent types

	@EJB
	NetworkData networkData;	//data for hosts

	private Connection connection;
	@Resource(lookup = "java:jboss/exported/jms/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(lookup = "java:jboss/exported/jms/topic/publicTopic")
	private Topic defaultTopic;

	@PostConstruct
	public void postConstruction() {
		try {
			connection = connectionFactory.createConnection("guest", "guest.guest.1");
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	//****************************************AGENT-CENTER - AGENT-CENTER****************************************//
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node")
	public Response registerNode(AgentCenter agentCenter) {
		//new node notifying master node
		//handshake 
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/node/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupportedAgentsClasses() {
		//return list of agent types from new node to master node
	    return Response.ok(this.data.getAgentTypes(), MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/publish")
	public Response publishNode(AgentCenter agentCenter) {
		for (AgentCenter a : networkData.getNodes()) {
			if (a.getAlias().equals(agentCenter.getAlias()))
				return Response.ok("Cancel", MediaType.APPLICATION_JSON).build();
		}
		new Thread(new Runnable() {
			public void run() {
				networkData.getNodes().add(agentCenter);
				System.out.println("New node registered.");
				//TODO:implement postNodes method ***************************************** da li treba????
				//postNodes(agentCenter);
			}
		}).start();
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build(); 
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/agents/classes")
	public Response registerAgentTypes(ArrayList<AgentType> agentTypes) {
		this.data.setAgentTypes(agentTypes);			//master setting agent types list to other nodes
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/nodes")
	public Response nodes(ArrayList<AgentCenter> nodes) {
		this.networkData.setNodes(nodes);		//master setting node list to other nodes
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/agents/classes/{alias}")
	public Response nodes(@PathParam("alias") String alias , ArrayList<AgentType> agentTypes) {
		//find node with given alias
		this.data.setAgentTypes(agentTypes);		//master setting node list to new node
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/agents/running")
	public Response getRunningAgentsNode(ArrayList<Agent> agents) {
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	//******************************************AGENT-CENTER - CLIENT*******************************************//
	
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
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/predict")
	public Response predictResult(PredictDTO predictDTO) {
	    PredictResultDTO retVal = new PredictResultDTO();
		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
}
