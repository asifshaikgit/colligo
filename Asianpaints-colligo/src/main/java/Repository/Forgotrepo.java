package Repository;

import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.OTP_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.forgotservice;
import Utilres.ValidationCtrl;

public class Forgotrepo implements forgotservice {
	
	private Otprepo otpmail;
	private ValidationCtrl validationlog;
	private Connection connection;
	private Mailrepository mail;
	
	@SuppressWarnings("static-access")
	public Forgotrepo() {
		// TODO Auto-generated constructor stub
		otpmail = new Otprepo();
		validationlog = new ValidationCtrl();
		connection = new Database().Connection();
		mail = new Mailrepository();
	}

	@Override
	public Object checkUserId(JsonObject data) {
		// TODO Auto-generated method stub
		String emailId = data.get("emailid").getAsString();
		Statement stmt;
		Timestamp createdAt = new Timestamp(System.currentTimeMillis());
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.USERID AS USER_ID,L.EMAIL,C.COUNTRY_OTP_LENGTH,C.COMPANY_NAME_IN_RECEIPT "
					+ "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "INNER JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " C ON C.COMPANY_CODE = SUBSTR_REGEXPR('[^,]+' IN L.COMPANY_ID FROM 1 OCCURRENCE 1 ) "
							+ "WHERE "
							+ "L.EMAIL = '"+emailId+"' ";
			
			ResultSet resultSet = stmt.executeQuery(querySelect);
			if(resultSet.next()) {
				String userregisterdId = resultSet.getString("USER_ID").toString();
				JSONObject httpResponsuccess = new JSONObject();
				String userp00Id = userregisterdId.substring(0,3);
				if( !userp00Id.equals("P00") ) {
					String otpLengthCountrywise = resultSet.getString("COUNTRY_OTP_LENGTH") != null ? resultSet.getString("COUNTRY_OTP_LENGTH").toString() : "";
					String otpMessage = otpmail.generatethemailOtp(otpLengthCountrywise);
					String insertQry = "INSERT INTO " + SCHEMA_TABLE + "." + OTP_TABLE + " ("
							+ "USEREMAIL,"
							+ "OTP,"
							+ "IS_VERIFY_OTP,"
							+ "IS_RESEND_OTP,"
							+ "CREATED_AT,"
							+ "OTP_TYPE,"
							+ "USERID,"
							+ "UPDATED_AT,"
							+ "UPDATED_BY"
							+ ") values (?,?,?,?,?,?,?,?,?) ";
					PreparedStatement pstOtpMsg = connection.prepareStatement(insertQry);
					pstOtpMsg.setString(1, emailId.toString());
					pstOtpMsg.setString(2, otpMessage.toString());
					pstOtpMsg.setInt(3, 0);
					pstOtpMsg.setInt(4, 0);
					pstOtpMsg.setString(5, createdAt.toString());
					pstOtpMsg.setInt(6, 2);
					pstOtpMsg.setString(7, resultSet.getString("USER_ID").toString());
					pstOtpMsg.setString(8, createdAt.toString());
					pstOtpMsg.setString(9, emailId.toString());
					pstOtpMsg.executeUpdate();
					pstOtpMsg.close();
					stmt.close();
					String OtpMainContent = "Dear "+resultSet.getString("USER_ID").toString()+",<br/>"
							+ "<br/>"
							+ "<br/>"
							+ "<br/>"
							+ "Please Use OTP:"+otpMessage.toString()+" to reset your password.<br/>"
							+ "<br/>"
							+ "<br/>"
							+ "<br/>"
							+ "Regards,<br/>"
							+ resultSet.getString("COMPANY_NAME_IN_RECEIPT").toString()+".";
					this.storeLogsDataInformation(OtpMainContent.toString(),"","mail",resultSet.getString("USER_ID").toString(),"info");
					Boolean mailBoolean = mail.sendMail(OtpMainContent, emailId,"","Forgot Password OTP","");
					int statusInsert = this.getMailOtpMessageid(otpMessage.toString(), emailId.toString(), false);
					if(mailBoolean) {
						httpResponsuccess.put(STATUS_CODE, SUCCESS_CODE);
						httpResponsuccess.put(STATUS_MESSAGE, "Mail successfully sent");
						httpResponsuccess.put("userid", resultSet.getString("USER_ID").toString());
						httpResponsuccess.put("emailid", resultSet.getString("EMAIL").toString());
						httpResponsuccess.put("otp", OtpMainContent.toString());
						httpResponsuccess.put("otplength", otpLengthCountrywise);
						httpResponsuccess.put("otptoken", statusInsert);
					} else {
						httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
						httpResponsuccess.put(STATUS_MESSAGE, "Mail not sent");
					}
				} else {
					httpResponsuccess.put(STATUS_CODE, ERROR_CODE_500);
					httpResponsuccess.put(STATUS_MESSAGE, "User dont have rights to change the password. Please visit http://passport.asianpaints.com to reset your password");
				}
				return httpResponsuccess;
				
			} else {
				this.storeLogsDataInformation("User Email Id is not found.","","mail",emailId,"error");
				return validationlog.errorValidationResponse(ERROR_CODE_500, "User Email Id is not found.");
			}
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object verifyEmailOtp(JsonObject verifyOtpObject) {
		// TODO Auto-generated method stub
		Statement stmt;
		Boolean emailVertitrue =true; Boolean emailVerifyFalse = false;
		try {
			Timestamp createdAt = new Timestamp(System.currentTimeMillis());
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "O.*"
					+ " FROM " + SCHEMA_TABLE + "." + OTP_TABLE + " O "
							+ "WHERE O.ID = '"+verifyOtpObject.get("otptoken").getAsString()+"' "
									+ "AND"
									+ " O.USEREMAIL = '"+verifyOtpObject.get("emailid").getAsString()+"' AND "
											+ "O.USERID = '"+verifyOtpObject.get("userid").getAsString()+"' "
											+ "AND"
											+ " O.IS_RESEND_OTP = "+emailVerifyFalse+" "
											+ "AND"
											+ " O.OTP_TYPE = 2 "
											+ "AND "
											+ "O.OTP = '"+verifyOtpObject.get("otp").getAsString()+"' ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			JSONObject jsonResultRes = new JSONObject();
			
			if(resultSet.next()) {
				int IsVerify = resultSet.getInt("IS_VERIFY_OTP");
				if(IsVerify == 0) {
					String updateQry = "UPDATE "
							+ "" + SCHEMA_TABLE + "." + OTP_TABLE + " O "
									+ "SET "
									+ "O.IS_VERIFY_OTP = "+emailVertitrue+" , UPDATED_AT = '" + createdAt + "' "
									+ "WHERE O.USEREMAIL = '"+verifyOtpObject.get("emailid").getAsString()+"' "
											+ "AND O.ID = '"+verifyOtpObject.get("otptoken").getAsString()+"' "
													+ "AND O.IS_RESEND_OTP = "+emailVerifyFalse+" ";
					PreparedStatement updateQryPrepare = connection.prepareStatement(updateQry);
					updateQryPrepare.execute();
					updateQryPrepare.close();
					jsonResultRes.put(STATUS_CODE, SUCCESS_CODE);
					jsonResultRes.put(STATUS_MESSAGE, SUCCESS);
				} else {
					jsonResultRes.put(STATUS_CODE, ERROR_CODE_500);
					jsonResultRes.put(STATUS_MESSAGE, "OTP Already Verified");
				}
			} else {
				jsonResultRes.put(STATUS_CODE, ERROR_CODE_500);
				jsonResultRes.put(STATUS_MESSAGE, "In Valid OTP");
			}
			stmt.close();
			return jsonResultRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private int getMailOtpMessageid(String otpMessage, String emailId, Boolean resendTag) {
		Statement stmt;
		try {
			Boolean resendOtpfalseStatus = false; 
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "O.*"
					+ " FROM " + SCHEMA_TABLE + "." + OTP_TABLE + " O "
							+ "WHERE O.USEREMAIL = '" + emailId + "' "
									+ "AND O.OTP_TYPE = 2 "
											+ "AND O.IS_RESEND_OTP = "+resendOtpfalseStatus+" ";
				if(resendTag.equals(true)) {
					querySelect += " AND O.OTP = '"+otpMessage+"' ";
				}
			querySelect += " ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			resultSet.next();
			int insertOtpId = resultSet.getInt("ID");
			stmt.close();
			return insertOtpId;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	private void storeLogsDataInformation(String messageInfo, String excutionTime, String category, String userId,String logType) {
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + "MESSAGE,"
                    + "EXEXUTION_TIME,"
                    + "CREATED_AT,"
                    + "CATEGORY,"
                    + "USERID,LOG_TYPE"
                    + ") VALUES (?,?,?,?,?,?) ";
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, messageInfo.toString());
            pstUserlogin.setString(2, excutionTime.toString());
            pstUserlogin.setString(3, createdAt.toString());
            pstUserlogin.setString(4, category.toString());
            pstUserlogin.setString(5, userId.toString());
            pstUserlogin.setString(6, logType.toString());
            pstUserlogin.executeUpdate();
            pstUserlogin.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
