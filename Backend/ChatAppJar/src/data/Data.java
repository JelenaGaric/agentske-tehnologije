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

import model.Agent;
import model.AgentType;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 120000)
public class Data {

	private List<Agent> agents = new ArrayList<Agent>();
	private List<AgentType> agentTypes = new ArrayList<AgentType>();
	
	public Data() {}

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
