package model;

import javax.ejb.Stateful;

@Stateful
public class Agent {

	private AID id;

	private void handleMessage(ACLMessage message) {
	}

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

	public Agent() {
		super();
	}

	public Agent(AID id) {
		super();
		this.id = id;
	}

}
