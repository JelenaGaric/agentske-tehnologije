package agent;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.MessageManagerBean;
import model.ACLMessage;
import model.AID;
import model.Performative;

@Stateful
@LocalBean
@Remote(PongRemote.class)
public class Pong implements PongRemote {
	
	private AID id;
	
//	private String nodeName;
	//private MessageManagerBean messageManager = new MessageManagerBean();
	public static int counter;
	
	public Pong() {
		counter = 0;
	}

	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;	
		System.out.println("MY COUNTER - " + counter);
	}
	
//	protected void onInit(String nodeName) {
//		this.nodeName = nodeName;
//		System.out.println("Pong created on " + nodeName);
//	}

	@Override
	public void handleMessage(ACLMessage msg) {
		if(msg.getPerformative() == Performative.request) {
			ACLMessage reply = new ACLMessage();
			reply.setPerformative(Performative.inform);
			reply.setContent("Pong returning a message... Counter: " + counter);
			reply.setSender(this.getId());
			ArrayList<AID> receivers = new ArrayList<AID>();
			receivers.add(msg.getSender());
			reply.setRecievers(receivers);
			counter++;
//			reply.getUserArgs().put("pongCreatedOn", nodeName);
//			reply.getUserArgs().put("pongWorkingOn", getNodeName());
			//reply.getUserArgs().put("pongCounter", ++counter);
			//messageManager.sendMessage(reply);
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/sendTestMsg");
			Response response = target.request().post(Entity.entity(reply, "application/json"));
			
			client.close();
			
			System.out.println("Pong got a message: " + msg.getContent() + "\nMessage number: " + counter);
		}
		
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	

}