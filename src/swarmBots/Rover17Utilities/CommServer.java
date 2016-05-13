package swarmBots.Rover17Utilities;

import java.io.OutputStream;
import java.net.URL;

import java.net.HttpURLConnection;
import java.net.InetAddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommServer {

	private static String strServer = "23.251.155.186:3000";
	private static String USER_AGENT = "Mozilla/5.0";

	public static boolean postData(CommObject commObject) throws Exception {
		boolean isSuccess = false;

		String urlServer = strServer;
		URL objServer = new URL("http://" + urlServer + "/api/global");
		System.out.println(objServer.toString());
		//boolean isRechable= InetAddress.getByName(strServer).isReachable(3000);

		// Check if we have any kind of connection, else just return.
		if (true) {
			HttpURLConnection conServer = (HttpURLConnection) objServer.openConnection();

			// add request header
			conServer.setRequestMethod("POST");
			conServer.setRequestProperty("User-Agent", USER_AGENT);
			conServer.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			conServer.setRequestProperty("Content-Type", "application/json");

			Gson gson = new GsonBuilder().create();
			String strJson = gson.toJson(commObject);

			System.out.println(strJson);

			// Send post request
			conServer.setDoOutput(true);
			byte[] outputInBytes = strJson.getBytes("UTF-8");
			OutputStream os = conServer.getOutputStream();
			os.write(outputInBytes);
			os.close();

			// Get response code.
			int responseCode = conServer.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			// Check if success.
			if (responseCode == 200) {
				isSuccess = true;
			}

			// Disconnect the Connection.
			conServer.disconnect();
		}

		return isSuccess;
	}

}
