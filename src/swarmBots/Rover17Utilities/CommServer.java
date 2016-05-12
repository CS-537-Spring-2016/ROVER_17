package swarmBots.Rover17Utilities;

import java.io.OutputStream;
import java.net.URL;

import java.net.HttpURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommServer {

	private static String strServer = "192.168.1.141";
	private static String strLocal = "localhost";
	private static String strPort = "3000";
	private static String USER_AGENT = "Mozilla/5.0";

	public static boolean postData(CommObject commObject) throws Exception {
		boolean isSuccess = false;

		String urlServer = strLocal + ":" + strPort;
		URL objServer = new URL("http://" + urlServer + "/scout");
		System.out.println(objServer.toString());

		HttpURLConnection conServer = (HttpURLConnection) objServer.openConnection();

		// Check if we have any kind of connection, else just return.
		if (conServer != null) {
			// add reuqest header
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

			int responseCode = conServer.getResponseCode();
			System.out.println("Response Code : " + responseCode);

			if (responseCode == 200) {
				isSuccess = true;
			}
			
			conServer.disconnect();
		}

		return isSuccess;
	}

}
