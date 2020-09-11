package agent;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.MessageBean;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;
import sun.security.mscapi.CPublicKey.CECPublicKey;
import ws.WS;

@Stateful
@LocalBean
@Remote(PredictorRemote.class)

public class Predictor implements PredictorRemote{
	private AID id;
	
	@EJB WS ws;

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
		//POZIV PAJTON SKRIPTE OVDJE
		
		//npr:
		ws.echoTextMessage("{\"certainty\": \"88.00\", \"result\": \"1\"}");
	}

}
