package agent;

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
@Remote(PongRemote.class)
public class Pong implements PongRemote {
	
	private AID id;
	
//	private String nodeName;
	private MessageManager messageManager;
	private int counter;
	
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
	}
	
//	protected void onInit(String nodeName) {
//		this.nodeName = nodeName;
//		System.out.println("Pong created on " + nodeName);
//	}

	@Override
	public void handleMessage(ACLMessage msg) {
		if(msg.getPerformative() == Performative.request) {
			ACLMessage reply = new ACLMessage();
			/*reply.setPerformative(Performative.inform);
			reply.setSender(this.getId());
//			reply.getUserArgs().put("pongCreatedOn", nodeName);
//			reply.getUserArgs().put("pongWorkingOn", getNodeName());
			reply.getUserArgs().put("pongCounter", ++counter);
			messageManager.sendMessage(reply);
			*/
			System.out.println("Pong got a message: " + msg.getContent() + "\nMessage number: " + counter);
		}
		
	}

}