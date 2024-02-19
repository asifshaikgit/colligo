package Repository;

import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DEALER_MOBILE;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.SALES_MAP;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;
import static Constants.AsianConstants.LOGS_TABLE;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import Databaseconnection.Database;
import Utilres.Jsonresponse;
import models.Delarmodel;

public class Dealerrepository {
	
	private Jsonresponse validationApi;
	private Connection connection;
	Random random;
	
	@SuppressWarnings("static-access")
	public Dealerrepository() {
		// TODO Auto-generated constructor stub
		validationApi = new Jsonresponse();
		this.connection = new Database().Connection();
		random = new Random(); 
	}
	
	public Object getDealerInformation(String userid, String countryId, String delarname, String isCountry){
		
		try {
		    Statement stmt;
			List<Delarmodel> delarlist = new ArrayList<>();
			ResultSet countryActiveInfo = this.getByCountryCodeData(countryId);
			if(countryActiveInfo.next()) {
			    String activeName;
	            if(countryActiveInfo.getBoolean("COUNTRY_OTP_STATUS") == true) {
	                activeName = "Active";
	            } else {
	                activeName = "In-Active";
	            }
	            stmt = connection.createStatement();
	            String querySelect = "SELECT *,(SELECT STRING_AGG(M.DEALER_MOBILE,',') FROM "+SCHEMA_TABLE+"."+DEALER_MOBILE+" M WHERE M.DEALER_ID = DEALER_CODE) AS MOBILE FROM ( SELECT "
	            		+ "L.DEALER_ID AS DEALER_CODE,"
	            		+ "L.DEALER_NAME,"
	            		+ "L.DEALER_EMAIL,"
	            		+ "L.DEALER_BANK_BRANCH,"
	            		+ "L.DEALER_BANK_NAME,"
	            		+ "L.DEALER_CITY FROM " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " L "
	            				+ "INNER JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " K "
	            						+ "ON L.DEALER_COMPANY_CODE = K.COMPANY_CODE WHERE ";
	            
	            if(delarname.length() > 0 && isCountry.equals("1")) {
	                querySelect += " K.COMPANY_CODE = '"+countryId+"' ";
	            } else if(delarname.length() > 0 && isCountry.equals("0")) {
	                querySelect += " K.COMPANY_CODE = '"+countryId+"' ";
	            } else if(delarname.length() == 0 && isCountry.equals("1")) {
	                querySelect += " K.COMPANY_CODE = '"+countryId+"' ";
	                querySelect += " OR L.DEALER_ID IN ("
	                		+ "SELECT ddsm.DEALERID FROM " + SCHEMA_TABLE + "." + SALES_MAP + " ddsm "
	                				+ "WHERE ddsm.USERID = '"+userid+"' AND ddsm.COUNTRY_MASTER_CODE = '"+countryId+"') ";
	            } else {
	                querySelect += " L.DEALER_ID IN ("
	                		+ "SELECT ddsm.DEALERID FROM " + SCHEMA_TABLE + "." + SALES_MAP + " ddsm "
	                				+ "WHERE ddsm.USERID = '"+userid+"' AND ddsm.COUNTRY_MASTER_CODE = '"+countryId+"') ";
	            }
	            
	            if( delarname != "" && delarname.length() > 3) {
	                querySelect += " AND (((L.DEALER_NAME LIKE '%" + delarname.toLowerCase().trim() + "%') "
	                		+ "OR"
	                		+ " (LOWER(L.DEALER_NAME) LIKE '%" + delarname.toLowerCase().trim() + "%')) "
	                				+ "OR"
	                				+ " ( (L.DEALER_ID LIKE '%" + delarname.toLowerCase().trim() + "%') "
	                						+ "OR"
	                						+ " (LOWER(L.DEALER_ID) "
	                						+ "LIKE '%" + delarname.toLowerCase().trim() + "%')) )";
	            }
	            querySelect += " ) X ";
	            ResultSet countriesresultSet = stmt.executeQuery(querySelect);
	            while(countriesresultSet.next()) {
	            	String[] mobileList = new String[0];
	            	if(countriesresultSet.getString("MOBILE") != null) {
	            		String dealerMobile = countriesresultSet.getString("MOBILE");
	            		mobileList = dealerMobile.split(",");
	            	}
	            	
	                Delarmodel deladerObj = new Delarmodel();
	                String countid = countriesresultSet.getString("DEALER_CODE");
	                String countryname = countriesresultSet.getString("DEALER_NAME");
	                String dealerCity = countriesresultSet.getString("DEALER_CITY").toString();
	                String dealerbankBranch = (countriesresultSet.getString("DEALER_BANK_BRANCH") != null ? countriesresultSet.getString("DEALER_BANK_BRANCH").toString() : "");
	                String dealerBankName = (countriesresultSet.getString("DEALER_BANK_NAME") != null ? countriesresultSet.getString("DEALER_BANK_NAME") : "");
	                String dealerEmail = (countriesresultSet.getString("DEALER_EMAIL") != null ? countriesresultSet.getString("DEALER_EMAIL").toString() : "");
	                deladerObj.setDealerbankBranch(dealerbankBranch);
	                deladerObj.setDealerBankName(dealerBankName);
	                deladerObj.setDealerCity(dealerCity);
	                deladerObj.setDealerCode(countid);
	                deladerObj.setDealerEmail(dealerEmail);
	                deladerObj.setDealerName(countryname);
	                deladerObj.setDealerMobile(mobileList);
	                delarlist.add(deladerObj);
	            }
	            stmt.close();
	            JSONObject responseMap = new JSONObject();
	            responseMap.put(STATUS_CODE, SUCCESS_CODE);
	            responseMap.put("dealerOtpSendStatus", activeName);
	            responseMap.put(STATUS_MESSAGE, SUCCESS);
	            responseMap.put("dealerList",delarlist);
	            return responseMap;
			} else {
				this.storeLogsDataInformation("Countries list not found","", "dealer", userid,"info");
			    return validationApi.errorValidationResponse(ERROR_CODE_500, "Countries list not found");
			}
		}catch (RuntimeException | JSONException | SQLException ex) {
			// TODO: handle exception
			this.storeLogsDataInformation(ex.getMessage().toString(),"", "dealer", userid,"error");
          return validationApi.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, Please try again.");
		}  
	}

	private ResultSet getByCountryCodeData(String countryId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			String[] countryLength = countryId.split(",");
			stmt = this.connection.createStatement();
			String querySelect = "SELECT L.* FROM " + SCHEMA_TABLE + " ." + COUNTRIES_MASTER + " L WHERE ";
			for (int i=0;i<countryLength.length;i++) {
				querySelect += " L.COMPANY_CODE = '" + countryLength[i] + "' ";
				if(i < (countryLength.length)-1 ) {
					querySelect += " OR ";
				}
			}
			this.storeLogsDataInformation(querySelect.toString(),"","dealer","","info");
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void storeLogsDataInformation(String messageInfo, String excutionTime, String category, String userId, String msgType) {
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + "MESSAGE,"
                    + "EXEXUTION_TIME,"
                    + "CREATED_AT,"
                    + "CATEGORY,"
                    + "USERID, LOG_TYPE"
                    + ") VALUES (?,?,?,?,?,?) ";
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, messageInfo.toString());
            pstUserlogin.setString(2, excutionTime.toString());
            pstUserlogin.setString(3, createdAt.toString());
            pstUserlogin.setString(4, category.toString());
            pstUserlogin.setString(5, userId.toString());
            pstUserlogin.setString(6, msgType.toString());
            pstUserlogin.executeUpdate();
            pstUserlogin.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
