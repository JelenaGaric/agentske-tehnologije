package beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.Data;
import data.NetworkData;
import model.CustomMessage;
import model.Host;
import model.User;

@Stateful
@Path("/users")
@LocalBean // sad restendpointi ne moraju biti u remote interfejsu
//Agentski centar
public class UserBean {

	@EJB
	Data data; // baza korisnika i poruka
	
	@EJB
	NetworkData networkData;
	
	private Connection connection;
	@Resource(lookup = "java:jboss/exported/jms/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(lookup = "java:jboss/exported/jms/topic/publicTopic")
	private Topic defaultTopic;
	

	@PostConstruct
	public void postConstruction() {
		try {
			connection = connectionFactory.createConnection("guest", "guest.guest.1");
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		} 
	}
	

	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String login(User user) {
		
		if (data.getRegistered().contains(user)) {
			if (!data.getLoggedIn().contains(user))
				user.setHost(networkData.getThisHost());
				data.getLoggedIn().add(user);
				System.out.println(user.getUsername()+" has logged in");
				
				for (Host h:networkData.getNodes()) {
					ResteasyClient client1 = new ResteasyClientBuilder().build();
					ResteasyWebTarget target1 = client1
							.target("http://" + h.getAdress() + ":8080/ChatAppWar/rest/users/loggedIn");
					Response response1 = target1.request().post(Entity.entity(data.getLoggedIn(), "application/json"));
					String ret1 = response1.readEntity(String.class);
					client1.close();
				}
				System.out.println("Notified other nodes about the new user");
				
				ObjectMapper mapper = new ObjectMapper();
				String obj;
				try {
					obj = mapper.writeValueAsString(user);
					
					try {
						Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
						connection.start();
						MessageProducer producer = session.createProducer(this.defaultTopic);
						TextMessage message = session.createTextMessage();
						message.setText("loggedIn");
						producer.send((TextMessage) message);
						producer.close();
						connection.close();
					} catch (JMSException e) {
						e.printStackTrace();
					}
					
					return obj;
					
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}	
		}

		return null;

	}

	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(User user) {

		for (User u : data.getRegistered()) {
			if (u.getUsername().equals(user.getUsername()))
				return Response.status(Response.Status.BAD_REQUEST).build();
		}

		System.out.println("Sucessfully registered " + user.getUsername());
		data.getRegistered().add(user);

		return Response.status(Response.Status.OK).build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/loggedIn")
	public List<User> loggedIn() {
		System.out.println("loggedIn " + data.getLoggedIn());
		return data.getLoggedIn();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/loggedIn")
	//recieve users from master on register OR from another node on userlogin
	//and notify websocket clients
	public String loggedIn(List<User> users) {
		System.out.println("recieved users");
		data.setLoggedIn(users);
		
		try {
			Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			MessageProducer producer = session.createProducer(this.defaultTopic);
			TextMessage message = session.createTextMessage();
			message.setText("loggedIn");
			producer.send((TextMessage) message);
			producer.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
			
		return "OK";
	}

	@GET
	@Path("/registered")
	public List<User> registered() {
		System.out.println("registered " + data.getRegistered());
		return data.getRegistered();
	}

	@DELETE
	@Path("/loggedIn/{user}")
	@Produces(MediaType.TEXT_PLAIN)
	public String delete(@PathParam("user") String user) throws JMSException {
		System.out.println("deleting");
		
		for (User u : data.getLoggedIn()) {
			if (u.getUsername().equals(user)) {
				data.getLoggedIn().remove(u);
				Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				connection.start();
				MessageProducer producer = session.createProducer(this.defaultTopic);
				TextMessage message = session.createTextMessage();
				message.setText("loggedOut:"+user);
				producer.send((TextMessage) message);
				producer.close();
				connection.close();
				return "success";
			}
		}
		return "error";

	}
}
