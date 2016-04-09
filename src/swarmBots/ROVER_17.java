package swarmBots;

import common.ScanMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;

/**
 * Created by steveshim on 4/9/16.
 */
public class ROVER_17 {

    BufferedReader in;
    PrintWriter out;
    String rovername;
    ScanMap scanMap;
    int sleepTime;
    String SERVER_ADDRESS = "localhost";
    static final int PORT_ADDRESS = 9537;

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

    }

    public static void main(String args[]) throws Exception {
        ROVER_17 client = new ROVER_17();
        client.run();
    }
}
