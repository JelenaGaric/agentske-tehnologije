package agent;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.MessageManagerBean;
import data.NetworkData;
import model.ACLMessage;
import model.AID;
import model.Performative;
import ws.WS;

@Stateful
@LocalBean
@Remote(PongRemote.class)
public class Pong implements PongRemote {
	
	private AID id;
	
	private String nodeName;
	public static int counter;

	@EJB
	NetworkData data;
	
	@EJB WS ws;

	public Pong() {
	}

	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;	
	}
	
	protected void onInit() {

		this.nodeName = this.data.getThisNode().getAddress();
		System.out.println("Pong created on " + this.nodeName);
		counter = 0;

	}

	@Override
	public void handleMessage(ACLMessage msg) {
		if(msg.getPerformative() == Performative.request) {
			counter++;
			ACLMessage reply = new ACLMessage();
			reply.setPerformative(Performative.inform);
			reply.setContent("Pong returning a message... Counter: " + counter);
			reply.setSender(this.getId());
			reply.setConversationId(msg.getConversationId());
			ArrayList<AID> receivers = new ArrayList<AID>();
			receivers.add(msg.getSender());
			
			reply.setRecievers(receivers);
			
			HashMap<String, Object> args = new HashMap<String, Object>();
			reply.setUserArgs(args);
			reply.getUserArgs().put("pongCreatedOn", nodeName);
			reply.getUserArgs().put("pongWorkingOn", getNodeName());
			reply.getUserArgs().put("pongCounter", counter);
			
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://"+data.getThisNode().getAddress()+":8080/ChatAppWar/rest/messages/acl");
			Response response = target.request().post(Entity.entity(reply, "application/json"));
			
			client.close();

			ObjectMapper mapper = new ObjectMapper();
			String pongJSON = "";
			try {
				pongJSON = mapper.writeValueAsString(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ws.echoTextMessage(pongJSON);

			System.out.println("Pong got a message: " + msg.getContent() + "\nMessage number: " + counter);
		}
		
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	

}