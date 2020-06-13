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

	public void lookupAgent(AID aid) {
		System.out.println("Iz lookupa " +aid.getName() + " - " + aid.getType().getName());
		
		
		if(this.data.getAgentTypes().contains(aid.getType())) {
			System.out.println("prva provjera");
			if(!this.data.getRunningAIDs().contains(aid)) {
				
				if(aid.getType().getModule().equals("test-module")) {
					
//					TestRemote testAgent = JNDILookup.lookUp(JNDILookup.TestLookup, TestRemote.class);
//					testAgent.setId(aid);
//					
//					this.data.getRunningAgents().add(testAgent);
					System.out.println("Started test-agent.");
					
				} else if(aid.getType().getModule().equals("lol-module")) {
					
					if(aid.getType().getName().equals("collector")) {
						
						CollectorRemote collector = JNDILookup.lookUp(JNDILookup.CollectorLookup, CollectorRemote.class);
						if(collector != null) {
							collector.setId(aid);
							
							this.data.getRunningAgents().add(collector);
							System.out.println("Started collector-agent.");
						} 
						else {
							System.out.println("collector is null");
						}
						
					} else if(aid.getType().getName().equals("predictor")){
						
						PredictorRemote predictor = JNDILookup.lookUp(JNDILookup.PredictorLookup, PredictorRemote.class);
						predictor.setId(aid);
						
						this.data.getRunningAgents().add(predictor);
						System.out.println("Started predictor-agent.");

					} else {
						System.out.println("Cannot start agent - unknown agent type.");
					}
					
				} else {
					System.out.println("Cannot start agent - unknown agent module.");

				}
			}			
		}
	}

	
}