package agent;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import data.Data;
import model.AID;
import model.Agent;
import model.AgentType;
import util.JNDILookup;

@Stateless
@LocalBean
public class LookupHelper {	
	
	@EJB
	Data data; // data for agents and agent types

	public Agent lookupAgent(AID aid) {
		
		Agent agent = null;

		if(this.data.agentTypeExists(aid.getType().getName())) {

			if(!this.data.getRunningAIDs().contains(aid)) {
				if(aid.getType().getModule().equals("test-module")) {
					
//					TestRemote testAgent = JNDILookup.lookUp(JNDILookup.TestLookup, TestRemote.class);
//					testAgent.setId(aid);
//					
//					this.data.getRunningAgents().add(testAgent);
					System.out.println("Started test-agent.");
					
				} else if(aid.getType().getModule().equals("lol-module")) {
					
					if(aid.getType().getName().equals("collector")) {
						
						agent = JNDILookup.lookUp(JNDILookup.CollectorLookup, CollectorRemote.class);
						if(agent != null) {
							agent.setId(aid);
							
							this.data.getRunningAgents().add(agent);
							System.out.println("Started agent.");
						} 
						else {
							System.out.println("Error. Agent is null");
						}
						
					} else {
						System.out.println("Cannot start agent - unknown agent type.");
					}
					
				} else {
					System.out.println("Cannot start agent - unknown agent module.");

				}
			}			
		}
		return agent;
	}

	
}