package agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.databind.ObjectMapper;

import data.NetworkData;
import model.ACLMessage;
import model.AID;
import model.MessageManager;
import model.Performative;
import ws.WS;

@Stateful
@LocalBean
@Remote(PingRemote.class)
public class Ping implements PingRemote{

	private AID id;
	private String nodeName = "";
	
	@EJB
	NetworkData data;

	@EJB WS ws;

	private MessageManager messageManager;

	public Ping() {}
	
	protected void onInit() {
		this.nodeName = this.data.getThisNode().getAddress();
		System.out.println("Ping created on " + nodeName);
	}
	
	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;		
	}
	
	
	@Override
	public void handleMessage(ACLMessage msg) {
		if (msg.getPerformative() == Performative.request) { // inital request
			System.out.println("Ping got first message: " + msg.getContent());
			
			ObjectMapper mapper = new ObjectMapper();
			String ping1JSON = "";
			try {
				ping1JSON = mapper.writeValueAsString(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ws.echoTextMessage(ping1JSON);

			//AID pongAid = new AID(msg.getContent(), this.getId().getHost(), this.getId().getType());
			AID pongAID = msg.getReplyTo();
			ACLMessage msgToPong = new ACLMessage();
			msgToPong.setConversationId(msg.getConversationId());
			msgToPong.setContent("Message from ping...");
			msgToPong.setPerformative(Performative.request);
			msgToPong.setSender(this.getId());
			ArrayList<AID> receivers = new ArrayList<>();
			receivers.add(pongAID);
			msgToPong.setRecievers(receivers);
			
			System.out.println("Ping got a message from " + msg.getConversationId());
			
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://"+this.data.getThisNode().getAddress()+":8080/ChatAppWar/rest/messages/acl");
			Response response = target.request().post(Entity.entity(msgToPong, "application/json"));
			
			client.close();
			
		} else if (msg.getPerformative() == Performative.inform) {
			// wait for the message
			HashMap<String, Object> args = new HashMap<String, Object>(msg.getUserArgs());
			args.put("pingCreatedOn", nodeName);
			args.put("pingWorkingOn", this.data.getThisNode().getAddress());
			msg.setUserArgs(args);
			System.out.println("Ping got a message from " + msg.getSender().getName() + ": " + msg.getContent());
			System.out.println("Ping user args: " + msg.getUserArgs());

			ObjectMapper mapper = new ObjectMapper();
			String ping2JSON = "";
			try {
				ping2JSON = mapper.writeValueAsString(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ws.echoTextMessage(ping2JSON);

		}
	}

	

	
}
