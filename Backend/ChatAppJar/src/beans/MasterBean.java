package beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.vfs.VirtualFile;


import DTO.PredictDTO;
import DTO.PredictResultDTO;
import data.Data;
import data.NetworkData;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.Performative;

@Path("/master")
@LocalBean
@Singleton
@Startup
public class MasterBean extends AgentCenter{
	
	@EJB
	Data data; // data for agents and agent types

	@EJB
	NetworkData networkData;	//data for hosts

	private String masterAddress;
	
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
		
		System.out.println("Created AgentCenter!");

		InetAddress inetAddress;
		try {
			AgentCenter node = new AgentCenter();
			inetAddress = InetAddress.getLocalHost();
			node.setAddress(inetAddress.getHostAddress());
			node.setAlias(inetAddress.getHostName() + networkData.getCounter());
			//this.currentNode = (node);
			networkData.setThisNode(node);
			System.out.println("IP Address:- " + node.getAddress() + " alias: " + node.getAlias());

			try {
//					Host master=discovery();
//					if (master!=null) {
//						this.masterAddress=master.getAdress();
//						data.setMaster(master);
//						data.getNodes().add(node);
//						System.out.println("slave created");
//						handshake(node);
//					}else {
//						System.out.println("master created");
//						data.setMaster(node);
//					}
				// ovo je iz sieboga
				File f = getFile(SessionBean.class, "", "connections.properties");
				FileInputStream fileInput;
				fileInput = new FileInputStream(f);
				Properties properties = new Properties();
				try {
					properties.load(fileInput);
					fileInput.close();
					this.masterAddress = properties.getProperty("master");

					if (this.masterAddress == null || this.masterAddress.equals("")) {
						System.out.println("master created");
						networkData.setMaster(node);
					} else {
						System.out.println("slave created");
						//handshake(node);

						/*timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								heartbeat();
							}
						}, 0, 1000 * 30 * 1); // every 30 sec*/

					}
					this.masterAddress = networkData.getMaster().getAddress(); 

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
	

	//******************************************MASTER -> SLAVES*************************************************//
	
	public void delete(String alias) {
		System.out.println("Deleting node...");
		//this method calls @DELETE/node/{alias} rest method
		this.networkData.deleteNode(alias);
		for (int i = 0; i < networkData.getNodes().size(); i++) {
			if (!(networkData.getNodes().get(i).getAlias()).equals(alias)) {
				System.out.println(i + 1 + "/" + networkData.getNodes().size());
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + networkData.getNodes().get(i).getAddress()
						+ ":8080/ChatAppWar/rest/master/node/" + alias);
				Response response = target.request().delete();
				String ret = response.readEntity(String.class);
				System.out.println("deleted node from " + networkData.getNodes().get(i).getAlias());
			}
		}
	}
	
	public void handshake(AgentCenter node) {
		try {
			register(node);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Retrying handshake");
			try {
				register(node);
			} catch (Exception e1) {
				System.out.println("Handshake unsuccessful. Node not registered");
			}
		}
	}
	
	public void register(AgentCenter node) {
		System.out.println("Registering node:");
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target("http://" + this.masterAddress + ":8080/ChatAppWar/rest/master/node");
		Response response = target.request().post(Entity.entity(node, "application/json"));
		client.close();
		if (response.equals("Ok"))
			System.out.println("Node registered");
		else
			System.out.println("Node with same alias already exists");
	}

	public void sendNodesToNewNode(AgentCenter node) {
		System.out.println("****NODE ADDRESS: " + node.getAddress());
		AgentCenter ac = new AgentCenter("alias", "192.168.0.102");
		// AgentType at = new AgentType("node", "modules");
		
		// AID aid = new AID("imee", ac, at);
		this.networkData.getNodes().add(ac);
		

		try {
			// throw new EmptyStackException();
			System.out.println("try send nodes to new");
			ResteasyClient client1 = new ResteasyClientBuilder().build();
			ResteasyWebTarget target1 = client1
					.target("http://" + node.getAddress() + ":8080/ChatAppWar/rest/master/nodes");
			Response response1 = target1.request().post(Entity.entity(networkData.getNodes(), "application/json"));
			String ret1 = response1.readEntity(String.class);
			System.out.println("Sent node info to new node.");
			client1.close();
			sendNewNodeToNodes(node);
		} catch (Exception e) {
			try {
				// throw new EmptyStackException();
				ResteasyClient client1 = new ResteasyClientBuilder().build();
				ResteasyWebTarget target1 = client1
						.target("http://" + node.getAddress() + ":8080/ChatAppWar/rest/master/nodes");

				Response response1 = target1.request().post(Entity.entity(networkData.getNodes(), "application/json"));
				String ret1 = response1.readEntity(String.class);
				System.out.println("Sent node info to new node");
				client1.close();
				sendNewNodeToNodes(node);
			} catch (Exception e1) {
				System.out.println("Handshake unsuccessful: Roll-back...");
				delete(node.getAlias());
			}
		}
	}

	public void sendNewNodeToNodes(AgentCenter node) {
		System.out.println("****** NODES heh: " + node.getAddress());
		// AgentCenter ac = new AgentCenter("alias", "192.168.0.102");
		// AgentType at = new AgentType("node", "modules");
		
		// AID aid = new AID("imee", ac, at);
		//this.networkData.getNodes().add(ac);
		System.out.println("sending new node to nodes");

		// send info about new node to other nodes
		for (AgentCenter agentCenter : networkData.getNodes()) {
			if (!agentCenter.getAlias().equals(node.getAlias())) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + agentCenter.getAddress() + 
						":8080/ChatAppWar/rest/master/node");
				Response response = target.request().post(Entity.entity(node, "application/json"));
				String ret = response.readEntity(String.class);
				System.out.println("Sent new node to other nodes.");
				client.close();
			}
		}
		// send agents to new node
		/*
		ResteasyClient client1 = new ResteasyClientBuilder().build();
		ResteasyWebTarget target1 = client1
				.target("http://" + host.getAdress() + ":8080/ChatAppWar/rest/users/loggedIn");
		Response response1 = target1.request().post(Entity.entity(userData.getLoggedIn(), "application/json"));
		String ret1 = response1.readEntity(String.class);
		System.out.println("sent users to new node");
		client1.close();*/
	}
	
