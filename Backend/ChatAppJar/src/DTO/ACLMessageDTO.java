package DTO;

import java.util.List;

import model.Performative;


public class ACLMessageDTO {
	private int senderIndex;
	private int[] receiverIndexes;
	private String content;
	private Performative performative;
	private String session;
	
	public int getSenderIndex() {
		return senderIndex;
	}
	public void setSenderIndex(int senderIndex) {
		this.senderIndex = senderIndex;
	}
	
	public int[] getReceiverIndexes() {
		return receiverIndexes;
	}
	public void setReceiverIndexes(int[] receiverIndexes) {
		this.receiverIndexes = receiverIndexes;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ACLMessageDTO() {
		super();
	}
	public Performative getPerformative() {
		return performative;
	}
	public void setPerformative(Performative performative) {
		this.performative = performative;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	
}
