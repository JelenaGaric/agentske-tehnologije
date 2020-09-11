package agent;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.ACLMessage;
import model.AID;
import model.MessageManager;
import model.Performative;

@Stateful
@LocalBean
@Remote(PingRemote.class)
public class Ping implements PingRemote{

	private AID id;

	//private String nodeName;
	private MessageManager messageManager;

	public Ping() {}

	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;		
	}
	/*
	protected void onInit(String nodeName) {
		this.nodeName = nodeName;
		System.out.println("Ping created on " + nodeName);
	}
	*/
	
	@Override
	public void handleMessage(ACLMessage msg) {
		if (msg.getPerformative() == Performative.request) { // inital request
			System.out.println("Pong got first message: " + msg.getContent());

			// send a request to the Pong agent, whose name is defined in the message content
			//AID pongAid = new AID(msg.getContent(), this.getId().getHost(), this.getId().getType());
			AID pongAID = msg.getReplyTo();
			ACLMessage msgToPong = new ACLMessage();
			msgToPong.setContent("Message from ping...");
			msgToPong.setPerformative(Performative.request);
			msgToPong.setSender(this.getId());
			ArrayList<AID> receivers = new ArrayList<>();
			receivers.add(pongAID);
			msgToPong.setRecievers(receivers);
			
		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
			Response response = target.request().post(Entity.entity(msgToPong, "application/json"));
			
			client.close();
			
		} else if (msg.getPerformative() == Performative.inform) {
			// wait for the message
			ACLMessage msgFromPong = msg;
			// we can put and retrieve custom user arguments using the userArgs field of the ACL message
			//Map<String, Object> args = new HashMap<String, Object>(msgFromPong.getUserArgs());
			//args.put("pingCreatedOn", nodeName);
//			args.put("pingWorkingOn", getNodeName);

			System.out.println("Ping got a message from " + msg.getSender().getName() + ": " + msg.getContent());
			
		}
	}

}
