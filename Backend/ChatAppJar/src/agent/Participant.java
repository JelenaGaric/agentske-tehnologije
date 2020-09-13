package agent;

import java.util.ArrayList;
import java.util.Random;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jms.JMSQueue;
import model.ACLMessage;
import model.AID;
import model.Performative;

@Stateful
@LocalBean
@Remote(ParticipantRemote.class)
public class Participant implements ParticipantRemote {
	private AID id;


	@Override
	public AID getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;
		
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative())
		{
			case cfp:
				handleCallForProposal(message);
			break;
			
			case rejectProposal:
				handleRejection(message);
			break;
			
			case acceptProposal:
				handleAcceptance(message);
			break;
			
			default:
				System.out.println("Performative not supported.");
		}
		
	}

	private void handleAcceptance(ACLMessage message) {
		
		ACLMessage informMessage = new ACLMessage();
		AID participantAID = message.getSender();
		informMessage.setConversationId(message.getConversationId());
		informMessage.setContent("Participant accepted.");
		informMessage.setSender(this.getId());
		ArrayList<AID> receivers = new ArrayList<>();
		receivers.add(participantAID);
		informMessage.setRecievers(receivers);
		informMessage.setPerformative(Performative.inform);
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
		Response response = target.request().post(Entity.entity(informMessage, "application/json"));
		
		client.close();
		
	}

	private void handleRejection(ACLMessage message) {
		System.out.println("Agent: [" + this.getId().getName() + " - " + this.getId().getType().getName() + "] is refused - ok:(");

		
	}

	private void handleCallForProposal(ACLMessage message) {
		
		Random rand = new Random();
		boolean refuseOrPropose = rand.nextBoolean();
		
		if(refuseOrPropose) //send propose
		{
			ACLMessage proposeMessage = new ACLMessage();
			proposeMessage.setSender(this.getId());
			proposeMessage.setConversationId("cnet");
			proposeMessage.setContent("Call for proposal: sending propose...");
			ArrayList<AID> receivers = new ArrayList<>();
			receivers.add(message.getSender());
			proposeMessage.setRecievers(receivers);
			proposeMessage.setPerformative(Performative.propose);
			
			proposeMessage.setContentObj(rand.nextInt(101)); //send a random number [0 - 100]
			
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
			Response response = target.request().post(Entity.entity(proposeMessage, "application/json"));
			
			client.close();
		}
		else					//send refuse
		{
			ACLMessage rejectMessage = new ACLMessage();
			rejectMessage.setSender(this.getId());
			rejectMessage.setConversationId("cnet");

			rejectMessage.setContent("Call for proposal: refused.");
			ArrayList<AID> receivers = new ArrayList<>();
			receivers.add(message.getSender());
			rejectMessage.setRecievers(receivers);
			rejectMessage.setPerformative(Performative.refuse);

			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
			Response response = target.request().post(Entity.entity(rejectMessage, "application/json"));
			
			client.close();
			}
		
	}

}
