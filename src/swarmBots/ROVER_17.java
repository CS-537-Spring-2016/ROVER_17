package swarmBots;

import common.Coord;
import common.MapTile;
import common.ScanMap;
import enums.Terrain;
import common.Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import swarmBots.Rover17Utilities.Coordinates;
import swarmBots.Rover17Utilities.Edge;
import swarmBots.Rover17Utilities.Node;
import swarmBots.Rover17Utilities.Rover17Map;

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
	private int xCoord=0, yCoord=0, counter=0;
	private String url = "http://23.251.155.186:3000/api/global";
	private Communication com = new Communication(url);
	private ArrayList<String> moves = new ArrayList();
	private String direction;

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
		Socket socket = null;
		try {
			socket = new Socket(SERVER_ADDRESS, PORT_ADDRESS); // set port here
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

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
			String line = "";


			Coord currentLoc = null;
			Coord previousLoc = null;

			String prevMove = "";

			// start Rover controller process
			while (true) {


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
				Scanner inTest = new Scanner(currentLoc.toString()).useDelimiter("[^0-9]+");
				xCoord = inTest.nextInt();
				yCoord = inTest.nextInt();


				// **** get equipment listing ****
				ArrayList<String> equipment = new ArrayList<String>();
				equipment = getEquipment();


				// ***** do a SCAN *****
				this.doScan();
				//scanMap.debugPrintMap();

				// pull the MapTile array out of the ScanMap object
				MapTile[][] scanMapTiles = scanMap.getScanMap();
				int centerIndex = (scanMap.getEdgeSize() - 1) / 2;

				rm.updateMap(xCoord, yCoord, scanMapTiles);

				if (scanMapTiles[centerIndex+5][centerIndex].getTerrain().equals(Terrain.NONE) ||
						scanMapTiles[centerIndex][centerIndex+5].getTerrain().equals(Terrain.NONE)) {
					switch (prevMove) {
						case "S":
							if (yCoord + 5 < rm.getMapHeight()) {
								rm.setMapHeight(yCoord + 5);
								System.out.println("Height = " + rm.getMapHeight());
							}
							break;
						case "E":
							if (xCoord + 5 < rm.getMapWidth()) {
								rm.setMapWidth(xCoord + 5);
								System.out.println("Width = " + rm.getMapWidth());
							}
							break;
					}
				}


				if (counter < 1){
					direction = rm.directionOfUndiscovered(xCoord, yCoord);
					LinkedList<Edge> path = new LinkedList();
					switch (direction){
						case "NE":
							path = rm.getTargetNE(xCoord, yCoord, scanMapTiles);
							break;
						case "NW":
							path = rm.getTargetNW(xCoord, yCoord, scanMapTiles);
							break;
						case "SW":
							path = rm.getTargetSW(xCoord, yCoord, scanMapTiles);
							break;
						case "SE":
							path = rm.getTargetSE(xCoord, yCoord, scanMapTiles);
							break;
					}
					moves = rm.getMoves(path);
					counter = moves.size();
					System.out.println(moves);
				}
				if(moves.size() != 0) {
					String direction = moves.get(moves.size() - counter);
					switch (direction){
						case "W":
							if (isBlocked(scanMapTiles[centerIndex-1][centerIndex])) {
								counter = 0;
							}
							break;
						case "E":
							if (isBlocked(scanMapTiles[centerIndex+1][centerIndex])) {
								counter = 0;
							}
							break;
						case "S":
							if (isBlocked(scanMapTiles[centerIndex][centerIndex+1])){
								counter = 0;
							}
							break;
						case "N":
							if (isBlocked(scanMapTiles[centerIndex][centerIndex-1])){
								counter = 0;
							}
							break;
					}
					Thread.sleep(300);
					out.println("MOVE " + direction);
					prevMove = direction;
					counter--;
				}


				// another call for current location
				out.println("LOC");
				line = in.readLine();
				if (line == null) {
					System.out.println("ROVER_17 check connection to server");
					line = "";
				}
				if (line.startsWith("LOC")) {
					currentLoc = extractLOC(line);
				}


				//print to see nodes
				//System.out.println(rm);
				//print to see edges
				//System.out.println(rm.getGraph().edgesToString());
				//print to see 2darray
				//System.out.println(rm.arrayToString());

				com.postScanMapTiles(currentLoc, scanMapTiles);

				Thread.sleep(sleepTime);

				System.out.println("ROVER_17 ------------ bottom process control --------------");
			}
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println("ROVER_17 problem closing socket");
				}
			}
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
		//System.out.println("ROVER_17 incomming SCAN result - first readline: " + jsonScanMapIn);
		
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
		

		String jsonScanMapString = jsonScanMap.toString();
		// debug print json object to a file
		//new MyWriter( jsonScanMapString, 0);  //gives a strange result - prints the \n instead of newline character in the file

		
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



    public static void main(String args[]) throws Exception {
        ROVER_17 client;

		if(!(args.length == 0)){
			client = new ROVER_17(args[0]);
		} else {
			client = new ROVER_17();
		}

        client.run();
    }
}
