package jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.ACLMessage;

public class JMSQueue {
	
	public JMSQueue(ACLMessage message)
	{
		try 
		{
			System.out.println("EE");
			Context context = new InitialContext();
			
			ConnectionFactory cf = (ConnectionFactory) context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
			Queue queue = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();
			
			 Connection connection;
			try
			{
				connection = cf.createConnection("guest", "guest.guest.1");
				Session session = connection.createSession();
				
				connection.start();
				MessageProducer messageProducer = session.createProducer(queue);

				ObjectMessage objectMessage = session.createObjectMessage(message);
				
				messageProducer.send(objectMessage);
				
				messageProducer.close();
				session.close();
				connection.close();
				
			}
			catch (JMSException e)
			{
			
				e.printStackTrace();
			}
			 
			 
		} 
		catch (NamingException e) 
		{
			e.printStackTrace();
		}
		
	}
	

}
