package model;

import java.io.Serializable;

import javax.ejb.Stateful;

public interface Agent extends Serializable{
	
	public AID getId();

	public void setId(AID id);
	
	public void handleMessage(ACLMessage message);

}
