package agent;

import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.AgentInterface;

@Stateful
public class Collector extends Agent implements AgentInterface {

	@Override
	public void handleMessage(ACLMessage message) {
		
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setSender(this.getId());
		aclMessage.setConversationId(message.getConversationId());
		
		//KAKO DA ZNAM U ZAVISNOSTI OD KOJE PERFORMATIVE STA RADIM?
		
	}

}
