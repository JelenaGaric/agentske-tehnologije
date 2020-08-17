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

	// private static Context remoteContext;

	@EJB
	DataLocal data;
	@EJB
	NetworkDataLocal networkData;
	@EJB
	private LookupHelper lh;

	@Override
	public void onMessage(Message message) {
		try {
			ACLMessage aclMessage = (ACLMessage) ((ObjectMessage) message).getObject();
			int i = message.getIntProperty("AIDIndex");
			AID aid = aclMessage.getRecievers().get(i);
			
			Agent agent = lh.lookupAgent(aid);

			if(agent != null) {
				agent.handleMessage(aclMessage);
			}
			
		} catch(JMSException ex) {
			System.out.println("MDB consumer cannot process message.");
		}

 }

	public void test() {

	}

}
