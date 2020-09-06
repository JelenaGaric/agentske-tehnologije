package agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
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
			// send a request to the Pong agent, whose name is defined in the message content
			AID pongAid = new AID(msg.getContent(), this.getId().getHost(), this.getId().getType());
			ACLMessage msgToPong = new ACLMessage();
			msgToPong.setPerformative(Performative.request);
			msgToPong.setSender(this.getId());
			msgToPong.getRecievers().add(pongAid);
			// use the message manager to publish the request
			//TREBA MSG MANAGEEEERRR
			this.messageManager.sendMessage(msgToPong);
		} else if (msg.getPerformative() == Performative.inform) {
			// wait for the message
			ACLMessage msgFromPong = msg;
			// we can put and retrieve custom user arguments using the userArgs field of the ACL message
			//Map<String, Object> args = new HashMap<String, Object>(msgFromPong.getUserArgs());
			//args.put("pingCreatedOn", nodeName);
//			args.put("pingWorkingOn", getNodeName);

			System.out.println("Ping got a message from pong: " + msg.getContent());
			
		}
	}

}
