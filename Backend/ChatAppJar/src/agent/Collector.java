package agent;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.NetworkData;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;
import model.Performative;

@Stateful
@LocalBean
@Remote(CollectorRemote.class)
public class Collector implements CollectorRemote{
	private AID id;
	

	@EJB
	NetworkData networkData; 

	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;		
	}
	
	public Collector() {}

	public Collector(AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		System.out.println("Collector got a message. Sending to predictor...");
		
		AID predictorAID = message.getReplyTo();
		ACLMessage messageToPredictor = new ACLMessage();
		messageToPredictor.setContent(message.getContent());
		messageToPredictor.setPerformative(Performative.request);
		messageToPredictor.setSender(this.getId());
		ArrayList<AID> receivers = new ArrayList<>();
		receivers.add(predictorAID);
		messageToPredictor.setRecievers(receivers);
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://"+networkData.getThisNode().getAddress().toString()+":8080/ChatAppWar/rest/messages/acl");
		Response response = target.request().post(Entity.entity(messageToPredictor, "application/json"));
		
		client.close();
	}
}
