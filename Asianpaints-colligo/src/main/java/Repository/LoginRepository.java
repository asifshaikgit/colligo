package Repository;

import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.LOGIN_API_ASIAN;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

//import com.google.common.net.HttpHeaders;.....................................
import org.carrot2.shaded.guava.common.net.HttpHeaders;

import Databaseconnection.Database;
import Services.logininterface;
import Utilres.ValidationCtrl;
import models.Countriesclas;

public class LoginRepository implements logininterface {
	
	private ValidationCtrl validationlog;
	Random random;
	
	public LoginRepository() {
		// TODO Auto-generated constructor stub
		validationlog = new ValidationCtrl();
		random = new Random(); 
	}
    int userexistleel = 1;
    public Object loginCheckrepository(String username, String password, String userlevel, String devicetoken) {
        // TODO Auto-generated method stub
        HttpClient httpclient = new DefaultHttpClient();
        String userpass = username.toUpperCase() + "@asianpaints.com:" + password;
        String basicAuth = "Basic " + new String( Base64.getEncoder().encode(userpass.getBytes()));
        try {
        	System.out.println("First inside try LOGIN CAll");
            HttpPost httpPost = new HttpPost(LOGIN_API_ASIAN);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, basicAuth);
            HttpResponse httpresponse = httpclient.execute(httpPost);
            if(httpresponse != null) {
                String entity = EntityUtils.toString(httpresponse.getEntity());
                Integer statusCode = httpresponse.getStatusLine().getStatusCode();
                this.storeLogsDataInformation(entity.toString(), "", "login", userpass, "info");
                if(statusCode.equals(200)) {
                    JSONObject userloggedinfo = new JSONObject(entity.toString());
                    if(userloggedinfo.has("email")) {
                        return this.userFindByEmail( userloggedinfo.get("email").toString(), username.toUpperCase(), userlevel, devicetoken );
                    } else {
                        return validationlog.errorValidationResponse(ERROR_CODE_500, "Login Failed, Please enter login details correctly");
                    }
                } else {
                	System.out.println("First after the call failed condition LOGIN CAll");
                    return validationlog.errorValidationResponse(ERROR_CODE_500, "Login Failed, Please enter login details correctly");
                }
            } else {
            	this.storeLogsDataInformation("Login Failed, Please enter login details correctly", "", "login", userpass, "info");
                return validationlog.errorValidationResponse(ERROR_CODE_500, "Login Failed, Please enter login details correctly");
            }
        } catch (IOException | URISyntaxException | InterruptedException | HttpException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("", e.toString(), "login", userpass, "error");
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Login Failed, Please enter login details correctly");
        }
    }
    
    private String generateRandomString() {
	      char[] values = {'a','b','c','d','e','f','g','h','i','j',
	               'k','l','m','n','o','p','q','r','s','t',
	               'u','v','w','x','y','z','0','1','2','3',
	               '4','5','6','7','8','9'};

	      String out = "";

	      for (int i=0;i<32;i++) {
	          int idx = random.nextInt(values.length);
	          out += values[idx];
	      }
	      return out;
	}

	public Object userFindByEmail(String emailId, String username, String userlevel, String devicetoken) {
		// TODO Auto-generated method stub
//		Timestamp createdAt = new Timestamp(System.currentTimeMillis());
		String userLoggedid = null;
		String usernameLogged = null;
//		int userintValue = Integer.parseInt(userlevel);
		String randomToken = this.generateRandomString();
		try {
			JSONObject responseMap = new JSONObject();
			ResultSet getResultdata = this.getExistingUserData(username);
			
			if(getResultdata.next()) {
				
				userLoggedid = getResultdata.getString("USERID").toString();
				usernameLogged = getResultdata.getString("USERNAME").toString();
				
				userexistleel = getResultdata.getInt("USER_LEVEL");
				Boolean userDeletestatus = getResultdata.getBoolean("USER_DELETE_FLAG");
//				Boolean userIdlogged = getResultdata.getBoolean("IS_LOGGED");
				if(userDeletestatus.equals(true)) {
					responseMap.put(STATUS_CODE, ERROR_CODE_500);
		            responseMap.put(STATUS_FLAG, 1);
		            responseMap.put(STATUS_MESSAGE, "User Account deleted, Please check and login.");
		            return responseMap;
				}
//				//////////
//				if(userIdlogged.equals(true)) {
//					responseMap.put(STATUS_CODE, ERROR_CODE_500);
//		            responseMap.put(STATUS_FLAG, 1);
//		            responseMap.put(STATUS_MESSAGE, "User Already Logged into another Device, Please check and login.");
//		            return responseMap;
//				}
				////////////
				Boolean islogged = true;
				String updateQry = "UPDATE " + SCHEMA_TABLE + "." + DL_APP_USER_INFO + " O "
						+ "SET"
						+ " O.IS_LOGGED = " +islogged+ ", O.USER_PUSH_TOKEN = '"+devicetoken+"', O.USER_LOGIN_TOKEN = '"+randomToken.toString()+"' WHERE "
						+ "O.USERID = '" +getResultdata.getString("USERID").toString()+ "' ";
				PreparedStatement updateQryPrepare = Database.Connection().prepareStatement(updateQry);
				updateQryPrepare.execute();
				updateQryPrepare.close();
				
			} else {
//				ResultSet resultSet = this.checkUserLoginTable(username);
//				resultSet.next();
//				String insertQry = "INSERT INTO " + SCHEMA_TABLE + "." + DL_APP_USER_INFO + " ("
//				        + "USERID,"
//				        + "USERNAME,"
//				        + "MOBILE_NO,"
//				        + "EMAIL,"
//				        + "AGENCY_NAME,"
//				        + "MANAGER_NAME,"
//				        + "MANAGER_PHONE_NO,"
//				        + "MANAGER_EMAIL_ID,"
//				        + "COMPANY_ID,"
//				        + "CREATED_AT,"
//				        + "USER_LEVEL,"
//				        + "USER_PUSH_TOKEN,"
//				        + "IS_LOGGED,"
//				        + "USER_DELETE_FLAG,"
//				        + "CREATED_BY,"
//				        + "UPDATED_BY,"
//				        + "UPDATED_AT,"
//				        + "USER_LOGIN_TOKEN "
//				        + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
//				PreparedStatement pstUserlogin = connection.prepareStatement(insertQry);
//				pstUserlogin.setString(1, resultSet.getString("USER_ID").toString());
//				pstUserlogin.setString(2, resultSet.getString("USERNAME").toString());
//				pstUserlogin.setString(3, resultSet.getString("MOBILE_NO").toString());
//				pstUserlogin.setString(4, resultSet.getString("EMAIL").toString());
//				pstUserlogin.setString(5, (resultSet.getString("AGENCY_NAME") != null ? resultSet.getString("AGENCY_NAME").toString() : ""));
//				pstUserlogin.setString(6, (resultSet.getString("MANAGER_NAME") != null ? resultSet.getString("MANAGER_NAME").toString() : ""));
//				pstUserlogin.setString(7, (resultSet.getString("MANAGER_PHONE_NO") != null ? resultSet.getString("MANAGER_PHONE_NO").toString() : ""));
//				pstUserlogin.setString(8, (resultSet.getString("MANAGER_EMAIL_ID") != null ? resultSet.getString("MANAGER_EMAIL_ID").toString(): ""));
//				pstUserlogin.setString(9, (resultSet.getString("COMPANY_CODE") != null ? resultSet.getString("COMPANY_CODE").toString() : ""));
//				pstUserlogin.setString(10, createdAt.toString());
//				pstUserlogin.setInt(11, userintValue);
//				pstUserlogin.setString(12, devicetoken);
//				pstUserlogin.setBoolean(13, true);
//				pstUserlogin.setBoolean(14, false);
//				pstUserlogin.setString(15, resultSet.getString("USER_ID").toString());
//				pstUserlogin.setString(16, resultSet.getString("USER_ID").toString());
//				pstUserlogin.setString(17, createdAt.toString());
//				pstUserlogin.setString(18,randomToken);
//				pstUserlogin.executeUpdate();
//				pstUserlogin.close();
//				ResultSet resultSetInsert = this.getExistingUserData(username);
//				resultSetInsert.next();
//				Boolean usercDeletestatus = resultSetInsert.getBoolean("USER_DELETE_FLAG");
//				if(usercDeletestatus.equals(true)) {
//					responseMap.put(STATUS_CODE, ERROR_CODE_500);
//		            responseMap.put(STATUS_FLAG, 1);
//		            responseMap.put(STATUS_MESSAGE, "User Account deleted,Please check and login.");
//		            return responseMap;
//				}
//				userLoggedid = resultSetInsert.getString("USERID").toString();
//				usernameLogged = resultSetInsert.getString("USERNAME").toString();
//				userexistleel = resultSetInsert.getInt("USER_LEVEL");
			}
            List<Countriesclas> countriesData = this.getCountrieslist(username);
            
			if(countriesData.size() > 0) {
			    responseMap.put(STATUS_CODE, SUCCESS_CODE);
	            responseMap.put(STATUS_FLAG, 0);
	            responseMap.put(STATUS_MESSAGE, SUCCESS);
	            responseMap.put("countries",countriesData);
	            responseMap.put("userid", userLoggedid);
	            responseMap.put("username", usernameLogged);
	            responseMap.put("usertoken", randomToken);
	            responseMap.put("userlevel", userexistleel);
			} else {
			    responseMap.put(STATUS_CODE, ERROR_CODE_500);
	            responseMap.put(STATUS_FLAG, 1);
	            responseMap.put(STATUS_MESSAGE, "Countries list not found");
			}
			return responseMap;
			
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Countriesclas> getCountrieslist(String userId) {
		System.out.println("//////////////////////////////////////////");
		
		Statement stmt;
		List<Countriesclas> countries = new ArrayList<>();
		try {
			stmt = Database.Connection().createStatement();
			String querySelect = "SELECT UI.* FROM "+SCHEMA_TABLE+"."+DL_APP_USER_INFO+" UI WHERE UI.USERID ='"+userId+"'";
			ResultSet countriesresultSet = stmt.executeQuery(querySelect);
			while(countriesresultSet.next()) {
				ResultSet countryDatinfo = this.getOtpStatusforCountry(countriesresultSet.getString("COMPANY_ID").toString());
				while(countryDatinfo.next()) {
					String countid = countryDatinfo.getString("COMPANY_CODE");
					String countryname = countryDatinfo.getString("COUNTRY_NAME");
					String countryCode = countryDatinfo.getString("COUNTRY_CODE");
					String currencycode = countryDatinfo.getString("CURRENCY");
					String numberFormate = countryDatinfo.getString("NUMBER_FORMATE");
					String ocrModelId = countryDatinfo.getString("OCR_MODE_ID");
					String apiUserName = countryDatinfo.getString("OCR_API_USERNAME");
					String apiUserPassword = "";
	                Integer daysBack = countryDatinfo.getInt("ACCEPT_BACK_DAYS");
	                String countryPriceLimit = countryDatinfo.getString("COUNTRY_PRICE_LIMIT");
	                String countrydecimalpoints = countryDatinfo.getString("DECIMAL_POINTS").toString();
	                Boolean ocrStatus = countryDatinfo.getBoolean("COUNTRY_OCR_STATUS");
	                String[] paymentMode = countryDatinfo.getString("PAYMENT_MODE").toString().split(",");
//	                boolean cash = false;
//	                boolean noncash = false;
//	                for (String mode : paymentMode) {
//	                	String value = mode.trim();
//	                    if (value.equalsIgnoreCase("cash")) {
//	                        cash = true;
//	                    }
//	                    if (value.equalsIgnoreCase("non cash")) {
//	                        noncash = true;
//	                    }
//	                }
	                boolean cash = Arrays.stream(paymentMode)
	                        .map(String::trim)
	                        .anyMatch(mode -> mode.equalsIgnoreCase("cash"));

	        		boolean noncash = Arrays.stream(paymentMode)
	                           .map(String::trim)
	                           .anyMatch(mode -> mode.equalsIgnoreCase("non cash"));
	        		
					countries.add(new Countriesclas(
							countid, 
							countryname, 
							currencycode, 
							countryCode, 
							numberFormate, 
							daysBack, 
							ocrModelId, 
							apiUserName, 
							apiUserPassword, 
							countryPriceLimit,
							countrydecimalpoints,
							ocrStatus, cash ,noncash));
				}
				
			}
			
			stmt.close();
			return countries;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return countries;
		}
	}
	
	public ResultSet checkUserLoginTable(String userId) {
		Statement stmt;
		try {
			stmt = Database.Connection().createStatement();
			String querySelect = "SELECT "
			        + "UI.USERID AS USER_ID,"
			        + "UI.USERNAME,"
			        + "UI.MOBILE_NO,"
			        + "UI.EMAIL,"
			        + "UI.MANAGER_ID,"
			        + "UI.AGENCY_NAME,"
			        + "UI.MANAGER_NAME,"
			        + "UI.MANAGER_PHONE_NO,"
			        + "UI.MANAGER_EMAIL_ID,"
			        + "CM.COMPANY_CODE,"
			        + "CM.CURRENCY,"
			        + "CM.COUNTRY_CODE "
			        + "FROM "+SCHEMA_TABLE+"."+COUNTRIES_MASTER+" CM "
			                + "INNER JOIN "+SCHEMA_TABLE+"."+DL_APP_USER_INFO+" UI "
			                        + "ON UI.COMPANY_ID = CM.COMPANY_CODE WHERE "
			                        + "UI.USER_ID ='"+userId+"'";
			System.err.println(querySelect);
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private ResultSet getExistingUserData(String username) {
		Statement stmt;
		try {
			stmt = Database.Connection().createStatement();
			String querySelect = "SELECT "
					+ "L.* "
					+ "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "WHERE "
							+ "L.USERID = '"+username+"' "
									+ "ORDER BY L.ID DESC LIMIT 1";
			System.err.println(querySelect);
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private ResultSet getOtpStatusforCountry(String countryId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			String[] countryLength = countryId.split(",");
			stmt = Database.Connection().createStatement();
			String querySelect = "SELECT "
			        + "L.*"
			        + " FROM " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " L WHERE ";
			for (int i=0;i<countryLength.length;i++) {
				querySelect += " L.COMPANY_CODE = '" + countryLength[i] + "' ";
				if(i < (countryLength.length)-1 ) {
					querySelect += " OR ";
				}
			}
			ResultSet resultSet = stmt.executeQuery(querySelect);
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void storeLogsDataInformation(String messageType, String excutionTime, String category, String userId, String infoType) {
		
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + " MESSAGE,"
                    + " EXEXUTION_TIME,"
                    + " CREATED_AT,"
                    + " CATEGORY,"
                    + " USERID,LOG_TYPE"
                    + ") VALUES (?,?,?,?,?,?) ";
    		PreparedStatement pstUserlogin = Database.Connection().prepareStatement(insertCommand);
            pstUserlogin.setString(1, excutionTime.toString());
			pstUserlogin.setString(2, messageType.toString());
			pstUserlogin.setString(3, createdAt.toString());
	        pstUserlogin.setString(4, category.toString());
	        pstUserlogin.setString(5, userId.toString());
	        pstUserlogin.setString(6, infoType.toString());
	        pstUserlogin.executeUpdate();
	        pstUserlogin.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
