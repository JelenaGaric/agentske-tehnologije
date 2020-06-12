package util;

import model.Agent;
import model.AgentType;

public class LookupHelper {

	public String agentLookup(AgentType agentType, boolean stateful) {
		if (agentType.getModule().contains("/")) {
			// in ear file
			if (stateful)
				return String.format("ejb:%s//%s!%s?stateful", agentType.getModule(), agentType.getName(),
						Agent.class.getName());
			else
				return String.format("ejb:%s//%s!%s", agentType.getModule(), agentType.getName(),
						Agent.class.getName());
		} else {
			// in jar file
			if (stateful)
				return String.format("ejb:/%s//%s!%s?stateful", agentType.getModule(), agentType.getName(),
						Agent.class.getName());
			else
				return String.format("ejb:/%s//%s!%s", agentType.getModule(), agentType.getName(),
						Agent.class.getName());
		}
	}

}
