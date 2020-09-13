package beans;

import java.awt.DisplayMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.websocket.RemoteEndpoint.Async;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DTO.ACLMessageDTO;
import DTO.PredictResultDTO;
import DTO.predictDTO;
import agent.Collector;
import agent.Ping;
import agent.Pong;
import agent.PongRemote;
import agent.Predictor;
import data.Data;
import data.NetworkData;
import jms.JMSQueue;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;
import model.Host;
import model.MessageManager;
import model.Performative;
import model.User;
import util.JNDILookup;
import util.ObjectFactory;

@Path("/messages")
@LocalBean
@Singleton
@Startup
public class MessageBean {
	
	public static double certainty = -1.0;
	
	
	@EJB
	Data data; // data for agents and agent types
	
	@EJB
	NetworkData networkData; 
	
	@Resource(lookup = "java:jboss/exported/jms/topic/publicTopic")
	private Topic defaultTopic;
	
	private Connection connection;

	@Resource(lookup = "java:jboss/exported/jms/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@PostConstruct
	public void postConstruction() {
		try {
			connection = connectionFactory.createConnection("guest", "guest.guest.1");
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		}
		System.out.println("Created Message bean!");
	}


	@POST
	@Path("/pingpong")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response testPingPong(ACLMessageDTO aclMessageDTO) {
		System.out.println("In ping pong test...");
		
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setContent(aclMessageDTO.getContent());
		aclMessage.setPerformative(aclMessageDTO.getPerformative());
		aclMessage.setConversationId(aclMessageDTO.getSession());
		
		//creates new agent type if it doesn't already exist
		AgentType pingAgentType = this.data.createAgentType("ping");
		AgentType pongAgentType = this.data.createAgentType("pong");
		
		//creates new agent if it doesn't already exist
		Agent pingAgent = this.data.createAgent(pingAgentType, "ping");
		Agent pongAgent = this.data.createAgent(pongAgentType, "pong");
	
		List<AID> receivers = new ArrayList<>();
		receivers.add(pingAgent.getId());
		aclMessage.setRecievers(receivers);
		for(AID aid: receivers) {
			System.out.println("********AID od ping agent: " + aid.getName().toString());
			}
		aclMessage.setReplyTo(pongAgent.getId());
		
			
		this.sendMsg(aclMessage);
		
		return Response.ok(aclMessage, MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("/contractNet")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response testContractNet(ACLMessageDTO aclMessageDTO) {
		System.out.println("In contract net test...");

		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setContent(aclMessageDTO.getContent());
		aclMessage.setPerformative(aclMessageDTO.getPerformative());
		System.out.println("MSG CONTENT: " + aclMessage.getContent() + ", MSG PERFORMATIVE: " + aclMessage.getPerformative());
		
		//creates new agent type if it doesn't already exist
		AgentType initiatorAgentType = this.data.createAgentType("initiator");
		AgentType participantAgentType = this.data.createAgentType("participant");
		
		//creates new agent if it doesn't already exist
		Agent initiatorAgent = this.data.createAgent(initiatorAgentType, "initiator");
		Agent participantAgent = this.data.createAgent(participantAgentType, "participant");
	
		List<AID> receivers = new ArrayList<>();
		receivers.add(participantAgent.getId());
		aclMessage.setRecievers(receivers);
		aclMessage.setSender(initiatorAgent.getId());
		for(AID initAID: receivers) {
			System.out.println("AID agent: " + initAID.getName().toString());
		}
		
		// start(initiatorAgent, participantAgent);
		
		this.sendMsg(aclMessage);

		
		return Response.ok(aclMessage, MediaType.APPLICATION_JSON).build();
	}
	
	/*private static void start(Agent initiatorAgent, Agent participantAgent) {
		ACLMessage msg = new ACLMessage(Performative.request);
		System.out.println("POKRENULI SE CNET AGENTI");
		ArrayList<AID> receivers = new ArrayList<>();
		receivers.add(initiatorAgent.getId());
		msg.setRecievers(receivers);

	}*/
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/test")
	public Response postMsg(ACLMessageDTO aclMessageDTO) {
		System.out.println("In post msg....");
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setContent(aclMessageDTO.getContent());
		
		AID sender = this.data.getAIDByIndex(aclMessageDTO.getSenderIndex());

		List<AID> receivers = new ArrayList<>();
		for (int receiverIndex : aclMessageDTO.getReceiverIndexes()) {
			receivers.add(this.data.getAIDByIndex(receiverIndex));
		}

		if (sender == null || receivers.size() == 0) {
			System.out.println("You need to set a sender and receivers.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		aclMessage.setSender(sender);
		aclMessage.setRecievers(receivers);

		this.sendMsg(aclMessage);
		
		return Response.ok(aclMessage, MediaType.APPLICATION_JSON).build();
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response predictResult(predictDTO predictDTO) throws InterruptedException {

		ObjectMapper mapper = new ObjectMapper();
		String predictDTOJSON = "";
		try {
			predictDTOJSON = mapper.writeValueAsString(predictDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(predictDTOJSON.equals("")) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Empty fields.").build();
		}
		
		AgentType collectorAgentType = this.data.createAgentType("collector");
		Agent collector = this.data.createAgent(collectorAgentType, "collector");

		AgentType predictorAgentType = this.data.createAgentType("predictor");
		Agent predictor = this.data.createAgent(predictorAgentType, "predictor");
		
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setContent(predictDTOJSON);
		aclMessage.setPerformative(Performative.request);
		aclMessage.setReplyTo(predictor.getId());
		
		ArrayList<AID> receivers = new ArrayList<>();
		receivers.add(collector.getId());
		aclMessage.setRecievers(receivers);
				
    	sendMsg(aclMessage);
			   
		return Response.ok().build();
		        
	}


	@POST
	@Path("/acl")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postMsg(ACLMessage aclMessage) {
		sendMsg(aclMessage);
		return Response.ok(aclMessage, MediaType.APPLICATION_JSON).build();
		
	}
	
	public void sendMsg(ACLMessage aclMessage) {
		try {
			for (int i = 0; i < aclMessage.getRecievers().size(); i++) {
				if (aclMessage.getRecievers().get(i) == null) {
					throw new IllegalArgumentException("AID cannot be null.");
				}
				postToReceiver(aclMessage, i);
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	private void postToReceiver(ACLMessage msg, int index) throws JMSException {
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		connection.start();

		MessageProducer producer = session.createProducer(this.defaultTopic);
		try {
			ObjectMessage jmsMsg = session.createObjectMessage(msg);
			jmsMsg.setIntProperty("AIDIndex", index);
			producer.send(jmsMsg);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerformatives() {
		// return list of performatives from enum
		ArrayList<Performative> retVal = new ArrayList<Performative>();

		Performative[] performative = Performative.values();

		for (Performative p : performative)

			retVal.add(p);

		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
//	@POST
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response sendACLMessage(ACLMessage aclMessage) {
//
//		ACLMessage aclMess = new ACLMessage();
//
//		aclMess.setSender(aclMessage.getSender());
//		aclMess.setRecievers(aclMessage.getRecievers());
//		aclMess.setPerformative(aclMessage.getPerformative());
//		aclMess.setContent(aclMessage.getContent());
//		aclMess.setContentObj(aclMessage.getContentObj());
//		aclMess.setConversationId(aclMessage.getConversationId());
//		aclMess.setEncoding(aclMessage.getEncoding());
//		aclMess.setInReplyTo(aclMessage.getInReplyTo());
//		aclMess.setLanguage(aclMessage.getLanguage());
//		aclMess.setOntology(aclMessage.getOntology());
//		aclMess.setProtocol(aclMessage.getProtocol());
//		aclMess.setReplyBy(aclMessage.getReplyBy());
//		aclMess.setReplyTo(aclMessage.getReplyTo());
//		aclMess.setReplyWith(aclMessage.getReplyWith());
//		aclMess.setUserArgs(aclMessage.getUserArgs());
//
//		this.data.getAclMessages().add(aclMess);
//
//		return Response.ok("Ok.", MediaType.APPLICATION_JSON).build();
//	}
	
	/*
	@EJB
	Data data; // database for registered users and messages

	@EJB
	NetworkData networkData;

	 * @POST
	 * 
	 * @Path("/all")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.TEXT_PLAIN) public Response sendToAll(CustomMessage
	 * message) throws JsonMappingException, JsonProcessingException {
	 * 
	 * List<CustomMessage> temp = new ArrayList<>(); for (User u :
	 * data.getLoggedIn()) { //rerouting if
	 * (!networkData.getThisHost().getAlias().equals(u.getHost().getAlias())) {
	 * ResteasyClient client1 = new ResteasyClientBuilder().build();
	 * ResteasyWebTarget target1 = client1 .target("http://" +
	 * u.getHost().getAdress() + ":8080/ChatAppWar/rest/messages/user");
	 * message.setReciever(u); Response response1 =
	 * target1.request().post(Entity.entity(message, "application/json")); String
	 * ret1 = response1.readEntity(String.class); client1.close(); return
	 * Response.status(Response.Status.OK).build(); } else {
	 * 
	 * try { if (data.getUserMessages().get(u.getUsername()) != null) { temp =
	 * data.getUserMessages().get(u.getUsername()); temp.add(message);
	 * data.getUserMessages().put(u.getUsername(), temp); } else { temp = new
	 * ArrayList<>(); temp.add(message); data.getUserMessages().put(u.getUsername(),
	 * temp); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return
	 * Response.status(Response.Status.BAD_REQUEST).build();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * } return Response.status(Response.Status.OK).build(); }
	 * 
	 * @POST
	 * 
	 * @Path("/user")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.TEXT_PLAIN) public Response sendToUser(CustomMessage
	 * message) {
	 * 
	 * User reciever = message.getReciever(); Host recieverHost =
	 * reciever.getHost();
	 * 
	 * // reroute the message to the recievers node
	 * System.out.println(networkData.getThisHost().getAlias()+recieverHost.getAlias
	 * ()); if
	 * (!networkData.getThisHost().getAlias().equals(recieverHost.getAlias())) {
	 * ResteasyClient client1 = new ResteasyClientBuilder().build();
	 * ResteasyWebTarget target1 = client1 .target("http://" +
	 * recieverHost.getAdress() + ":8080/ChatAppWar/rest/messages/user"); Response
	 * response1 = target1.request().post(Entity.entity(message,
	 * "application/json")); String ret1 = response1.readEntity(String.class);
	 * client1.close(); return Response.status(Response.Status.OK).build(); } else {
	 * 
	 * try { if (data.getUserMessages().get(message.getReciever().getUsername()) !=
	 * null) { List<CustomMessage> temp =
	 * data.getUserMessages().get(message.getReciever().getUsername());
	 * temp.add(message);
	 * data.getUserMessages().put(message.getReciever().getUsername(), temp); if
	 * (data.getUserMessages().get(message.getAuthor().getUsername()) != null) {
	 * List<CustomMessage> temp2 =
	 * data.getUserMessages().get(message.getAuthor().getUsername());
	 * temp2.add(message);
	 * data.getUserMessages().put(message.getAuthor().getUsername(), temp2); }else {
	 * List<CustomMessage> temp2 = new ArrayList<>(); temp2.add(message);
	 * data.getUserMessages().put(message.getAuthor().getUsername(), temp2); }
	 * 
	 * } else { List<CustomMessage> temp = new ArrayList<>(); temp.add(message);
	 * data.getUserMessages().put(message.getReciever().getUsername(), temp); if
	 * (data.getUserMessages().get(message.getAuthor().getUsername()) != null) {
	 * List<CustomMessage> temp2 =
	 * data.getUserMessages().get(message.getAuthor().getUsername());
	 * temp2.add(message);
	 * data.getUserMessages().put(message.getAuthor().getUsername(), temp2); }else {
	 * List<CustomMessage> temp2 = new ArrayList<>(); temp2.add(message);
	 * data.getUserMessages().put(message.getAuthor().getUsername(), temp2); }
	 * 
	 * }
	 * 
	 * return Response.status(Response.Status.OK).build();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * return Response.status(Response.Status.BAD_REQUEST).build();
	 * 
	 * }
	 * 
	 * @GET
	 * 
	 * @Path("/{user}") public List<CustomMessage> getAllMessages(@PathParam("user")
	 * String user) { try {
	 * 
	 * if (data.getUserMessages().get(user) != null) return
	 * data.getUserMessages().get(user); else return new ArrayList<CustomMessage>();
	 * } catch (Exception e) { e.printStackTrace(); return new
	 * ArrayList<CustomMessage>(); } }
	 */
}
