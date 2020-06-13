package agent;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;

@Stateful
@LocalBean
@Remote(CollectorRemote.class)
public class Collector implements CollectorRemote{
	private AID id;

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
		System.out.println( message.getContent() );
		
	}
}
