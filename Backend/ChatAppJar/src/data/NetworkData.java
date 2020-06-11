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

import model.Host;

@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 120000)
public class NetworkData {
	
	private List<Host> nodes= new ArrayList<>();
	private Host master;
    private Host thisHost;
    
	public Host getThisHost() {
		return thisHost;
	}

	public void setThisHost(Host thisHost) {
		this.thisHost = thisHost;
	}
	private int counter=0;

	@Lock(LockType.READ)
	public Host getMaster() {
		return master;
	}

	@Lock(LockType.WRITE)
	public void setMaster(Host master) {
		this.master = master;
	}

	@Lock(LockType.READ)
	public List<Host> getNodes() {
		return nodes;
	}

	@Lock(LockType.WRITE)
	public void setNodes(List<Host> nodes) {
		this.nodes = nodes;
	}
	@Lock(LockType.READ)
	public int getCounter() {
		return counter++;
	}
	@Lock(LockType.WRITE)
	public void setCounter(int counter) {
		this.counter = counter;
	}

}
