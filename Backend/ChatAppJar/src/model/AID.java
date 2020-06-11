package model;

public class AID {

	private AgentCenter host;
	private AgentType type;

	public AID() {
		super();
	}

	public AID(AgentCenter host, AgentType type) {
		super();
		this.host = host;
		this.type = type;
	}

	public AgentCenter getHost() {
		return host;
	}

	public void setHost(AgentCenter host) {
		this.host = host;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}

}
