package swarmBots.Rover17Utilities;

public class CommObject {

	int x;
	int y;
	String terrain;// gravel, soild, rock,
	String science; // organic, radioactive, mineral
	boolean stillExists = true; // should be set to true when first seen by
								// sensors

	public CommObject() {
		super();
	}

	/**
	 * @param x
	 * @param y
	 * @param terrain
	 * @param science
	 * @param stillExists
	 */
	public CommObject(int x, int y, String terrain, String science, boolean stillExists) {
		super();
		this.x = x;
		this.y = y;
		this.terrain = terrain;
		this.science = science;
		this.stillExists = stillExists;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getTerrain() {
		return terrain;
	}

	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}

	public String getScience() {
		return science;
	}

	public void setScience(String science) {
		this.science = science;
	}

	public boolean isStillExists() {
		return stillExists;
	}

	public void setStillExists(boolean stillExists) {
		this.stillExists = stillExists;
	}

}
