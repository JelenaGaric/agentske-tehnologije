package beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.Data;
import data.NetworkData;
import model.CustomMessage;
import model.Host;
import model.User;

@Stateless
@Path("/messages")
@LocalBean // sad restendpointi ne moraju biti u remote interfejsu
//prima rest i prepakuje poruku u jms poruku 1 korak
public class MessageBean {

	@EJB
	Data data; // database for registered users and messages

	@EJB
	NetworkData networkData;

	@POST
	@Path("/all")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendToAll(CustomMessage message) throws JsonMappingException, JsonProcessingException {

		List<CustomMessage> temp = new ArrayList<>();
		for (User u : data.getLoggedIn()) {
			//rerouting
			if (!networkData.getThisHost().getAlias().equals(u.getHost().getAlias())) {
				ResteasyClient client1 = new ResteasyClientBuilder().build();
				ResteasyWebTarget target1 = client1
						.target("http://" + u.getHost().getAdress() + ":8080/ChatAppWar/rest/messages/user");
				message.setReciever(u);
				Response response1 = target1.request().post(Entity.entity(message, "application/json"));
				String ret1 = response1.readEntity(String.class);
				client1.close();
				return Response.status(Response.Status.OK).build();
			} else {
				
				try {
					if (data.getUserMessages().get(u.getUsername()) != null) {
						temp = data.getUserMessages().get(u.getUsername());
						temp.add(message);
						data.getUserMessages().put(u.getUsername(), temp);
					} else {
						temp = new ArrayList<>();
						temp.add(message);
						data.getUserMessages().put(u.getUsername(), temp);
					}
	
				} catch (Exception e) {
					e.printStackTrace();
					return Response.status(Response.Status.BAD_REQUEST).build();
	
				}
	
			}
	
			
		}
		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendToUser(CustomMessage message) {

		User reciever = message.getReciever();
		Host recieverHost = reciever.getHost();

		// reroute the message to the recievers node
		System.out.println(networkData.getThisHost().getAlias()+recieverHost.getAlias());
		if (!networkData.getThisHost().getAlias().equals(recieverHost.getAlias())) {
			ResteasyClient client1 = new ResteasyClientBuilder().build();
			ResteasyWebTarget target1 = client1
					.target("http://" + recieverHost.getAdress() + ":8080/ChatAppWar/rest/messages/user");
			Response response1 = target1.request().post(Entity.entity(message, "application/json"));
			String ret1 = response1.readEntity(String.class);
			client1.close();
			return Response.status(Response.Status.OK).build();
		} else {

			try {
				if (data.getUserMessages().get(message.getReciever().getUsername()) != null) {
					List<CustomMessage> temp = data.getUserMessages().get(message.getReciever().getUsername());
					temp.add(message);
					data.getUserMessages().put(message.getReciever().getUsername(), temp);
					if (data.getUserMessages().get(message.getAuthor().getUsername()) != null) {
						List<CustomMessage> temp2 = data.getUserMessages().get(message.getAuthor().getUsername());
						temp2.add(message);
						data.getUserMessages().put(message.getAuthor().getUsername(), temp2);
					}else {
						List<CustomMessage> temp2 = new ArrayList<>();
						temp2.add(message);
						data.getUserMessages().put(message.getAuthor().getUsername(), temp2);
					}

				} else {
					List<CustomMessage> temp = new ArrayList<>();
					temp.add(message);
					data.getUserMessages().put(message.getReciever().getUsername(), temp);
					if (data.getUserMessages().get(message.getAuthor().getUsername()) != null) {
						List<CustomMessage> temp2 = data.getUserMessages().get(message.getAuthor().getUsername());
						temp2.add(message);
						data.getUserMessages().put(message.getAuthor().getUsername(), temp2);
					}else {
						List<CustomMessage> temp2 = new ArrayList<>();
						temp2.add(message);
						data.getUserMessages().put(message.getAuthor().getUsername(), temp2);
					}

				}

				return Response.status(Response.Status.OK).build();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Response.status(Response.Status.BAD_REQUEST).build();

	}

	@GET
	@Path("/{user}")
	public List<CustomMessage> getAllMessages(@PathParam("user") String user) {
		try {

			if (data.getUserMessages().get(user) != null)
				return data.getUserMessages().get(user);
			else
				return new ArrayList<CustomMessage>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<CustomMessage>();
		}
	}

}
