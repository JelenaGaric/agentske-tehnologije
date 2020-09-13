package util;

import java.io.Serializable;
import java.util.HashMap;

import model.AID;

public class ContractNetTender implements Serializable {
	
	public ContractNetTender() {
		super();
	}
	
	private HashMap<AID, Integer> offers = new HashMap<>();

	public HashMap<AID, Integer> getOffers() {
		return offers;
	}

	public void setOffers(HashMap<AID, Integer> offers) {
		this.offers = offers;
	}

}
