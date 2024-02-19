package Repository;

import static Constants.AsianConstants.DEALER_MOBILE;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.Accountverifiaction;

public class RepositoryAccountVerification implements Accountverifiaction {
	
	private Connection connection;
	@SuppressWarnings("static-access")
	public RepositoryAccountVerification() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
	}

	@Override
	public Object getCheckAccountbyDealerId(JsonObject accountData) {
		// TODO Auto-generated method stub
		String dealerId = accountData.get("dealerid").getAsString();
		String countrycode = accountData.get("countrycode").getAsString();
		String dealerAccountNumber = accountData.get("accountnumber").getAsString();
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.DEALER_ID,L.DEALER_NAME,L.DEALER_EMAIL,L.DEALER_BANK_BRANCH,L.DEALER_BANK_NAME "
					+ "FROM " + SCHEMA_TABLE + " ." + DL_DEALER_TABLE + " L "
							+ "WHERE ";
								if(!dealerId.isEmpty()) {
									querySelect += " L.DEALER_ID = '" + dealerId + "' AND ";
								}
								if(!countrycode.isEmpty()) {
									querySelect += " L.DEALER_COMPANY_CODE = '" + countrycode + "' AND ";
								}
								querySelect += " L.DEALER_BANK_ACCOUNT_NUMBER = '" + dealerAccountNumber + "'"
									+ "ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			JSONObject httpResponsuccess = new JSONObject();
			if(resultSet.next()) {
				ArrayList<String> mobileList = this.getDealerMobileInfoData(resultSet.getString("DEALER_ID").toString());
                String dealersId = resultSet.getString("DEALER_ID") != null ? resultSet.getString("DEALER_ID").toString() : "";
                String dealerName = resultSet.getString("DEALER_NAME") != null ? resultSet.getString("DEALER_NAME").toString() : "";
                String dealerEmail = resultSet.getString("DEALER_EMAIL") != null ? resultSet.getString("DEALER_EMAIL").toString() : "";
                String dealerbankBranch = (resultSet.getString("DEALER_BANK_BRANCH") != null ? resultSet.getString("DEALER_BANK_BRANCH").toString() : "");
                String dealerBankName = (resultSet.getString("DEALER_BANK_NAME") != null ? resultSet.getString("DEALER_BANK_NAME").toString() : "");
				httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
				httpResponsuccess.put(STATUS_MESSAGE, SUCCESS);
				httpResponsuccess.put("dealerName", dealerName);
				httpResponsuccess.put("dealerEmail", dealerEmail);
				httpResponsuccess.put("dealerCode", dealersId);
                httpResponsuccess.put("dealerBankName", dealerBankName);
                httpResponsuccess.put("dealerBankBranch", dealerbankBranch);
                httpResponsuccess.put("dealerMobile", mobileList);
			} else {
				httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
				httpResponsuccess.put(STATUS_MESSAGE, "Account Information not matched!.");
			}
			stmt.close();
			return httpResponsuccess;
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<String> getDealerMobileInfoData(String dealerId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.DEALER_MOBILE "
					+ "FROM " + SCHEMA_TABLE + " ." + DEALER_MOBILE + " L "
							+ "WHERE L.DEALER_ID = '"+dealerId+"'";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			ArrayList<String> mobileList =new ArrayList<String>();//Creating arraylist    
			while( resultSet.next() ) {
				mobileList.add(resultSet.getString("DEALER_MOBILE"));
			}
			stmt.close();
			return mobileList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
