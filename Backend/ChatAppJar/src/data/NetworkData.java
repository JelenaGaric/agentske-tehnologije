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

import model.AgentCenter;
import model.Host;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 120000)
public class NetworkData implements NetworkDataLocal{

	private List<AgentCenter> nodes = new ArrayList<>();
	private AgentCenter master;
	private AgentCenter thisNode;

	private int counter = 0;
	
	
	public NetworkData() {}
	
	@Override
	public AgentCenter getThisNode() {
		return thisNode;
	}
	@Override
	public void setThisNode(AgentCenter thisNode) {
		this.thisNode = thisNode;
	}
	@Override
	@Lock(LockType.READ)
	public AgentCenter getNode(String alias) {
		for(AgentCenter agentCenter : this.nodes) {
			if(alias.equals(agentCenter.getAlias())) {
				return agentCenter;
			}
		}
		return null;
	}
	@Override
	@Lock(LockType.READ)
	public List<AgentCenter> getNodes() {
		return nodes;
	}
	@Override
	@Lock(LockType.WRITE)
	public void setNodes(List<AgentCenter> nodes) {
		this.nodes = nodes;
	}
	@Override
	@Lock(LockType.WRITE)
	public boolean deleteNode(String alias) {
		AgentCenter toDelete = null;
		for(AgentCenter agentCenter : this.nodes) {
			if(agentCenter.getAlias().equals(alias)) {
				toDelete = agentCenter;
			}
		}
		if(toDelete != null) {
			this.nodes.remove(toDelete);
			return true;
		}
		return false;
	}
	@Override
	@Lock(LockType.READ)
	public AgentCenter getMaster() {
		return master;
	}
	@Override
	@Lock(LockType.WRITE)
	public void setMaster(AgentCenter master) {
		this.master = master;
	}
	@Override
	@Lock(LockType.READ)
	public int getCounter() {
		return counter;
	}
	@Override
	@Lock(LockType.WRITE)
	public void setCounter(int counter) {
		this.counter = counter;
	}

}
