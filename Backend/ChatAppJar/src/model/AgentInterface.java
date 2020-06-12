package model;

import java.io.Serializable;

public interface AgentInterface extends Serializable {

		public void handleMessage(ACLMessage message);


}
