package data;

import java.util.List;

import javax.ejb.Local;

import model.AgentCenter;

@Local
public interface NetworkDataLocal {
	AgentCenter getThisNode();
	void setThisNode(AgentCenter thisNode);
	AgentCenter getNode(String alias);
	List<AgentCenter> getNodes();
	void setNodes(List<AgentCenter> nodes);
	boolean deleteNode(String alias);
	AgentCenter getMaster();
	void setMaster(AgentCenter master);
	int getCounter();
	void setCounter(int counter);
}
