package swarmBots;

import common.Coord;
import common.MapTile;
import common.ScanMap;
import enums.Terrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Team_17 on 4/9/16.
 */
 
public class ROVER_17 {

	Rover17Map rm = new Rover17Map();
    BufferedReader in;
    PrintWriter out;
    String rovername;
    ScanMap scanMap;
    int sleepTime;
    String SERVER_ADDRESS = "localhost";
    static final int PORT_ADDRESS = 9537;
	private int xCoord, yCoord = 0;

    public ROVER_17(){
        System.out.println("ROVER_17 constructed.");
        rovername = "ROVER_17";
        SERVER_ADDRESS = "localhost";
        sleepTime = 300;

    }

    public ROVER_17(String servername){
        System.out.println("ROVER_17 constructed.");
        rovername = "ROVER_17";
        SERVER_ADDRESS = servername;
        sleepTime = 200;
    }

    public void run() throws IOException, InterruptedException{
    	// Make connection and initialize streams
		//TODO - need to close this socket
		Socket socket = new Socket(SERVER_ADDRESS, PORT_ADDRESS); // set port here
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		//Gson gson = new GsonBuilder().setPrettyPrinting().create();

		// Process all messages from server, wait until server requests Rover ID
		// name
		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				out.println(rovername); // This sets the name of this instance
										// of a swarmBot for identifying the
										// thread to the server
				break;
			}
		}

		// ******** Rover logic *********
		// int cnt=0;
		String line = "";

		boolean goingSouth = false;
		boolean stuck = false; // just means it did not change locations between requests,
								// could be velocity limit or obstruction etc.
		boolean blocked = false;
		String latMov = "W";
		int latCount = 0;


		Coord currentLoc = null;
		Coord previousLoc = null;

		// start Rover controller process
		while (true) {

			// currently the requirements allow sensor calls to be made with no
			// simulated resource cost


			// **** location call ****
			out.println("LOC");
			line = in.readLine();
			if (line == null) {
				System.out.println("ROVER_17 check connection to server");
				line = "";
			}
			if (line.startsWith("LOC")) {
				// loc = line.substring(4);
				currentLoc = extractLOC(line);
			}
			System.out.println("ROVER_17 currentLoc at start: " + currentLoc);
			Scanner inTest = new Scanner(currentLoc.toString()).useDelimiter("[^0-9]+");
			xCoord = inTest.nextInt();
			yCoord = inTest.nextInt();

			// after getting location set previous equal current to be able to check for stuckness and blocked later
			previousLoc = currentLoc;



			// **** get equipment listing ****
			ArrayList<String> equipment = new ArrayList<String>();
			equipment = getEquipment();
			//System.out.println("ROVER_17 equipment list results drive " + equipment.get(0));
			System.out.println("ROVER_17 equipment list results " + equipment + "\n");



			// ***** do a SCAN *****
			//System.out.println("ROVER_17 sending SCAN request");
			this.doScan();
			scanMap.debugPrintMap();

			// pull the MapTile array out of the ScanMap object
			MapTile[][] scanMapTiles = scanMap.getScanMap();
			int centerIndex = (scanMap.getEdgeSize() - 1)/2;
			// tile S = y + 1; N = y - 1; E = x + 1; W = x - 1

			rm.updateMap(xCoord, yCoord, scanMapTiles);
			//out.print("MOVE " + makeDecision(xCoord, yCoord, getPossibleMoves(scanMap)));

			// ***** MOVING *****
			// try moving east 5 block if blocked
			if (blocked) {
				for (int i = 0; i < 5; i++) {
					if ((isBlocked(scanMapTiles[centerIndex+1][centerIndex]) && latMov.equals("E")) ||
							(isBlocked(scanMapTiles[centerIndex-1][centerIndex])) && latMov.equals("W")){
						latCount++;
						System.out.println(latCount);
						break;
					}
					out.println("MOVE " + latMov);
					//System.out.println("ROVER_17 request move E");
					Thread.sleep(300);
				}
				blocked = false;
				//reverses direction after being blocked
				goingSouth = !goingSouth;
			} else {

				//Check if end of map width, if so, lateral movement will go in opposite direction.
				if (latCount >= 2) {
					if (latMov.equals("E")) {
						latMov = "W";
					} else if (latMov.equals("W")) {
						latMov = "E";
					}
					latCount = 0;
				}

				if (goingSouth) {
					// check scanMap to see if path is blocked to the south
					// (scanMap may be old data by now)
					if (isBlocked(scanMapTiles[centerIndex][centerIndex +1])) {
						blocked = true;
					} else {
						// request to server to move
						out.println("MOVE S");
						//System.out.println("ROVER_17 request move S");
					}

				} else {
					// check scanMap to see if path is blocked to the north
					// (scanMap may be old data by now)
					//System.out.println("ROVER_17 scanMapTiles[2][1].getHasRover() " + scanMapTiles[2][1].getHasRover());
					//System.out.println("ROVER_17 scanMapTiles[2][1].getTerrain() " + scanMapTiles[2][1].getTerrain().toString());

					if (isBlocked(scanMapTiles[centerIndex][centerIndex -1])) {
						blocked = true;
					} else {
						// request to server to move
						out.println("MOVE N");
						//System.out.println("ROVER_17 request move N");
					}
				}
			}

			// another call for current location
			out.println("LOC");
			line = in.readLine();
			if(line == null){
				System.out.println("ROVER_17 check connection to server");
				line = "";
			}
			if (line.startsWith("LOC")) {
				currentLoc = extractLOC(line);
			}

			//System.out.println("ROVER_17 currentLoc after recheck: " + currentLoc);
			//System.out.println("ROVER_17 previousLoc: " + previousLoc);

			// test for stuckness
			stuck = currentLoc.equals(previousLoc);

			//System.out.println("ROVER_17 stuck test " + stuck);
			System.out.println("ROVER_17 blocked test " + blocked);
			System.out.println(getPossibleMoves(scanMap));

			// TODO - logic to calculate where to move next



			Thread.sleep(sleepTime);

			System.out.println("ROVER_17 ------------ bottom process control --------------");
		}
    }

	// this takes the LOC response string, parses out the x and x values and
	// returns a Coord object
	public static Coord extractLOC(String sStr) {
		sStr = sStr.substring(4);
		if (sStr.lastIndexOf(" ") != -1) {
			String xStr = sStr.substring(0, sStr.lastIndexOf(" "));
			//System.out.println("extracted xStr " + xStr);

			String yStr = sStr.substring(sStr.lastIndexOf(" ") + 1);
			//System.out.println("extracted yStr " + yStr);
			return new Coord(Integer.parseInt(xStr), Integer.parseInt(yStr));
		}
		return null;
	}
	
	

	// method to retrieve a list of the rover's equipment from the server
	private ArrayList<String> getEquipment() throws IOException {
		//System.out.println("ROVER_17 method getEquipment()");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		out.println("EQUIPMENT");
		
		String jsonEqListIn = in.readLine(); //grabs the string that was returned first
		if(jsonEqListIn == null){
			jsonEqListIn = "";
		}
		StringBuilder jsonEqList = new StringBuilder();
		//System.out.println("ROVER_17 incomming EQUIPMENT result - first readline: " + jsonEqListIn);
		
		if(jsonEqListIn.startsWith("EQUIPMENT")){
			while (!(jsonEqListIn = in.readLine()).equals("EQUIPMENT_END")) {
				if(jsonEqListIn == null){
					break;
				}
				//System.out.println("ROVER_17 incomming EQUIPMENT result: " + jsonEqListIn);
				jsonEqList.append(jsonEqListIn);
				jsonEqList.append("\n");
				//System.out.println("ROVER_17 doScan() bottom of while");
			}
		} else {
			// in case the server call gives unexpected results
			clearReadLineBuffer();
			return null; // server response did not start with "EQUIPMENT"
		}
		
		String jsonEqListString = jsonEqList.toString();		
		ArrayList<String> returnList;		
		returnList = gson.fromJson(jsonEqListString, new TypeToken<ArrayList<String>>(){}.getType());		
		//System.out.println("ROVER_17 returnList " + returnList);
		
		return returnList;
	}
	

	// sends a SCAN request to the server and puts the result in the scanMap array
	public void doScan() throws IOException {
		//System.out.println("ROVER_17 method doScan()");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		out.println("SCAN");

		String jsonScanMapIn = in.readLine(); //grabs the string that was returned first
		if(jsonScanMapIn == null){
			System.out.println("ROVER_17 check connection to server");
			jsonScanMapIn = "";
		}
		StringBuilder jsonScanMap = new StringBuilder();
		System.out.println("ROVER_17 incomming SCAN result - first readline: " + jsonScanMapIn);
		
		if(jsonScanMapIn.startsWith("SCAN")){	
			while (!(jsonScanMapIn = in.readLine()).equals("SCAN_END")) {
				//System.out.println("ROVER_17 incomming SCAN result: " + jsonScanMapIn);
				jsonScanMap.append(jsonScanMapIn);
				jsonScanMap.append("\n");
				//System.out.println("ROVER_17 doScan() bottom of while");
			}
		} else {
			// in case the server call gives unexpected results
			clearReadLineBuffer();
			return; // server response did not start with "SCAN"
		}
		//System.out.println("ROVER_17 finished scan while");

		String jsonScanMapString = jsonScanMap.toString();
		// debug print json object to a file
		//new MyWriter( jsonScanMapString, 0);  //gives a strange result - prints the \n instead of newline character in the file

		//System.out.println("ROVER_17 convert from json back to ScanMap class");
		// convert from the json string back to a ScanMap object
		scanMap = gson.fromJson(jsonScanMapString, ScanMap.class);		
	}
	
	
	private void clearReadLineBuffer() throws IOException{
		while(in.ready()){
			//System.out.println("ROVER_17 clearing readLine()");
			String garbage = in.readLine();	
		}
	}

	//Helper method to make cleaner run() cleaner
	private boolean isBlocked(MapTile mt){
		if (mt.getHasRover() || mt.getTerrain() == Terrain.ROCK ||
				mt.getTerrain() == Terrain.NONE || mt.getTerrain() == Terrain.SAND){
			return true;
		} else {
			return false;
		}
	}

	//get arraylist of possible moves from current position
	private ArrayList<String> getPossibleMoves(ScanMap sm){
		ArrayList<String> possibleMoves = new ArrayList();
		MapTile[][] view = sm.getScanMap();
		int center = (sm.getEdgeSize() - 1)/2;
		if (!isBlocked(view[center+1][center])){
			possibleMoves.add("E");
		}
		if (!isBlocked(view[center-1][center])){
			possibleMoves.add("W");
		}
		if (!isBlocked(view[center][center-1])){
			possibleMoves.add("N");
		}
		if (!isBlocked(view[center][center+1])){
			possibleMoves.add("S");
		}
		return possibleMoves;
	}

    public static void main(String args[]) throws Exception {
        ROVER_17 client = new ROVER_17();
        client.run();
    }
}
