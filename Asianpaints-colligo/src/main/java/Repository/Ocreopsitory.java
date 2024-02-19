package Repository;

import static Constants.AsianConstants.DEALER_MOBILE;
import static Constants.AsianConstants.DEALER_OCR_DATA;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.SUCCESS_CODE;
import static Constants.AsianConstants.COUNTRIES_MASTER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.OcrInterFace;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;

public class Ocreopsitory implements OcrInterFace {
    
    private ValidationCtrl validationlog;
    private Jsonresponse responseCtrl;
    private Connection connection;
    
    private String microNumber;
    private String payieName;
    private String accountNumber;
    private String Amount;
    private String chequeDate;
    private String chequeNumber;
    
    @SuppressWarnings("static-access")
	public Ocreopsitory() {
		// TODO Auto-generated constructor stub
    	validationlog = new ValidationCtrl();
    	responseCtrl = new Jsonresponse();
    	connection = new Database().Connection();
	}
    
    
    
    @Override
	public Object getChecktheOcrData(JsonObject dataResponse) {
		// TODO Auto-generated method stub
    	try {
            String AzureUrl = "";
            String AzureUser = dataResponse.get("userid").getAsString();
            String countrycode = dataResponse.get("countrycode").getAsString();
            String AzureDealerID = "";
            String instrumenttype = "";
            JSONObject resultData = new JSONObject( dataResponse.get("ocrresult").toString() );
            JsonObject dataOcresponse = new Gson().fromJson(resultData.toString(), JsonObject.class);
            if(resultData.has("message")) {
                if(resultData.get("message").toString().equals("Success")) {
                    return this.storeDealerinstrumentData(dataOcresponse, AzureDealerID, AzureUser, instrumenttype, AzureUrl, countrycode);
                } else {
                    return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong,Please try again..");
                }
            } else {
                return validationlog.errorValidationResponse(ERROR_CODE_500, "Please Upload Valid Dcoument.");
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong,Please try again..");
        }
	}
        
    private Object getCheckDealerAccountNumber(String accountNumber, String countrycode) {
        try {
            JSONObject jsonOcrData = new JSONObject();
            ResultSet getDealerInfo = this.getAccountDealerinfo(accountNumber, countrycode);
            if(getDealerInfo.next()) {
                jsonOcrData.put("DEALER_ID", getDealerInfo.getString("DEALER_ID").toString());
                jsonOcrData.put(STATUS_CODE, SUCCESS_CODE);
            } else {
                jsonOcrData.put(STATUS_CODE, ERROR_CODE_500);
            }
            return jsonOcrData;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
        
    }

    public Object storeDealerinstrumentData(JsonObject dataRes, String azureDealerID, String azureUser, String instrumenttype, String azureUrl, String countrycode) {
        // TODO Auto-generated method stub
        try {
            Boolean labelMicroNumber = false;
            Boolean labelPayieName = false;
            Boolean labelAccountNumber = false;
            Boolean labelAmount = false;
            Boolean labelChequeDate = false;
            Boolean labelChequeNumber = false;
            
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
//            Timestamp createdAt = this.getCountryTimestamp(countrycode);
//            this.getC
            JsonArray resultOcr = dataRes.get("result").getAsJsonArray();
            String azurUrl = resultOcr.get(0).getAsJsonObject().get("input").getAsString();
            JsonArray predictionObj = resultOcr.get(0).getAsJsonObject().get("prediction").getAsJsonArray();
           
            if(resultOcr.get(0).getAsJsonObject().has("prediction") && predictionObj.size() > 0) {
                
                for( int k=0; k < predictionObj.size(); k++) {
                    JsonObject predictionObjData = predictionObj.get(k).getAsJsonObject();
                    if(predictionObjData.get("label").getAsString().equals("payee_name")) {
                        payieName = predictionObjData.get("ocr_text").getAsString();//.substring(0,100);
                        if(payieName.length() > 100) {
                        	payieName = payieName.substring(0, 100);
                        }
                        labelPayieName = true;
                    }
                    if(predictionObjData.get("label").getAsString().equals("account_no")) {
                        accountNumber = predictionObjData.get("ocr_text").getAsString();
                        if(accountNumber.length() > 100) {
                        	accountNumber = accountNumber.substring(0, 100);
                        }
                        labelAccountNumber = true;
                    }
                    if(predictionObjData.get("label").getAsString().equals("amount")) {
                        Amount = predictionObjData.get("ocr_text").getAsString();
                        if(Amount.length() > 100) {
                        	Amount = Amount.substring(0, 100);
                        }
                        labelAmount = true;
                    }
                    if(predictionObjData.get("label").getAsString().equals("cheque_date")) {
                        chequeDate = predictionObjData.get("ocr_text").getAsString();
                        if(chequeDate.length() > 100) {
                        	chequeDate = chequeDate.substring(0, 100);
                        }
                        labelChequeDate = true;
                    }
                    if(predictionObjData.get("label").getAsString().equals("cheque_no")) {
                        chequeNumber = predictionObjData.get("ocr_text").getAsString();
                        if(chequeNumber.length() > 100) {
                        	chequeNumber = chequeNumber.substring(0, 100);
                        }
                        labelChequeNumber = true;
                    }
                    if(predictionObjData.get("label").getAsString().equals("micr_no")) {
                        microNumber = predictionObjData.get("ocr_text").getAsString();
                        if(microNumber.length() > 100) {
                        	microNumber = microNumber.substring(0, 100);
                        }
                        labelMicroNumber = true;
                    }
                }
                if( 
                        labelPayieName == true 
                        && labelAccountNumber == true 
                        && labelAmount == true 
                        && labelChequeDate == true 
                        && labelChequeNumber == true 
                        && labelMicroNumber == true
                        
                        ) {
                    JSONObject checkAccountNumber = new JSONObject(this.getCheckDealerAccountNumber(accountNumber, countrycode).toString());
                    this.storeLogsDataInformation(checkAccountNumber.toString(),"","OCR",azureUser,"info");
                    
                    ResultSet resultSet = this.getMaxInstIdData();
    	            resultSet.next();
    	            int instrumentTokenumber = resultSet.getInt("maxId")+1;
    	            String instrumentid = this.generateReceiptNumber(countrycode,instrumentTokenumber);
                    
                        JSONObject jsonOcrData = new JSONObject();
                        String getazureDealerID = (checkAccountNumber.has("DEALER_ID") ? checkAccountNumber.getString("DEALER_ID").toString() : "");
                        String insertDealerocrdata = "INSERT INTO " + SCHEMA_TABLE + "."+ DEALER_OCR_DATA + " ("
                                + "DEALER_ACCOUNT_NUMBER,"
                                + "DEALER_ID,"
                                + "DEALER_MOBILE_NUMBER,"
                                + "DEALER_INSTRUMENT_TYPE,"
                                + "DEALER_INSTRUMENT_NUMBER,"
                                + "DEALER_INSTRUMENT_DATE,"
                                + "DEALER_BANK_NAME,"
                                + "DEALER_BANK_BRANCH,"
                                + "DEALER_INSTRUMENT_AMOUNT,"
                                + "DEALER_REPORT_ID,"
                                + "DEALER_INSTRUMENT_IMAGE_PATH,"
                                + "CREATED_AT,"
                                + "INSTRUMENT_ID"
                                + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                        
                        PreparedStatement pstOtpMsg = connection.prepareStatement(insertDealerocrdata);
                        pstOtpMsg.setString(1, accountNumber.toString());
                        pstOtpMsg.setString(2, getazureDealerID);
                        pstOtpMsg.setString(3, "");
                        pstOtpMsg.setString(4, instrumenttype);
                        pstOtpMsg.setString(5, microNumber.toString());
                        pstOtpMsg.setString(6, chequeDate.toString());
                        pstOtpMsg.setString(7, payieName.toString());
                        pstOtpMsg.setString(8, "");
                        pstOtpMsg.setString(9, Amount.toString());
                        pstOtpMsg.setString(10, "");
                        pstOtpMsg.setString(11, azurUrl.toString());
                        pstOtpMsg.setString(12, createdAt.toString());
                        pstOtpMsg.setString(13, instrumentid.toString());
                        pstOtpMsg.executeUpdate();
                        pstOtpMsg.close();
                        if(pstOtpMsg != null) {
                            jsonOcrData.put("micr_no", microNumber);
                            jsonOcrData.put("cheque_no", chequeNumber);
                            jsonOcrData.put("cheque_date", chequeDate);
                            jsonOcrData.put("amount", Amount);
                            jsonOcrData.put("account_no", accountNumber);
                            jsonOcrData.put("payee_name", payieName);
                            jsonOcrData.put("azurLink", azurUrl);
                            jsonOcrData.put("instrumenttype", "cheque");
                            jsonOcrData.put("instrumentid", instrumentid.toString());
                        }
                       Object dealerInformation = this.getDLDealerInformationById(getazureDealerID);
                       Object ocrResponseinfo = responseCtrl.successOcrResponseCall("ocrData", jsonOcrData,"dealerList", dealerInformation);
                       this.storeLogsDataInformation(ocrResponseinfo.toString(),"","OCR",azureUser,"info");
                       return ocrResponseinfo;
                } else {
                    return validationlog.errorValidationResponse(ERROR_CODE_500, "Please Upload Valid Cheque");
                }
                
            } else {
                return validationlog.errorValidationResponse(ERROR_CODE_500, "Please Upload Valid Cheque");
            }
                     
        } catch (Exception e) {
            // TODO: handle exception
            this.storeLogsDataInformation(e.getMessage().toString(),"","OCR",azureUser,"error");
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong,Please try again..");
        }
    }

	private ResultSet getAccountDealerinfo(String accountNumber, String countrycode) {
        // TODO Auto-generated method stub
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String querySelect = "SELECT "
                    + "L.* "
                    + "FROM " + SCHEMA_TABLE + " ." + DL_DEALER_TABLE + " L "
                            + "WHERE L.DEALER_BANK_ACCOUNT_NUMBER = '"+accountNumber+"' AND DEALER_COMPANY_CODE = '"+countrycode+"' ORDER BY ID DESC";
            ResultSet resultSet = stmt.executeQuery(querySelect);
            stmt.close();
            return resultSet;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    private Object getDLDealerInformationById(String dealerid) {
        // TODO Auto-generated method stub
           Statement stmt;
           try {
               stmt = connection.createStatement();
               String querySelect = "SELECT "
                       + "L.DEALER_ID,"
                       + "L.DEALER_NAME,"
                       + "L.DEALER_BANK_BRANCH,"
                       + "L.DEALER_BANK_NAME,"
                       + "L.DEALER_EMAIL "
                       + "FROM " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " L "
                               + "INNER JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " K "
                                       + "ON L.DEALER_COMPANY_CODE = K.COMPANY_CODE "
                                       + "WHERE L.DEALER_ID = '"+ dealerid +"' ";
               ResultSet resultSet = stmt.executeQuery(querySelect);
               JSONObject delarlist = new JSONObject();
               while( resultSet.next() ) {
                   ArrayList<String> mobileList = this.getDealerMobileInfoData(resultSet.getString("DEALER_ID"));
                   String countid = resultSet.getString("DEALER_ID");
                   String countryname = resultSet.getString("DEALER_NAME");
                   String dealerbankBranch = (resultSet.getString("DEALER_BANK_BRANCH") != null ? resultSet.getString("DEALER_BANK_BRANCH") : "");
                   String dealerBankName = (resultSet.getString("DEALER_BANK_NAME") != null ? resultSet.getString("DEALER_BANK_NAME") : "");
                   String dealerEmail = resultSet.getString("DEALER_EMAIL") != null ? resultSet.getString("DEALER_EMAIL").toString() : "";
                   delarlist.put("dealerName", countid);
                   delarlist.put("dealerCode", countryname);
                   delarlist.put("dealerEmail", dealerEmail);
                   delarlist.put("dealerBankName", dealerBankName);
                   delarlist.put("dealerBankBranch", dealerbankBranch);
                   delarlist.put("dealerMobile", mobileList);
               }
               stmt.close();
               return delarlist;
           } catch (Exception e) {
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
    
    private void storeLogsDataInformation(String message, String excutionTime, String category, String userId, String logType) {
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + "MESSAGE,"
                    + "EXEXUTION_TIME,"
                    + "CREATED_AT,"
                    + "CATEGORY,"
                    + "USERID"
                    + "LOG_TYPE"
                    + ") VALUES (?,?,?,?,?,?) ";
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, message.toString());
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
    
    private ResultSet getMaxInstIdData() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT MAX(I.Id) AS maxId FROM " + SCHEMA_TABLE + "." + DEALER_OCR_DATA + " I";
			System.out.println(querySelect);
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    private String generateReceiptNumber(String azureUser, Integer instrumentTokenumber) {
		// TODO Auto-generated method stub
    	String x = "INO".concat(azureUser + instrumentTokenumber.toString());
	    return x;
	}

}
