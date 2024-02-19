package Repository;

import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.SETTINGS_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import Databaseconnection.Database;
import models.PayeenameDropdown;
import models.Settingsmodel;

public class SettingsRepo {
	
	private Connection connection;
	@SuppressWarnings("static-access")
	public SettingsRepo() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
	}
	
	public Object getloadSettingsdata(String queryType) {
		JSONObject responseMap = new JSONObject();
		try {
			responseMap.put(STATUS_CODE, SUCCESS_CODE);
			responseMap.put(STATUS_MESSAGE, SUCCESS);
			if(queryType.equals("payment")) {
				responseMap.put("paymentTypes",this.loadPaymentTypeOptions(queryType.toString()));
			}
			if(queryType.equals("instrument")) {
                responseMap.put("instrumentypes",this.loadPaymentTypeOptions(queryType.toString()));
            }
			if(queryType.equals("filter")) {
                responseMap.put("filters",this.loadFilterTypeOptions());
            }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseMap;
	}
	
	public Object loadBanknameslist(String countryCode) {
		Statement stmt;
		JSONObject responseMap = new JSONObject();
		List<Settingsmodel> settingsData = new ArrayList<>();
		try {
			stmt = connection.createStatement();
			String querySelect = " SELECT S.ID,"
					+ "S.NAME"
					+ " FROM "+SCHEMA_TABLE+"."+SETTINGS_TABLE+" S "
							+ "WHERE S.CATEGORY = 'bank' AND S.COMPANY_MASTER_CODE = '"+countryCode+"' ";
			ResultSet settingsInfo = stmt.executeQuery(querySelect);
			while(settingsInfo.next()) {
				Integer setId = settingsInfo.getInt("ID");
				String setName = settingsInfo.getString("NAME");
				settingsData.add(new Settingsmodel(setId, setName));
			}
			stmt.close();
			responseMap.put(STATUS_CODE, SUCCESS_CODE);
			responseMap.put(STATUS_MESSAGE, SUCCESS);
			responseMap.put("data",settingsData);
			return responseMap;
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return settingsData;
		}
	}
	
	private Object loadFilterTypeOptions() {
		Statement stmt;
		List<Settingsmodel> settingsData = new ArrayList<>();
		try {
			stmt = connection.createStatement();
			String querySelect = " SELECT S.ID ,S.NAME FROM "+SCHEMA_TABLE+"."+SETTINGS_TABLE+" S WHERE S.CATEGORY IN ('receiptsales','receiptdepo','payment','glpost') ";
			ResultSet settingsInfo = stmt.executeQuery(querySelect);
			while(settingsInfo.next()) {
				String setName = settingsInfo.getString("NAME");
				settingsData.add(new Settingsmodel(0, setName));
			}
			stmt.close();
			return settingsData;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return settingsData;
		}
	}
	
	public Object loadPaymentTypeOptions(String category) {
		Statement stmt;
		List<Settingsmodel> settingsData = new ArrayList<>();
		try {
			stmt = connection.createStatement();
			String querySelect = " SELECT S.ID ,S.NAME FROM "+SCHEMA_TABLE+"."+SETTINGS_TABLE+" S WHERE S.CATEGORY = '"+category+"' ";
			ResultSet settingsInfo = stmt.executeQuery(querySelect);
			while(settingsInfo.next()) {
				Integer setId = settingsInfo.getInt("ID");
				String setName = settingsInfo.getString("NAME");
				settingsData.add(new Settingsmodel(setId, setName));
			}
			stmt.close();
			return settingsData;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return settingsData;
		}
	}
	
	public Object getpayeenameDropdown(JsonObject data) {
		JSONObject responseMap = new JSONObject();
		try {
			String countrycode = data.get("countrycode").getAsString();
			String category = data.get("category").getAsString();
			String sqlquery = "SELECT "
								+ " DST.ID, "
								+ " DST.NAME "
								+ " FROM "+SCHEMA_TABLE+"."+SETTINGS_TABLE+" DST"
								+ " WHERE DST.COMPANY_MASTER_CODE = '"+countrycode+"' "
								+ "	AND DST.CATEGORY = '"+category+"'";
			Statement pst = connection.createStatement();
			ResultSet rs = pst.executeQuery(sqlquery);
			ArrayList<Object> list = new ArrayList<Object>();
			while(rs.next()) {
				String name = rs.getString("NAME");
				int id = rs.getInt("ID");
				list.add(new PayeenameDropdown(id,name));
			}
			responseMap.put("dropdown", list);
			responseMap.put(STATUS_CODE, SUCCESS_CODE);
			responseMap.put(STATUS_MESSAGE, SUCCESS);
			pst.close();
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseMap;
	}
}
