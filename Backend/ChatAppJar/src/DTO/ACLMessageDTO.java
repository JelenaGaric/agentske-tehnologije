package DTO;

import java.util.List;


public class ACLMessageDTO {
	private int senderIndex;
	private int[] receiverIndexes;
	private String content;
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
	
	
}
