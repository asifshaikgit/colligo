package Repository;

import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.OTP_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONObject;

import Databaseconnection.Database;
import Services.verifyotpservice;

public class VerifyingRepository implements verifyotpservice {
	
	private Connection connection;
	@SuppressWarnings("static-access")
	public VerifyingRepository() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
	}
	
	public Object getCheckOtpMessage(JSONObject verifyOtpObject) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			Boolean resendOtpfalseStatus = false;Boolean otpverifytrue = true;
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "O.*"
					+ " FROM " + SCHEMA_TABLE + "." + OTP_TABLE + " O "
							+ "WHERE O.ID = '"+verifyOtpObject.getString("otptoken")+"' "
									+ "AND"
									+ " O.USERID = '"+verifyOtpObject.getString("userId")+"' "
											+ "AND"
											+ " O.IS_RESEND_OTP = "+resendOtpfalseStatus+" "
											+ "AND"
											+ " O.DEALER_MOBILE = '" + verifyOtpObject.getString("mobileNumber") + "' "
													+ "AND"
													+ " O.OTP = '"+verifyOtpObject.getString("otp")+"' ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			JSONObject jsonResultRes = new JSONObject();
			if(resultSet.next()) {
				int IsVerify = resultSet.getInt("IS_VERIFY_OTP");
				if(IsVerify == 0) {
					String updateQry = "UPDATE "
							+ "" + SCHEMA_TABLE + "." + OTP_TABLE + " O "
									+ "SET "
									+ "O.IS_VERIFY_OTP = "+otpverifytrue+" "
									+ "WHERE O.USERID = '"+verifyOtpObject.getString("userId")+"' "
											+ "AND O.ID = '"+verifyOtpObject.getString("otptoken")+"' "
													+ "AND O.IS_RESEND_OTP = "+resendOtpfalseStatus+" "
													+ "AND O.DEALER_MOBILE = '" + verifyOtpObject.getString("mobileNumber") + "' ";
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
			return jsonResultRes;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
