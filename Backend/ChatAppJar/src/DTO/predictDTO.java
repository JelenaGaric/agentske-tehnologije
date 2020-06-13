package DTO;

public class predictDTO {

	private int redGold;
	private int blueGold;
	private int redKills;
	private int blueKills;
	private int redDragons;
	private int blueDragons;
	private int redMidLaneStructures;
	private int redBotLaneStructures;
	private int redTopLaneStructures;
	private int blueTopLaneStructures;
	private int blueMidLaneStructures;
	private int blueBotLaneStructures;

	public predictDTO() {
		super();
	}

	public predictDTO(int redGold, int blueGold, int redKills, int blueKills, int redDragons, int blueDragons,
			int redMidLaneStructures, int redBotLaneStructures, int redTopLaneStructures, int blueTopLaneStructures,
			int blueMidLaneStructures, int blueBotLaneStructures) {
		super();
		this.redGold = redGold;
		this.blueGold = blueGold;
		this.redKills = redKills;
		this.blueKills = blueKills;
		this.redDragons = redDragons;
		this.blueDragons = blueDragons;
		this.redMidLaneStructures = redMidLaneStructures;
		this.redBotLaneStructures = redBotLaneStructures;
		this.redTopLaneStructures = redTopLaneStructures;
		this.blueTopLaneStructures = blueTopLaneStructures;
		this.blueMidLaneStructures = blueMidLaneStructures;
		this.blueBotLaneStructures = blueBotLaneStructures;
	}

	public int getRedGold() {
		return redGold;
	}

	public void setRedGold(int redGold) {
		this.redGold = redGold;
	}

	public int getBlueGold() {
		return blueGold;
	}

	public void setBlueGold(int blueGold) {
		this.blueGold = blueGold;
	}

	public int getRedKills() {
		return redKills;
	}

	public void setRedKills(int redKills) {
		this.redKills = redKills;
	}

	public int getBlueKills() {
		return blueKills;
	}

	public void setBlueKills(int blueKills) {
		this.blueKills = blueKills;
	}

	public int getRedDragons() {
		return redDragons;
	}

	public void setRedDragons(int redDragons) {
		this.redDragons = redDragons;
	}

	public int getBlueDragons() {
		return blueDragons;
	}

	public void setBlueDragons(int blueDragons) {
		this.blueDragons = blueDragons;
	}

	public int getRedMidLaneStructures() {
		return redMidLaneStructures;
	}

	public void setRedMidLaneStructures(int redMidLaneStructures) {
		this.redMidLaneStructures = redMidLaneStructures;
	}

	public int getRedBotLaneStructures() {
		return redBotLaneStructures;
	}

	public void setRedBotLaneStructures(int redBotLaneStructures) {
		this.redBotLaneStructures = redBotLaneStructures;
	}

	public int getRedTopLaneStructures() {
		return redTopLaneStructures;
	}

	public void setRedTopLaneStructures(int redTopLaneStructures) {
		this.redTopLaneStructures = redTopLaneStructures;
	}

	public int getBlueTopLaneStructures() {
		return blueTopLaneStructures;
	}

	public void setBlueTopLaneStructures(int blueTopLaneStructures) {
		this.blueTopLaneStructures = blueTopLaneStructures;
	}

	public int getBlueMidLaneStructures() {
		return blueMidLaneStructures;
	}

	public void setBlueMidLaneStructures(int blueMidLaneStructures) {
		this.blueMidLaneStructures = blueMidLaneStructures;
	}

	public int getBlueBotLaneStructures() {
		return blueBotLaneStructures;
	}

	public void setBlueBotLaneStructures(int blueBotLaneStructures) {
		this.blueBotLaneStructures = blueBotLaneStructures;
	}

}
