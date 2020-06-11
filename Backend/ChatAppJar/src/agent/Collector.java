package agent;

import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;

@Stateful
public class Collector extends Agent {
	
private void handleMessage(ACLMessage message) {
	
	ACLMessage aclMessage = new ACLMessage();
	aclMessage.setSender(this.getId());
	aclMessage.setConversationId(message.getConversationId());
	
	//KAKO DA ZNAM U ZAVISNOSTI OD KOJE PERFORMATIVE STA RADIM?
	
	
	
	
		
	}


}
