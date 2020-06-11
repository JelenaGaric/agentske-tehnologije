package agent;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import ws.WS;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class UserAgent implements MessageListener{
	
	@EJB WS ws; //websocket

	@Override
	public void onMessage(Message mess) {
		
		String msg;
		try {
			msg = mess.getBody(String.class);
			ws.echoTextMessage(msg); //posalji info socketu	

		} catch (JMSException e) {
			e.printStackTrace();
		}	
		
	}
	
	

}
