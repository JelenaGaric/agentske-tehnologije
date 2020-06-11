package model;

public enum Performative {
	
	acceptProposal, // The action of accepting a previously submitted propose to perform an action.
	
	agree, // The action of agreeing to perform a requestd action made by another agent.
			// Agent will carry it out.
	cancel, // Agent wants to cancel a previous request.
	
	cfp, // Agent issues a call for proposals. It contains the actions to be carried out
			// and any other terms of the agreement.
	confirm, // The sender confirms to the receiver the truth of the content. The sender
				// initially believed that the receiver was unsure about it.
	disconfirm, // The sender confirms to the receiver the falsity of the content.
	
	failure, // Tell the other agent that a previously requested action failed.
	
	inform, // Tell another agent something. The sender must believe in the truth of the
			// statement. Most used performative.
	informiIf, // Used as content of request to ask another agent to tell us is a statement is
				// true or false.
	informRef, // Like inform-if but asks for the value of the expression.
	
	notUnderstood, // Sent when the agent did not understand the message.
	
	propagate, // Asks another agent so forward this same propagate message to others.
	
	propose, // Used as a response to a cfp. Agent proposes a deal.
	
	proxy, // The sender wants the receiver to select target agents denoted by a given
			// description and to send an embedded message to them.
	queryIf, // The action of asking another agent whether or not a given proposition is
				// true.
	queryRef, // The action of asking another agent for the object referred to by an
				// referential expression.
	refuse, // The action of refusing to perform a given action, and explaining the reason
			// for the refusal.
	rejectProposal, // The action of rejecting a proposal to perform some action during a
					// negotiation.
	request, // The sender requests the receiver to perform some action. Usually to request
				// the receiver to perform another communicative act.
	requestWhen, // The sender wants the receiver to perform some action when some given
					// proposition becomes true.
	requestWhenever, // The sender wants the receiver to perform some action as soon as some
						// proposition becomes true and thereafter each time the proposition becomes
						// true again.
	subscribe// The act of requesting a persistent intention to notify the sender of the
				// value of a reference, and to notify again whenever the object identified by
				// the reference changes.

}
