package data;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentType;

@Local
public interface DataLocal {
	List<Agent> getAgents();
	void setAgents(List<Agent> agents);
	List<AgentType> getAgentTypes();
	void setAgentTypes(List<AgentType> agentTypes);
	List<Agent> getRunningAgents();
	void setRunningAgents(List<Agent> runningAgents);
	List<ACLMessage> getAclMessages();
	void setAclMessages(List<ACLMessage> aclMessages);
	Agent getAgentByName(String name);
	AID getAIDByIndex(int index);
	Agent getAgent(AID id);
	AgentType getAgentType(String type);
	void deleteAgent(Agent agent);
	ArrayList<AID> getRunningAIDs();
	ArrayList<Agent> getAgentsByType(AgentType agentType);
	ArrayList<Agent> getRunningAgentsByType(AgentType agentType);
	void stopRunningAgent(AID aid);
	void stopRunningAgents();
	boolean agentTypeExists(String name);
}
