package beans;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agent.CollectorRemote;
import agent.LookupHelper;
import data.DataLocal;
import data.NetworkDataLocal;
import model.ACLMessage;
import model.AID;
import model.Agent;
import util.JNDILookup;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {

	//private static Context remoteContext;

	@EJB
	DataLocal data;
	@EJB
	NetworkDataLocal networkData;
	@EJB
	private LookupHelper lh;

	@Override
	public void onMessage(Message message) {
//		AgentType agentType = new AgentType("collector", "lol-module");
//		this.data.getAgentTypes().add(agentType);
//		for(AgentType at:data.getAgentTypes()) {
//			System.out.println("iz mdba "+at.getName());
//		}
//		AID aid = new AID("test", networkData.getMaster(), agentType);
//		lh.lookupAgent(aid);
//		System.out.println("done");
		try {
			ACLMessage aclMessage = (ACLMessage) ((ObjectMessage) message).getObject();
			//acl.receivers.get(i);
			//deliverMessage(acl, aid);
			for(AID aid : aclMessage.getRecievers()) {
				System.out.println(aid.getName());

				//da li da lookup helper dobavlja ili direktno jndi
				//Agent agent = lh.lookupAgent(aid);
				Agent agent = JNDILookup.lookUp(JNDILookup.CollectorLookup, CollectorRemote.class);
				System.out.println(agent);

				if(agent != null) {
					agent.handleMessage(aclMessage);
				}
			}
		} catch (JMSException ex) {
			System.out.println("MDB consumer cannot process message.");
		}
		
	}
	
	public void test() {
		
	}
		
		

}
