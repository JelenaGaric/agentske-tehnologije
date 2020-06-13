package beans;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.Context;

import agent.Collector;
import data.Data;
import data.NetworkData;
import util.LookupHelper;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {

	private static Context remoteContext;

	@EJB
	Data data;
	@EJB
	static NetworkData networkData;
	
	private LookupHelper lh;

	@Override
	public void onMessage(Message arg0) {
		System.out.println(lh.agentLookup(new Collector(),true));
	}
	
	public void test() {
		
	}
		
		

}
