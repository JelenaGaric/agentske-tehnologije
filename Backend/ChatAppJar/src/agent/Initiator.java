package agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.websocket.Session;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jms.JMSQueue;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import util.ContractNetTender;

@Stateful
@LocalBean
@Remote(InitiatorRemote.class)
public class Initiator implements InitiatorRemote {

	//private HashMap<String, ContractNetTender> sessions = new HashMap<String, ContractNetTender>();
	private HashMap<String, Integer> sentCFPNumber = new HashMap<String, Integer>();
	//private ArrayList<ContractNetTender> sessions = new ArrayList<ContractNetTender>();
	public static HashMap<AID, Integer> offers = new HashMap<>();

	private AID id;

	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;

	}

	@Override
	public void handleMessage(ACLMessage message) {
		// client initiated - sends call for proporsal to all running agents
		switch (message.getPerformative()) {
		case request: // client initiated, sends cpf to all running participant agents
			System.out.println("Initiator agent got first message: " + message.getContent());
			handleRequest(message);
			waitForParticipans(message, 5);
			break;

		case resume:
			chooseAgent(message);
			break;

		case refuse:
			handleRefuse(message);
			break;

		case propose:
			handlePropose(message);
			waitForParticipans(message, 5);

			break;

		case failure:
			System.out.println("PROCESS ABORTED: Choosen agent: [" + message.getSender().getName() + " - "
					+ message.getSender().getType().getName() + "] failed to execute the task.");
			break;

		case inform:
			System.out.println(
					"Choosen agent: [" + message.getSender().getName() + " - " + message.getSender().getType().getName()
							+ "] successfully executed the task. Returned: " + message.getContent());
			break;

		default:
			System.out.println("Performative not supported.");
		}

	}

	private void handlePropose(ACLMessage message) {
		try {
			int offer = (int) message.getContentObj();
			addOfferToSession(message.getConversationId(), message.getSender(), offer);
			System.out.println("Agent: [" + message.getSender().getName() + " - "
					+ message.getSender().getType().getName() + "] offered: " + offer);
		} catch (Exception e) {
			System.out.println("OFFER CANCELED: Agent: [" + message.getSender().getName() + " - "
					+ message.getSender().getType().getName() + "] sends an invalid value.");
			addOfferToSession(message.getConversationId(), message.getSender(), -1);
		}

	}

	private void addOfferToSession(String conversationID, AID aid, int value) {
		this.offers.put(aid, value);
		System.out.println(offers.size());

		for (Entry<AID, Integer> entry : this.offers.entrySet()) {
			System.out.println(entry.getKey().getName()+ " - " + entry.getValue());
		}
	}

	private void handleRefuse(ACLMessage message) {
		System.out.println("Agent: [" + message.getSender().getName() + " - " + message.getSender().getType().getName()
				+ "]  refused call for proposal.");

	}

	private void chooseAgent(ACLMessage message) {

		System.out.println("Biram agenta");

		AID acceptedAgent = getBestOffer();

		if (acceptedAgent == null) {
			System.out.println("All agents refused to propose.");
			return;
		}

		System.out.println(
				"The choosen agent: [" + acceptedAgent.getName() + " - " + acceptedAgent.getType().getName() + "]");
	
		ArrayList<AID> agents = new ArrayList<AID>();


		for (Entry<AID, Integer> entry : offers.entrySet()) {
			agents.add(entry.getKey());
		}

		for (AID agent : agents) {
			if (agent.equals(acceptedAgent)) // send accept proposal
			{
				ACLMessage acceptMessage = new ACLMessage();
				acceptMessage.setSender(this.getId());
				ArrayList<AID> receivers = new ArrayList<>();
				receivers.add(acceptedAgent);
				acceptMessage.setRecievers(receivers);
				acceptMessage.setContent("Accepted aid: " + agent.getName());
				acceptMessage.setPerformative(Performative.acceptProposal);
				acceptMessage.setConversationId(message.getConversationId());

				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
				Response response = target.request().post(Entity.entity(acceptMessage, "application/json"));
				
				client.close();
			} else // send decline proposal
			{
				ACLMessage declineMessage = new ACLMessage();
				declineMessage.setSender(this.getId());
				declineMessage.setContent("Declined aid: " + agent.getName());
				declineMessage.setConversationId(message.getConversationId());
				ArrayList<AID> receivers = new ArrayList<>();
				receivers.add(agent);
				declineMessage.setRecievers(receivers);
				declineMessage.setPerformative(Performative.rejectProposal);
				
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
				Response response = target.request().post(Entity.entity(declineMessage, "application/json"));
				
				client.close();
			}
		}

	}

	private void handleRequest(ACLMessage msg) {
				
		AID participantAID = msg.getReplyTo();
		ACLMessage msgToParticipant = new ACLMessage();
		msgToParticipant.setContent("Message from initiator...");
		msgToParticipant.setPerformative(Performative.request);
		msgToParticipant.setSender(this.getId());
		ArrayList<AID> receivers = new ArrayList<>();
		receivers.add(participantAID);
		msgToParticipant.setRecievers(receivers);
		
	
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
		Response response = target.request().post(Entity.entity(msgToParticipant, "application/json"));
		

		client.close();
		
		System.out.println("CALL FOR PROPOSAL MESSAGE FROM INIT: " + msgToParticipant.getContent().toString() + "FROM " + msgToParticipant.getSender().toString());
		


		/*// create a new request bidding session
		sessions.put(msg.getConversationId(), new ContractNetTender());

		ACLMessage callForProposalMessage = new ACLMessage();
		callForProposalMessage.setContent("CONTRACT NET: Call for proposal - from Initiator");
		callForProposalMessage.setSender(this.getId());

		// get all PaAIDcipant id's
		ArrayList<AID> receivers = new ArrayList<AID>();
		AID participantAID = msg.getReplyTo(); //za 1?

		@SuppressWarnings("unchecked")
		ArrayList<Agent> arrayList = (ArrayList<Agent>) msg.getContentObj();
		for (Agent a : arrayList) {
			if (a.getId().getType().getName().equals("Participant"))
				receivers.add(a.getId());
		}

		if (receivers.size() == 0) {
			System.out.println("PROCESS ABORTED: No running Participant agents found.");
			return;
		}
		
		receivers.add(participantAID);
		callForProposalMessage.setRecievers(receivers);
		
		callForProposalMessage.setContent("Message from initiator agent....");
		callForProposalMessage.setSender(this.getId());
		System.out.println("***KOJI AID OVDJE DOBIJEM???:   " + this.getId());

		sentCFPNumber.put(msg.getConversationId(), receivers.size());
		// callForProposalMessage.setRecievers(receivers);

		callForProposalMessage.setConversationId(msg.getConversationId());
		callForProposalMessage.setPerformative(Performative.cfp);
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
		Response response = target.request().post(Entity.entity(callForProposalMessage, "application/json"));
		
		client.close();

		new JMSQueue(callForProposalMessage);

		System.out.println("CALL FOR PROPOSAL MESSAGE: " + callForProposalMessage.getContent().toString() + "FROM " + callForProposalMessage.getSender().toString());*/
	}

	private void waitForParticipans(ACLMessage msg, int sleep) {
		System.out.println("Agent: [" + this.getId().getName() + " - Initiator] waits for proposals to collect for "
				+ sleep + " seconds.");

		ACLMessage pause = new ACLMessage();
		ArrayList<AID> receivers = new ArrayList<AID>();
		receivers.add(this.getId());
		pause.setRecievers(receivers);
		pause.setContent("Pause from initiator finished.");
		pause.setConversationId(msg.getConversationId());
		pause.setSender(this.getId());
		pause.setPerformative(Performative.resume);
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(sleep * 1000);
					
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client.target("http://localhost:8080/ChatAppWar/rest/messages/acl");
					Response response = target.request().post(Entity.entity(pause, "application/json"));
					
					client.close();					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

	}

	private AID getBestOffer() {
		System.out.println(this.offers.size());
		ArrayList<AID> agents = new ArrayList<AID>();
		for (Entry<AID, Integer> entry : this.offers.entrySet()) {
			agents.add(entry.getKey());
			System.out.println("aid od svih "+entry.getKey().getName());
		}
		
		ArrayList<Integer> offers = new ArrayList<Integer>();
		for (Entry<AID, Integer> entry : this.offers.entrySet()) {
			offers.add(entry.getValue());
		}
		
		int bestOffer = 0;
		int bestOfferIndex = -1;

		for (int i = 0; i < offers.size(); i++) {
			if (offers.get(i) > bestOffer) {
				bestOffer = offers.get(i);
				bestOfferIndex = i;
			}
		}
		System.out.println("Best offer: " + bestOffer + " i index "+ bestOfferIndex);

		if (bestOfferIndex == -1 || bestOffer == -1)
			return null;
		return agents.get(bestOfferIndex);
	}

}
