package Repository;

import static Constants.AsianConstants.FCMTOKENAPI;
import static Constants.AsianConstants.FCMTOKENID;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Pushnotificationrepo {
		
	public Object send(Object tokensList) {
		URL url;
		try {
			url = new URL(FCMTOKENAPI);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Authorization", "key="+FCMTOKENID);
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setDoOutput(true);
			OutputStream os = httpURLConnection.getOutputStream();
			os.write(tokensList.toString().getBytes());
			os.flush();
			os.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            JsonObject data = new Gson().fromJson(response.toString(), JsonObject.class);
            return data.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage().toString();
		}
	}
}
