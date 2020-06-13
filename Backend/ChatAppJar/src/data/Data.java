package data;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import agent.Predictor;
import model.AID;
import model.ACLMessage;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 120000)
public class Data {

	private List<Agent> agents = new ArrayList<Agent>();
	private static List<Agent> runningAgents = new ArrayList<Agent>();
	private static List<AgentType> agentTypes = new ArrayList<AgentType>();
	private List<ACLMessage> aclMessages = new ArrayList<ACLMessage>();
	
	public Data() {}
	
	static {
		AgentType agentType = new AgentType();
		agentType.setModule("Modul 1");
		agentType.setName("Agent tip 1");
		agentTypes.add(agentType);
		
		AgentType agentType2 = new AgentType();
		agentType2.setModule("Modul 2");
		agentType2.setName("Agent tip 2");
		agentTypes.add(agentType2);

		AID aid = new AID();
		aid.setHost(new AgentCenter("alijas", "adresa"));
		aid.setName("aid ime");
		aid.setType(agentType);
		Agent agent = new Agent();
		agent.setId(aid);
		
		runningAgents.add(agent);
		

	}

	@Lock(LockType.READ)
	public List<Agent> getAgents() {
		return agents;
	}
	
	@Lock(LockType.WRITE)
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	@Lock(LockType.READ)
	public List<AgentType> getAgentTypes() {
		return agentTypes;
	}
	
	@Lock(LockType.WRITE)
	public void setAgentTypes(List<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}
	
	@Lock(LockType.READ)
	public List<Agent> getRunningAgents() {
		return runningAgents;
	}
	
	@Lock(LockType.WRITE)
	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}
	

	@Lock(LockType.READ)
	public List<ACLMessage> getAclMessages() {
		return aclMessages;
	}

	@Lock(LockType.WRITE)
	public void setAclMessages(List<ACLMessage> aclMessages) {
		this.aclMessages = aclMessages;
	}

	public Agent agentName(String name) {
		
		for (Agent agent : agents) {

			if (agent.getId().getName().equals(name)) 

				return agent; 
			
		}
		return null;
		
	}
		
	@Lock(LockType.READ)
	public Agent getAgent(AID id) {
		for(Agent agent : this.agents) {
			if(agent.getId().equals(id)) {
				return agent;
			}
		}
		return null;
	}
	
	@Lock(LockType.READ)
	public AgentType getAgentType(String type) {
		for(AgentType agentType : this.agentTypes) {
			if(agentType.getName().equals(type)) {
				return agentType;
			}
		}
		return null;
	}
	
	@Lock(LockType.WRITE)
	public boolean deleteAgent(Agent agent) {
		if(this.agents.contains(agent)) {
			this.agents.remove(agent);
			return true;
		}
		return false;
	}
	
	//private List<User> loggedIn = new ArrayList<User>();
	//private List<User> registered = new ArrayList<User>();
	//private HashMap<String, List<CustomMessage>> userMessages = new HashMap<String, List<CustomMessage>>();
	
	
	/*
	public void setLoggedIn(List<User> loggedIn) {
		this.loggedIn = loggedIn;
	}

	@Lock(LockType.READ)
	public List<User> getRegistered() {
		return registered;
	}

	@Lock(LockType.WRITE)
	public void setRegistered(List<User> registered) {
		this.registered = registered;
	}
	
	@Lock(LockType.READ)
	public HashMap<String, List<CustomMessage>> getUserMessages() {
		return userMessages;
	}

	@Lock(LockType.WRITE)
	public void setUserMessages(HashMap<String, List<CustomMessage>> userMessages) {
		this.userMessages = userMessages;
	}

	@Lock(LockType.READ)
	public List<User> getLoggedIn() {
		return this.loggedIn;
	}

	@Lock(LockType.WRITE)
	public void addUser(String key, User user) {
		registered.add(user);
	}
	*/
	
}
