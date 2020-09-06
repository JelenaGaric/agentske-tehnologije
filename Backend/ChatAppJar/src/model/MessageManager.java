package model;

import javax.ejb.Remote;

@Remote
public interface MessageManager {

	boolean sendMessage(ACLMessage message);

	String ping();
}