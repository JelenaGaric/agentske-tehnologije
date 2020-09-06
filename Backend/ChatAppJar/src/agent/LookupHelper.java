package agent;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import data.Data;
import model.AID;
import model.Agent;
import util.JNDILookup;

@Stateless
@LocalBean
public class LookupHelper {	
	
	@EJB
	Data data; // data for agents and agent types

	public Agent lookupAgent(AID aid) {

		Agent agent = null;

		if(this.data.agentTypeExists(aid.getType().getName())) {
			System.out.println("AgentType exists " + aid.getName());

			if(!this.data.getRunningAIDs().contains(aid.getName())) {
				System.out.println("Agent with given aid has not been started - " + aid.getName());
			} else {
				System.out.println("Agent runs " + aid.getName());
				if(aid.getType().getModule().equals("test-module")) {
					if(aid.getType().getName().equals("ping")) {
						agent = JNDILookup.lookUp(JNDILookup.PingLookup, PingRemote.class);
						
					} else if(aid.getType().getName().equals("pong")) {
						agent = JNDILookup.lookUp(JNDILookup.PongLookup, PongRemote.class);
					}
					
					if(agent != null) {
						agent.setId(aid);
						System.out.println("Looked up for test agent.");
					} 
					else {
						System.out.println("Error. Agent is null");
					}
					
				} else if(aid.getType().getModule().equals("lol-module")) {
					
					if(aid.getType().getName().equals("collector")) {
						
						agent = JNDILookup.lookUp(JNDILookup.CollectorLookup, CollectorRemote.class);
						if(agent != null) {
							agent.setId(aid);
							
							System.out.println("Looked up for collector agent.");
						} 
						else {
							System.out.println("Error. Agent is null");
						}
						
					} else if(aid.getType().getName().equals("predictor")) {
						
						agent = JNDILookup.lookUp(JNDILookup.PredictorLookup, PredictorRemote.class);
						if(agent != null) {
							agent.setId(aid);
							
							System.out.println("Looked up for predictor agent.");
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