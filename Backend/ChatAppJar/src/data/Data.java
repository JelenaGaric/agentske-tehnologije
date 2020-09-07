package data;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import agent.Collector;
import agent.Ping;
import agent.Pong;
import agent.Predictor;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 120000)
public class Data implements DataLocal {

	private List<Agent> agents = new ArrayList<Agent>();
	private static List<Agent> runningAgents = new ArrayList<Agent>();
	private static List<AgentType> agentTypes = new ArrayList<AgentType>();
	private List<ACLMessage> aclMessages = new ArrayList<ACLMessage>();

	@EJB
	NetworkData networkData; 
	
	public Data() {
	}

	static {
		AgentType agentType = new AgentType();
		agentType.setModule("Modul 1");
		agentType.setName("Agent tip 1");
		agentTypes.add(agentType);

		AgentType agentType2 = new AgentType();
		agentType2.setModule("Modul 2");
		agentType2.setName("Agent tip 2");
		agentTypes.add(agentType2);

	}
	
	public Agent createAgent(AgentType agentType, String agentName) {
		Agent agent = getAgentByName(agentName);
		
		if (agent == null) {
			System.out.println("Agent with given name not found, creating new one: " + agentName);
			switch(agentType.getName()) {
				case "collector":
					agent = new Collector();
					break;
				case "predictor":
					agent = new Predictor();

					break;
				case "ping":
					agent = new Ping();
					break;
				case "pong":
					agent = new Pong();
			}
			
			if(agent == null) {
				System.out.println("Agent can't be created (unknown type).");
				return agent;
			}
			
			AID aid = new AID(this.networkData.getThisNode(), agentType);
			aid.setName(agentName);
			agent.setId(aid);

			agents.add(agent);
		} else {
			if(agent.getId().getType().getName() != agentType.getName()) {
				System.out.println("Agent already exists, but it's a different type.");
			}
			System.out.println("Found agent with name " + agent.getId().getName());
		}

		if (!getRunningAgents().contains(agent))
			runningAgents.add(agent);
		else
			System.out.println("The agent has already been run.");
		
		return agent;
	}
	

	@Lock(LockType.READ)
	public List<Agent> getAgents() {
		return agents;
	}

	@Override
	@Lock(LockType.WRITE)
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	@Override
	@Lock(LockType.READ)
	public List<AgentType> getAgentTypes() {
		return agentTypes;
	}

	@Override
	public boolean agentTypeExists(String name) {
		for (AgentType at : agentTypes) {
			if (at.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Lock(LockType.WRITE)
	public void setAgentTypes(List<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}

	@Override
	@Lock(LockType.READ)
	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	@Override
	@Lock(LockType.WRITE)
	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}

	@Override
	@Lock(LockType.READ)
	public List<ACLMessage> getAclMessages() {
		return aclMessages;
	}

	@Override
	@Lock(LockType.WRITE)
	public void setAclMessages(List<ACLMessage> aclMessages) {
		this.aclMessages = aclMessages;
	}

	@Override
	public Agent getAgentByName(String name) {
		for (Agent agent : agents) {
			if (agent.getId().getName().equals(name))
				return agent;
		}
		return null;
	}


	@Override
	public AID getAIDByIndex(int index) {
		AID agent = null;

		try {
			agent = agents.get(index).getId();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Agent with given index cannot be found.");
		}
		System.out.println("Found agent " + agent.getName());
		return agent;
	}
	@Override
	@Lock(LockType.READ)
	public Agent getAgent(AID id) {
		for (Agent agent : this.agents) {
			if (agent.getId().equals(id)) {
				return agent;
			}
		}
		return null;
	}

	@Override
	@Lock(LockType.READ)
	public AgentType getAgentType(String type) {
		System.out.println(type);
		for (AgentType agentType : agentTypes) {
			if (agentType.getName().equals(type)) {
				return agentType;
			}
		}
		return null;
	}

	@Override
	@Lock(LockType.WRITE)
	public void deleteAgent(Agent agent) {
		if (this.agents.contains(agent)) {
			this.agents.remove(agent);
		}
		// if there are no agents with that type anymore
		if (getAgentsByType(agent.getId().getType()).isEmpty())
			this.agentTypes.remove(agent.getId().getType());
	}

	@Override
	@Lock(LockType.READ)
	public ArrayList<String> getRunningAIDs() {
		ArrayList<String> aids = new ArrayList<>();
		for (Agent agent : Data.runningAgents) {
			aids.add(agent.getId().getName());
		}
		return aids;
	}
	
	
	@Override
	@Lock(LockType.READ)
	public ArrayList<Agent> getAgentsByType(AgentType agentType) {
		ArrayList<Agent> retVal = new ArrayList<>();
		for (Agent agent : agents) {
			if (agent.getId().getType().equals(agentType)) {
				retVal.add(agent);
			}
		}

		return retVal;
	}

	@Override
	public ArrayList<Agent> getRunningAgentsByType(AgentType agentType) {
		ArrayList<Agent> retVal = new ArrayList<>();
		for (Agent agent : runningAgents) {
			if (agent.getId().getType().equals(agentType)) {
				retVal.add(agent);
			}
		}

		return retVal;
	}
	
	public AgentType createAgentType(String type) {
		
		AgentType agentType = getAgentType(type);

		if (agentType == null) {
			System.out.println("Agent type not found, creating new one: " + type);
			agentType = new AgentType();
			agentType.setName(type);
			// default module
			if(type.equals("ping") || type.equals("pong")) {
				//test module for ping pong
				agentType.setModule("test-module");
			} else {
				//default module
				agentType.setModule("lol-module");
			}
			agentTypes.add(agentType);
		} else {
			System.out.println("Found type " + agentType.getName());
		}
		return agentType;
	}
	
	@Override
	public boolean stopRunningAgent(AID aid) {
		if (this.getRunningAIDs().contains(aid.getName())) {
			Data.runningAgents.remove(getAgent(aid));
			return true;
		}
		return false;
	}

	@Override
	public void stopRunningAgents() {
		Data.runningAgents.clear();
	}

}