	//****************************************AGENT-CENTER - AGENT-CENTER****************************************//
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node")
	public Response registerNode(AgentCenter agentCenter) {
		System.out.println("******AGENT ALLIAS:" + agentCenter.getAddress()+"******");
		
		if(networkData.getThisNode().getAddress().equals(masterAddress)) {
			System.out.println("imal te rodjo");
			System.out.println("***master adresa + " + masterAddress);
			//master registering new node 
			for (AgentCenter a : networkData.getNodes()) {
				System.out.println("****All nodes:*** "+ a.getAlias() + "** " + a.getAddress());
				if (a.getAlias().equals(agentCenter.getAlias()))
					//already exists
					return Response.status(400).build();
			}
//			networkData.getNodes().add(agentCenter);
//			System.out.println("New node registered.");
//			sendNodesToNewNode(agentCenter);
		
			new Thread(new Runnable() {
				public void run() {
					networkData.getNodes().add(agentCenter);
					System.out.println("New node registered.");
					
					sendNodesToNewNode(agentCenter);
				}
			}).start();
			
			return Response.status(200).build();
		}
		//other nodes registering new node from master
		networkData.getNodes().add(agentCenter);
		return Response.status(200).build();

		
	}
	
	@GET
	@Path("/node/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupportedAgentsClasses() {
		//return list of agent types from new node to master node
	    return Response.ok(this.data.getAgentTypes(), MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/agents/classes")
	public Response registerAgentTypes(ArrayList<AgentType> agentTypes) {
		this.data.setAgentTypes(agentTypes);			//master setting agent types list to other nodes
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/nodes")
	public Response nodes(ArrayList<AgentCenter> nodes) {
		System.out.println("***List: " + nodes + "***");
		this.networkData.setNodes(nodes);		//master setting node list to other nodes
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/agents/classes/{alias}")
	public Response nodes(@PathParam("alias") String alias , ArrayList<AgentType> agentTypes) {
		//find node with given alias
		this.data.setAgentTypes(agentTypes);		//master setting node list to new node
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/node/agents/running")
	public Response getRunningAgentsNode(ArrayList<Agent> agents) {
		this.data.setRunningAgents(agents);
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("/node/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNode(@PathParam("alias") String alias) {
		//delete node from node list
		this.networkData.deleteNode(alias);
		
		// delete all node agent types and shut down agents from that node
		ArrayList<Agent> toDelete = new ArrayList<>();
		for(Agent agent : this.data.getAgents()) {
			if (agent.getId().getHost().getAlias().equals(alias)) {
				toDelete.add(agent); 
			}
		}

        for(Agent agent : toDelete) {
        	this.data.deleteAgent(agent);
        }
		
		return Response.ok("Ok", MediaType.APPLICATION_JSON).build();
		
		//return Response.noContent().build();
	}
	
	/* OVAKO ILI OVO DOLE??????????????????
	@GET
	@Path("/node/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNode(@PathParam("alias") String alias) {
		return Response.ok(this.networkData.getNode(alias), MediaType.APPLICATION_JSON).build();
	}*/
	
	@GET
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNode() {
		return Response.ok(this.networkData.getThisNode(), MediaType.APPLICATION_JSON).build();
	}
	
	//******************************************AGENT-CENTER - CLIENT*******************************************//
	
	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgentsClasses() {
		//return list of agent types
	    return Response.ok(this.data.getAgentTypes(), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningAgents() {
		ArrayList<Agent> retVal = new ArrayList<>();		//return list of agents which have been run
		
		for(Agent agent : data.getRunningAgents()) {
			retVal.add(agent);		
		}
			
	    return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
		//return list of agents which have been run
	    //return Response.ok(this.getRunningAgents(), MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response runAgent(@PathParam("type") String type, @PathParam("name") String name) {
	    Agent retVal = new Agent();					//return agent which has been run
	    AID id = new AID();
	   /* AID id = new AID();
	    
	    retVal.getId().setType(this.data.getAgentTypes(id));

	    id.setType(this.data.getAgentType(type));
	    Agent retVal = data.getAgent(id);				//return agent which has been run
	    this.data.getRunningAgents().add(retVal);		//add it to list of running agents	   
		*/

		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("/agents/running/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopAgent(@PathParam("aid") String aid) {
		Agent retVal = new Agent();				//return agent which has been stopped
		
		String agentId = retVal.getId().toString();
		if(agentId == aid) {
			
			this.data.getRunningAgents().remove(retVal);
			
		}

		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();

	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messages")
	public Response sendACLMessage(ACLMessage aclMessage) {
		
		ACLMessage aclMess = new ACLMessage();
		
		aclMess.setSender(aclMessage.getSender());
		
		aclMess.setRecievers(aclMessage.getRecievers());
		
		aclMess.setPerformative(aclMessage.getPerformative());

		aclMess.setContent(aclMessage.getContent());

		aclMess.setContentObj(aclMessage.getContentObj());

		aclMess.setConversationId(aclMessage.getConversationId());

		aclMess.setEncoding(aclMessage.getEncoding());

		aclMess.setInReplyTo(aclMessage.getInReplyTo());

		aclMess.setLanguage(aclMessage.getLanguage());

		aclMess.setOntology(aclMessage.getOntology());

		aclMess.setProtocol(aclMessage.getProtocol());

		aclMess.setReplyBy(aclMessage.getReplyBy());

		aclMess.setReplyTo(aclMessage.getReplyTo());

		aclMess.setReplyWith(aclMessage.getReplyWith());

		aclMess.setUserArgs(aclMessage.getUserArgs());

		this.data.getAclMessages().add(aclMess);
		
		return Response.ok("Ok.", MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerformatives() {
		//return list of performatives from enum
		ArrayList<Performative> retVal = new ArrayList<Performative>();

		Performative[] performative = Performative.values();

		for(Performative p : performative)

			retVal.add(p);

	    return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/predict")
	public Response predictResult(PredictDTO predictDTO) {
	    PredictResultDTO retVal = new PredictResultDTO();
		return Response.ok(retVal, MediaType.APPLICATION_JSON).build();
	}
	
	//***********************************************PREBACITI KASNIJE U UTILS*******************************************//
	public static File getFile(Class<?> c, String prefix, String fileName) {
		File f = null;
		URL url = c.getResource(prefix + fileName);
		if (url != null) {
			if (url.toString().startsWith("vfs:/")) {
				try {
					URLConnection conn = new URL(url.toString()).openConnection();
					VirtualFile vf = (VirtualFile) conn.getContent();
					f = vf.getPhysicalFile();
				} catch (Exception ex) {
					ex.printStackTrace();
					f = new File(".");
				}
			} else {
				try {
					f = new File(url.toURI());
				} catch (URISyntaxException e) {
					e.printStackTrace();
					f = new File(".");
				}
			}
		} else {
			f = new File(fileName);
		}
		return f;
	}
}
