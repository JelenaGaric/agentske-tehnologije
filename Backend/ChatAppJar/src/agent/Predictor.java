package agent;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import beans.MessageBean;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;

@Stateful
@LocalBean
@Remote(PredictorRemote.class)
public class Predictor implements PredictorRemote{
	private AID id;
	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;		
	}
	
	public Predictor() {}

	public Predictor(AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		System.out.println("Agent " + id.getName() + " received message " + message.getContent() );
		
	}

}
