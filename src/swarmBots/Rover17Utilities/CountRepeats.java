package swarmBots.Rover17Utilities;

import java.util.LinkedList;

public class CountRepeats {
	private LinkedList<Coordinates> countList;
	private int backwardCount;

	public CountRepeats() {
		this.countList = new LinkedList<Coordinates>();
	}

	public boolean isRepeating(Coordinates t) {
		boolean repeating = false;
		//peek the top of the stack and compare it with the new tile
		//if it is not equal correct move
		if (!t.equals(countList.peek())) {
			countList.add(t);
			if (backwardCount > 0)
				backwardCount--;
		} else {
			System.out.println("duplicate move");
			countList.pop();
			backwardCount++;
		}
		if (backwardCount >= 20)
			repeating = true;
		return repeating;
	}
}
