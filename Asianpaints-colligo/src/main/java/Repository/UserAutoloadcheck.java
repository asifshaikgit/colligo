package Repository;

import com.google.gson.JsonObject;

import Databaseconnection.Database;

import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.DL_APP_USER_INFO;
//import static Constants.AsianConstants.SETTINGS_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;
import static Constants.AsianConstants.VERSION_MAPPYING;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import Services.Autoload;

public class UserAutoloadcheck implements Autoload {
	
	private Connection connection;	
	@SuppressWarnings("static-access")
	public UserAutoloadcheck() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
	}
	
	private Boolean getDeviceVersion(String appversion) {
		Statement stmt;
		String appVerionNumber = "";
		try {
			stmt = connection.createStatement();
			String settingSql = "SELECT VERSION FROM "+SCHEMA_TABLE+"."+VERSION_MAPPYING+" U ORDER BY U.ID DESC LIMIT 1";
			ResultSet resultSet = stmt.executeQuery(settingSql);
			resultSet.next();
			appVerionNumber = resultSet.getString("VERSION").toString();
			System.err.println(appVerionNumber);
			if(!appversion.toString().equals(appVerionNumber.toString())) {
//				return true;
			}
			stmt.close();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Object CheckuserinfoAutoload(JsonObject data) {
		// TODO Auto-generated method stub
		String userid = data.get("userid").getAsString();
		String logintoken = data.get("logintoken").getAsString();
		String appversion = data.get("appversion").getAsString();
		Statement stmt;
		JSONObject responseMap = new JSONObject();
		try {
			stmt = connection.createStatement();
			String userSql = "SELECT * FROM "+SCHEMA_TABLE+"."+DL_APP_USER_INFO+" U WHERE U.USERID = '"+userid+"' AND U.USER_LOGIN_TOKEN = '"+logintoken+"'";
			ResultSet resultSet = stmt.executeQuery(userSql);
			if(resultSet.next()) {
				responseMap.put("login", true);
			} else {
				responseMap.put("login", false);
			}
			stmt.close();
			responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
			responseMap.put("userid", userid.toString());
			responseMap.put("version", this.getDeviceVersion(appversion));
			return responseMap;
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
