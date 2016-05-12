package testUtillities;

import swarmBots.Rover17Utilities.CommObject;
import swarmBots.Rover17Utilities.CommServer;

public class CommTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CommObject obj = new CommObject();
		obj.setX(50);
		obj.setY(50);
		obj.setTerrain("sand");
		obj.setScience("mineral");
		
		CommServer.postData(obj);
	}
}
