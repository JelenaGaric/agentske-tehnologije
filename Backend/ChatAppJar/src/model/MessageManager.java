package model;

public interface MessageManager {

	boolean sendMessage(ACLMessage message);

	String ping();
}