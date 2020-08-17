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

		AID aid = new AID();
		aid.setHost(new AgentCenter("alijas", "adresa"));
		aid.setName("aid ime");
		aid.setType(agentType);
		// Agent agent = new Agent();
		// agent.setId(aid);

		// runningAgents.add(agent);

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
			System.out.println("baza- " + agentType.getName());
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
	public ArrayList<AID> getRunningAIDs() {
		ArrayList<AID> aids = new ArrayList<>();
		for (Agent agent : this.agents) {
			aids.add(agent.getId());
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

	@Override
	public void stopRunningAgent(AID aid) {
		if (this.getRunningAIDs().contains(aid))
			this.runningAgents.remove(getAgent(aid));
	}

	@Override
	public void stopRunningAgents() {
		this.runningAgents.clear();
	}

}
