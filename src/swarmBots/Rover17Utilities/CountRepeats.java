package swarmBots.Rover17Utilities;

import java.util.LinkedList;

public class CountRepeats {
	private LinkedList<Coordinates> countList;
	private int backwardCount;

	public CountRepeats() {
		this.countList = new LinkedList<Coordinates>();
	}

	public void isBackward(Coordinates t) {
		Coordinates tmpCoord;
		tmpCoord = countList.peek();
		if (!tmpCoord.equals(t)) {
			countList.add(t);
			if (backwardCount > 0)
				backwardCount--;
		} else {
			System.out.println("duplicate move");
			countList.pop();
			backwardCount++;
		}
	}
}
