package beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.Destroyed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.vfs.VirtualFile;

import data.Data;
import data.NetworkData;
import model.Host;
import model.User;

import java.net.URL;
import java.net.URLConnection;
import java.net.URISyntaxException;

@Singleton
@LocalBean
@Path("/host")
@Startup
public class SessionBean { // standalone.bat -c standalone-full-ha.xml to run in terminal ctrl+c to
							// gracefully exit
							//or just republish ejb without stopping in eclipse to gracefully exit and trigger
							//preDestroy

	@EJB
	NetworkData data;

	@EJB
	Data userData;

	private String masterAddress;

	//need this for preDestroy
	private Host currentNode;
	
	private Timer timer;


	@PostConstruct
	public void postConstruct() {
		System.out.println("Created Host Agent!");
		
		InetAddress inetAddress;
		try {
			Host node = new Host();
			inetAddress = InetAddress.getLocalHost();
			node.setAdress(inetAddress.getHostAddress());
			node.setAlias(inetAddress.getHostName() + data.getCounter());
			this.currentNode = (node);
			data.setThisHost(node);
			System.out.println("IP Address:- " + node.getAdress() + " alias: " + node.getAlias());
			
			try {
//				Host master=discovery();
//				if (master!=null) {
//					this.masterAddress=master.getAdress();
//					data.setMaster(master);
//					data.getNodes().add(node);
//					System.out.println("slave created");
//					handshake(node);
//				}else {
//					System.out.println("master created");
//					data.setMaster(node);
//				}
				// ovo je iz sieboga
				File f = getFile(SessionBean.class, "", "connections.properties");
				FileInputStream fileInput;
				fileInput = new FileInputStream(f);
				Properties properties = new Properties();
				try {
					properties.load(fileInput);
					fileInput.close();
					this.masterAddress = properties.getProperty("master");
					System.out.print(this.masterAddress);
					if (this.masterAddress == null || this.masterAddress.equals("")) {
						// TODO WRITE MASTER TO FILE
						System.out.println("master created");
						data.setMaster(node);
					} else {
						//data.getNodes().add(node);
						System.out.println("slave created");
						handshake(node);
						
				        timer = new Timer();
				        timer.schedule(new TimerTask() {
				            @Override
				            public void run() { 
				                heartbeat();
				            }
				         }, 0, 1000 * 30 * 1); //every 30 sec
				
					}
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

	@PreDestroy
	public void onDestroy() {
		sendShutdownSignal(this.currentNode);
	}
	
	public Host discovery() {
		String address=this.currentNode.getAdress();
		String[] parts=address.split("\\.");
		System.out.println(address);
		String addressPiece=parts[0]+"."+parts[1]+"."+parts[2];
		System.out.println("\n Discovery initiated");
		
		for(int i=0;i<256;i++) {
			if(!(addressPiece+"."+i).equals(address)) {
				ResteasyClient client = new ResteasyClientBuilder().build();
			try {
				
					System.out.println("\n Pinging "+ addressPiece+"."+i );
					
					ResteasyWebTarget target = client
							.target("http://" + addressPiece+"."+i + ":8080/ChatAppWar/rest/host/discover");
					Response response = target.request().get();
					Host ret = response.readEntity(Host.class);
					if(ret!=null) {
						System.out.println("Discovered:" + ret.getAlias()+" at " +ret.getAdress());
						return ret;
					}
					client.close();
					
				}catch(Exception e) {
					client.close();
				}
			}
		}
		return null;
	}

	
	@GET
	@Path("/discover")
	@Produces(MediaType.APPLICATION_JSON)
	public Host getDiscovered() {
		return data.getMaster();
	}
	
	public void sendShutdownSignal(Host node) {
		System.out.println("\n Shutdown initiated");
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target("http://" + this.masterAddress + ":8080/ChatAppWar/rest/host/shutdownSignal");
		Response response = target.request().post(Entity.entity(node, "application/json"));
		String ret = response.readEntity(String.class);
		System.out.println("Shutdown signal sent");
		client.close();
	}

	public void handshake(Host host) {
		try {
			register(host);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Retrying handshake");
			try {
				register(host);
			} catch (Exception e1) {
				System.out.println("Handshake unsuccessful. Node not registered");
			}
		}
	}
	
	public void heartbeat() {
		
		//randomly pick a node to ping
		System.out.println("heart.beat");
		if(data.getNodes().size()>1){
			{double x=Math.random() * data.getNodes().size();
				Host node=data.getNodes().get((int) x);
				if (!node.equals(data.getThisHost())) {
				
					try {
							String alias=ping(node.getAdress());
							if (alias!=node.getAlias()) {
								throw new Exception();
							}
							else System.out.println("\n "+alias+" is alive");
						}
						catch(Exception e) {
							try {
								String alias=ping(node.getAdress());
								if (alias!=node.getAlias()) {
									throw new Exception();
								}
								else System.out.println("\n "+alias+" is alive");
							}
							catch(Exception e1) {
								sendShutdownSignal(node);
								System.out.println("\n "+node.getAlias()+" is not responding");
							}
						}
				}
			}
		}else {
			System.out.println("No nodes to ping");
		}
	}
	
	public String ping(String nodeAdress) {
		 
		
		System.out.println("\n Pinging"+nodeAdress);
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target("http://" + nodeAdress+ ":8080/ChatAppWar/rest/host/node");
		Response response = target.request().get();
		String alias = response.readEntity(String.class);
		
		client.close();
		return alias;
	
	}
	
	@GET
	@Path("/node")
	public String recievePing() {
		return data.getThisHost().getAlias();
	}

	public void register(Host host) {
		System.out.println("Registering node:");
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target("http://" + this.masterAddress + ":8080/ChatAppWar/rest/host/register");
		Response response = target.request().post(Entity.entity(host, "application/json"));
		client.close();
		if (response.equals("ok"))
			System.out.println("Node registered");
		else
			System.out.println("Node with same alias already exists");
	}

	public void sendInfo(Host host) {
		// send info about new node to other nodes
		for (Host n : data.getNodes()) {
			if (!host.getAlias().equals(n.getAlias())) { //dont send info to new node, he already recieved his
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + n.getAdress() + ":8080/ChatAppWar/rest/host/node");
				Response response = target.request().post(Entity.entity(host, "application/json"));
				String ret = response.readEntity(String.class);
				System.out.println("sent new node to everyone");
				client.close();
			}
		}
		// send loggedInUsers to new node
		ResteasyClient client1 = new ResteasyClientBuilder().build();
		ResteasyWebTarget target1 = client1
				.target("http://" + host.getAdress() + ":8080/ChatAppWar/rest/users/loggedIn");
		Response response1 = target1.request().post(Entity.entity(userData.getLoggedIn(), "application/json"));
		String ret1 = response1.readEntity(String.class);
		System.out.println("sent users to new node");
		client1.close();
	}

	public void postNodes(Host host) {

		try {
			// throw new EmptyStackException();
			ResteasyClient client1 = new ResteasyClientBuilder().build();
			ResteasyWebTarget target1 = client1
					.target("http://" + host.getAdress() + ":8080/ChatAppWar/rest/host/nodes");
			Response response1 = target1.request().post(Entity.entity(data.getNodes(), "application/json"));
			String ret1 = response1.readEntity(String.class);
			System.out.println("Sent node info to new node");
			client1.close();
			sendInfo(host);
		} catch (Exception e) {
			try {
				// throw new EmptyStackException();
				ResteasyClient client1 = new ResteasyClientBuilder().build();
				ResteasyWebTarget target1 = client1
						.target("http://" + host.getAdress() + ":8080/ChatAppWar/rest/host/nodes");

				Response response1 = target1.request().post(Entity.entity(data.getNodes(), "application/json"));
				String ret1 = response1.readEntity(String.class);
				System.out.println("Sent node info to new node");
				client1.close();
				sendInfo(host);
			} catch (Exception e1) {
				System.out.println("Handshake unsuccessful: Roll-back");
				delete(host);

			}
		}
	}

	public void delete(Host host) {
		System.out.println("Deleting node from hosts");
		 data.getNodes().remove(host);
		for (int i = 0; i < data.getNodes().size(); i++) {
			if(!(data.getNodes().get(i).getAlias()).equals(host.getAlias())) {
				System.out.println(i + 1 + "/" + data.getNodes().size());
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client
						.target("http://" + data.getNodes().get(i).getAdress() + ":8080/ChatAppWar/rest/host/node/" + host.getAlias());
				Response response = target.request().delete();
				String ret = response.readEntity(String.class);
				System.out.println("deleted node from " + data.getNodes().get(i).getAlias());
			}
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/register")
	public String registerNode(Host host) {
		for (Host h: data.getNodes()) {
			if (h.getAlias().equals(host.getAlias()))
				return "Cancel";
		}
		new Thread(new Runnable() {
			public void run() {

				data.getNodes().add(host);
				System.out.println("node registered");
				postNodes(host);
			}
		}).start();
		return "ok";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/node")
	public String node(Host host) {
		System.out.println("New node: " + host);
		data.getNodes().add(host);
		return "OK";
	}

	@POST
	@Path("/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	public String nodes(List<Host> nodes) {
		data.setNodes(nodes);
		return "OK";
	}

	@DELETE
	@Path("/node/{alias}")
	public String delete(@PathParam("alias") String host) {
		// delete node from node list
		Iterator i = data.getNodes().iterator();
		Host node;
		while (i.hasNext()) {
			node = (Host) i.next();
			if (node.getAlias().equals(host)) {
				i.remove();
				System.out.println("\n Node has been removed");
				break;
			}
		}
		// delete all node users
		Iterator iter = userData.getLoggedIn().iterator();
		User user;
		while (iter.hasNext()) {
			user = (User) iter.next();
			if (user.getHost().getAlias().equals(host)) {
				i.remove();
				break;
			}
		}
		return "OK";
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/shutdownSignal")
	public String recieveShutdownSignal(Host node) {
		System.out.println("\nShutdown signal from node: " + node.getAlias());
		new Thread(new Runnable() {
			public void run() {
				delete(node);
			}
		}).start();
		return "Recieved";
	}

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