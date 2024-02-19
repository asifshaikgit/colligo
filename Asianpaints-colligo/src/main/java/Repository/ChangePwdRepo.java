package Repository;

import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS_CODE;
import static Constants.AsianConstants.FORGOTPWDURL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Services.ChangePwdService;

public class ChangePwdRepo implements ChangePwdService {

	@Override
	public Object updateThePwd(JsonObject data) {
		// TODO Auto-generated method stub
		try {
			String userid = data.get("userid").getAsString();
			String password = data.get("password").getAsString();
			URL url = new URL(FORGOTPWDURL);
			final String DUBAIE_MESSAGE = "{"
					+ "    \"username\": \""+userid+"\","
					+ "    \"newPassword\": \""+password+"\","
					+ "    \"confirmPassword\": \""+password+"\","
					+ "    \"type\": \"forgot\","
					+ "    \"adminUsername\": \"P00996738\","
					+ "    \"adminPassword\": \"@4ET4Jmx\","
					+ "    \"organizationalUnit\": ["
					+ "        \"Offroll\","
					+ "        \"IBU\""
					+ "    ],"
					+ "    \"domainController\": ["
					+ "        \"asianpaints\","
					+ "        \"com\""
					+ "    ]"
					+ "}";
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	 	    httpURLConnection.setRequestMethod("PUT");
	 	    httpURLConnection.setDoOutput(true);
			OutputStream os1 = httpURLConnection.getOutputStream();
			os1.write(DUBAIE_MESSAGE.getBytes());
			os1.flush();
			os1.close();
			JSONObject httpResponsuccess = new JSONObject();
			int responseCode1 = httpURLConnection.getResponseCode();
			if (responseCode1 == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in .readLine()) != null) {
	                response.append(inputLine);
	            } in .close();
	            JsonObject datas = new Gson().fromJson(response.toString(), JsonObject.class);
				if(datas.get("status").getAsString().equals("true")) {
					httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
					httpResponsuccess.put(STATUS_MESSAGE, "Password has been changed successfully.");
				} else {
					httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
					httpResponsuccess.put(STATUS_MESSAGE, "Something went wrong!, Password not changed.");
				}
			} else {
				httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
				httpResponsuccess.put(STATUS_MESSAGE, "Something went wrong!, Please try again");
			}
			return httpResponsuccess;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}
