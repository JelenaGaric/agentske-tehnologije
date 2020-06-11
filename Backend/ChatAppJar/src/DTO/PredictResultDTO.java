package DTO;

public class PredictResultDTO {
	private double certainty;
	private boolean result;
	
	public PredictResultDTO() {
		super();
	}
	public double getCertainty() {
		return certainty;
	}
	public void setCertainty(double certainty) {
		this.certainty = certainty;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
}
