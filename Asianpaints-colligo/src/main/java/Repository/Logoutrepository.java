package Repository;

import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.logout;

public class Logoutrepository implements logout {
	
	@Override
	public Object Userlogout(JsonObject data) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseMap = new JSONObject();
			String userid = data.get("userid").getAsString();
			Boolean islogged = false;
			String updateQry = "UPDATE " + SCHEMA_TABLE + "." + DL_APP_USER_INFO + " O "
					+ "SET"
					+ " O.IS_LOGGED = " +islogged+ ", USER_PUSH_TOKEN = '' WHERE "
					+ "O.USERID = '" +userid.toString()+ "' ";
			PreparedStatement updateQryPrepare;
			updateQryPrepare = Database.Connection().prepareStatement(updateQry);
			updateQryPrepare.execute();
			updateQryPrepare.close();
			responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_FLAG, 0);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
            return responseMap;
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
