package Repository;

import static Constants.AsianConstants.COLLECTION_RECEIPT_TABLE;
import static Constants.AsianConstants.CONTENTSTORAGE;
import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.DL_FONT_COLLECTION_URL;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.INSTRUMENT_TABLE;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;
import static Constants.AsianConstants.devLogoPath;
import static Constants.AsianConstants.DL_RECEIPT_INSTRUMENT_HISTORY_DETAILS;
import static Constants.AsianConstants.DL_COLLECTION_RECEIPT_HISTORY_DETAILS;
import static Constants.AsianConstants.DL_RECEIPT_REMARKS_TABLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import Constants.Languageslist;
import Databaseconnection.Database;
import Services.NonOcrServices;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;
import models.Instrumentmodel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NonOcrRepository implements NonOcrServices {
	
	private Connection connection;
	Random random;
	private Jsonresponse jsonValit;
	private Pushnotificationrepo pushrepo;
    private ValidationCtrl validationlog;
    private Mailrepository mailling;
    private Languageslist langinfo;
	@SuppressWarnings("static-access")
	
	public NonOcrRepository() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
		random = new Random();
		jsonValit = new Jsonresponse();
		validationlog = new ValidationCtrl();
		pushrepo = new Pushnotificationrepo();
		mailling = new Mailrepository();
		langinfo = new Languageslist();
	}
	
	public Object checkSavingtheNonOcrData(JsonObject data, String fullPathImage, String devlogopath) {
		
		ResultSet usercheckResult = this.getLoggeduserinfo(data.get("userid").getAsString());
		
		Boolean usercheck;
		String companyName;
		String userloggedName;
		try {
			usercheckResult.next();
			usercheck = usercheckResult.getBoolean("USER_DELETE_FLAG");
			companyName = usercheckResult.getString("COMPANY_NAME_IN_RECEIPT").toString();
			userloggedName = usercheckResult.getString("USERNAME").toString();
			if(usercheck.equals(true)) {
	    		return jsonValit.loginerrorResponseCall();
	    	}
			return  this.getloadPreviousInstrumentsData(data, fullPathImage, devlogopath,companyName,userloggedName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage().toString());
			return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
		}		
		
	}
	
	private Timestamp getCountryTimestamp(String cOMPANYCODE) {
		Timestamp createAt =null;
		try {
			String getTimestamp="SELECT dcm.TIMEZONE FROM "+ SCHEMA_TABLE +" . "+ COUNTRIES_MASTER +" dcm WHERE dcm.COMPANY_CODE = ? ";
			PreparedStatement pst = connection.prepareStatement(getTimestamp);
			pst.setString(1, cOMPANYCODE);
			ResultSet resultSet = pst.executeQuery();
			while(resultSet.next()) {
				String timestamp = resultSet.getString("TIMEZONE");
				Pattern pattern = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?");
				Matcher matcher = pattern.matcher(timestamp);
				if (matcher.find()) {
					String hour = matcher.group(1);
					String minute = matcher.group(2) != null ? matcher.group(2) : "00";
					System.out.println("Hour: " + hour);
					System.out.println("Minute: " + minute);
					
					Calendar calendar = Calendar.getInstance();
					System.out.println(calendar.getTime());
					calendar.setTime(new Date());
					calendar.add(calendar.HOUR, Integer.parseInt(hour));
					calendar.add(calendar.MINUTE, Integer.parseInt(minute));
					createAt = new Timestamp(calendar.getTimeInMillis());
				}
			}
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return createAt;
	}
    
    @SuppressWarnings("unused")
    public Object getSaveNonOcrData(JsonObject data, String fullPathImage, String devlogopath, String instrumentNumbers, String companyName, String userloggedName) {
        // TODO Auto-generated method stub
//    	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    	
        Integer userLoggedid = 0;
        try {
            
            String userId = data.get("userid").getAsString();
            ResultSet getUserinformation = this.getExistingUserDataById(userId);
            getUserinformation.next();
            int userLevel = getUserinformation.getInt("USER_LEVEL");
            String userCompanyCode = getUserinformation.getString("COMPANY_ID").toString();
            Statement statement = connection.createStatement();
            String statuscollection = data.get("statuscollection").getAsString();
            String inputDealerid = data.get("dealerId").getAsString();
            ResultSet dealerCheck = this.checkDealerinfo(inputDealerid);
            dealerCheck.next();
            String dealerIdBack = !inputDealerid.isEmpty() ? dealerCheck.getString("DEALER_ID") != null ? dealerCheck.getString("DEALER_ID").toString() : "" : "";
            String dealerEmail = !inputDealerid.isEmpty() ? dealerCheck.getString("DEALER_EMAIL") != null ? dealerCheck.getString("DEALER_EMAIL").toString() : "" : "";
            if(dealerIdBack.isEmpty() && data.get("saveflag").getAsInt() == 1) {
            	this.storeLogsDataInformation("Dealer information not found with dealer id "+inputDealerid,"","OCR",data.get("userid").getAsString(),"info");
                return validationlog.errorValidationResponse(ERROR_CODE_500, "Dealer information not found");
            } else {
            	connection.setAutoCommit(false);
                ResultSet resultSet = this.getMaxIdData();
                resultSet.next();
                userLoggedid = resultSet.getInt("maxId")+1;
                String countrycode = data.has("countrycode") ? data.get("countrycode").getAsString() : "";
                String receiptNumber = this.generateReceiptNumber(userLoggedid, countrycode);
                String paymentMethod = "Non Cash";
                Timestamp createdAt = this.getCountryTimestamp(countrycode);
                String insertQry = "INSERT INTO " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " "
                        + "("
                        + "DEALER_ID,"
                        + "DEALER_MOBILE,"
                        + "DEALER_EMAIL,"
                        + "RECEIPT_AMOUNT,"
                        + "CREATED_AT,"
                        + "CREATED_BY,"
                        + "MODE_OF_PAYMENT,"
                        + "RECEIPT_NUMBER,"
                        + "OTP,"
                        + "DEALER_REPRESENTATION,"
                        + "DEALER_REMARKS,"
                        + "DEALER_COLLETION_STATUS,"
                        + "OCRTYPE,"
                        + "UPDATED_AT,"
                        + "DEPO_RECEIPT_STATUS,"
                        + "COMPANY_CODE,UPDATED_BY"
                        + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                int saveFlag = data.get("saveflag").getAsInt();
	            String receiptStatussave = "draft";
	            if(saveFlag == 1) {
	            	receiptStatussave = "issued";
	            }
                PreparedStatement pstUserlogin = connection.prepareStatement(insertQry);
                pstUserlogin.setString(1, data.get("dealerId").getAsString());
                pstUserlogin.setString(2, data.get("dealerMobile").getAsString());
                pstUserlogin.setString(3, "");
                pstUserlogin.setString(4, data.get("finalRecepitAmount").getAsString());
                pstUserlogin.setString(5, createdAt.toString());
                pstUserlogin.setString(6, data.get("userid").getAsString());
                pstUserlogin.setString(7, paymentMethod);
                pstUserlogin.setString(8, receiptNumber);
                pstUserlogin.setString(9, "");
                pstUserlogin.setString(10, data.get("dealerRepresentative").getAsString());
                pstUserlogin.setString(11, data.get("dealerRemark").getAsString());
                pstUserlogin.setString(12, receiptStatussave);
                pstUserlogin.setString(13, data.get("ocr").getAsString());
                pstUserlogin.setString(14, createdAt.toString());
                pstUserlogin.setString(15, "");
                pstUserlogin.setString(16, countrycode);
                pstUserlogin.setString(17, data.get("userid").getAsString());
                int executeInfoStatus = pstUserlogin.executeUpdate();
                pstUserlogin.close();
                JSONObject responseMap = new JSONObject();
                if(executeInfoStatus == 1) {
                	
                	this.insertReciptRemarks(data.get("userid").getAsString(), receiptNumber, data.get("dealerRemark").getAsString(),"SALES",countrycode);
                	this.updateReceiptIntrumentupdate(receiptNumber, instrumentNumbers);
                    this.getInsertNonOcrInstrumentData(data.get("instruments").toString(), receiptNumber,data.get("userid").getAsString(), countrycode, statuscollection);
                    
                    if(data.get("saveflag").getAsInt() == 1) {
                        String receptAzureUrl = this.getGenerateOpenpdf(
                        		receiptNumber, 
                        		data.get("userid").getAsString(), 
                        		fullPathImage,
                        		"noncash", 
                        		data.get("finalRecepitAmount").getAsString(), 
                        		data.get("paymentype").getAsString(), 
                        		devlogopath,dealerEmail, countrycode,companyName,userloggedName, data.get("lang").getAsString(),"");
                        if(receptAzureUrl != null) {
                            this.updateReceiptPathtoReceipt(receptAzureUrl.toString(),receiptNumber);
                        } else {
                        	connection.rollback();
                        }
                        responseMap.put("receipturl", receptAzureUrl.toString());
                        responseMap.put(STATUS_MESSAGE, SUCCESS);
                    } else {
                        responseMap.put("receipturl", "");
                        responseMap.put(STATUS_MESSAGE, "Receipt : "+receiptNumber+", Saved Successfully as Draft.");
                    }
                    responseMap.put("receiptNumber", receiptNumber);
                    responseMap.put("receiptAmount", data.get("finalRecepitAmount").getAsString());
                    responseMap.put(STATUS_CODE, SUCCESS_CODE);
                    responseMap.put(STATUS_FLAG, 0);
                    
                } else {
                	responseMap.put(STATUS_CODE, ERROR_CODE_500);
                    responseMap.put(STATUS_MESSAGE, "Something went wrong!, please try again..");
                }
                connection.commit();
                return responseMap;
            }
        } catch (Exception e) {
        	// TODO: handle exception
        	try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	this.storeLogsDataInformation("Something went wrong!, please try again "+e.getMessage().toString(),"","OCR",data.get("userid").getAsString(),"error");
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
    }
    
    /*
	 * GET THE RECEIPT INFORMATION FROM THE TABLE
	 */
	
	private ResultSet getloadReceiptInformation(String receiptNumber) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT * FROM " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " R WHERE R.RECEIPT_NUMBER = '"+receiptNumber+"'";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
	private void insertReciptRemarks(String userid, String receiptNumber, String receiptRemarks,String receiptType, String countrycode) {
		Timestamp createdAt = this.getCountryTimestamp(countrycode);
		try {
    		String instrumentQry = "INSERT INTO " + SCHEMA_TABLE + "." + DL_RECEIPT_REMARKS_TABLE + " "
                    + "(RECEIPT_NUMNER,"
                    + "RECEIPT_REMARKS,"
                    + "REMARK_TYPE,"
                    + "CREATED_AT,"
                    + "CREATED_BY,"
                    + "UPDATED_BY,"
                    + "UPDATED_AT"
                    + ") VALUES (?,?,?,?,?,?,?)";
                    PreparedStatement pstUserlogin = connection.prepareStatement(instrumentQry);
                    pstUserlogin.setString(1, receiptNumber);
                    pstUserlogin.setString(2, receiptRemarks);
                    pstUserlogin.setString(3, receiptType);
                    pstUserlogin.setString(4, createdAt.toString());
                    pstUserlogin.setString(5, userid.toString());
                    pstUserlogin.setString(6, userid.toString());
                    pstUserlogin.setString(7, createdAt.toString());
                    pstUserlogin.executeUpdate();
                    pstUserlogin.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage().toString());
		}
	}
    public Object getUpdateNonOcrData(JsonObject data, String fullPathImage, String devlogopath, String instrumentNumbers, String companyName, String userloggedName) {
        // TODO Auto-generated method stub
    	
        try {
            String userId = data.get("userid").getAsString();
            ResultSet getUserinformation = this.getExistingUserDataById(userId);
            getUserinformation.next();
            String statuscollection = data.get("statuscollection").getAsString();
            ResultSet dealerCheck = this.checkDealerinfo(data.get("dealerId").getAsString());
            if(!dealerCheck.next() && data.get("saveflag").getAsInt() == 1) {
            	this.storeLogsDataInformation("Dealer information not found with dealer id "+data.get("dealerId").getAsString(),"","OCR",data.get("userid").getAsString(),"info");
                return validationlog.errorValidationResponse(ERROR_CODE_500, "Dealer information not found");
            } else {
            	connection.setAutoCommit(false);
            	String dealerEmail = dealerCheck.getString("DEALER_EMAIL") != null ? dealerCheck.getString("DEALER_EMAIL").toString() : "";
                String receiptNumber = data.get("receiptid").getAsString();
                String paymentMethod = "Non Cash";
                int saveFlag = data.get("saveflag").getAsInt();
                String receiptStatussave = "draft";
	            if(saveFlag == 1) {
	            	receiptStatussave = "issued";
	            }
	            String countrycode = data.get("countrycode").getAsString();
	            Timestamp createdAt =this.getCountryTimestamp(countrycode);
	            /**** FIRST INSERTING INTO HISTORY TABLE **/
	            ResultSet receiptInformation = this.getloadReceiptInformation(receiptNumber);
	            receiptInformation.next();
	            this.getstoreHistoryCollectioninfo(receiptInformation.getString("OCRTYPE").toString(),
	            		receiptInformation.getString("DEALER_MOBILE").toString(),
	            		receiptInformation.getString("MODE_OF_PAYMENT").toString(),
	            		receiptInformation.getString("DEALER_REPRESENTATION").toString(),
	            		receiptInformation.getString("DEALER_REMARKS").toString(),
	            		receiptInformation.getString("COMPANY_CODE").toString(),
	            		receiptInformation.getString("DEALER_ID").toString(),
            			receiptNumber, receiptInformation.getString("RECEIPT_AMOUNT") != null ? receiptInformation.getString("RECEIPT_AMOUNT").toString() :"",receiptInformation.getString("RECEIPT_FILENAME") != null ? receiptInformation.getString("RECEIPT_FILENAME").toString() : "", createdAt, userId, receiptInformation.getString("DEPO_REMARKS") != null ? receiptInformation.getString("DEPO_REMARKS").toString() : "", receiptInformation.getString("DEPO_RECEIPT_STATUS") != null ? receiptInformation.getString("DEPO_RECEIPT_STATUS").toString() : "");
	            
	            /**** FIRST INSERTING INTO HISTORY TABLE **/
                String updateQuery = "UPDATE " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " SET "
                        + "DEALER_ID = '" + data.get("dealerId").getAsString() + "',"
                        + "DEALER_MOBILE = '" + data.get("dealerMobile").getAsString() + "',"
                        + "RECEIPT_AMOUNT = '" + data.get("finalRecepitAmount").getAsString() + "',"
                        + "UPDATED_AT = '" + createdAt.toString() + "',"
                        + "UPDATED_BY = '" + data.get("userid").getAsString() + "',"
                        + "MODE_OF_PAYMENT = '" + paymentMethod + "',"
                        + "DEALER_REPRESENTATION = '" + data.get("dealerRepresentative").getAsString() + "',"
                        + "DEALER_REMARKS = '" + data.get("dealerRemark").getAsString() + "',"
                        + "DEALER_COLLETION_STATUS = '" + receiptStatussave + "', COMPANY_CODE = '"+countrycode+"', "
                        + "OCRTYPE = '" + data.get("ocr").getAsString() + "'"
                        + " WHERE RECEIPT_NUMBER = '" + receiptNumber + "'";
                System.out.println(updateQuery);
                PreparedStatement updateQryPrepare = connection.prepareStatement(updateQuery);
                int updatedRows = updateQryPrepare.executeUpdate();
                updateQryPrepare.close();
                if(updatedRows == 1) {
                	
                		this.insertReciptRemarks(data.get("userid").getAsString(), receiptNumber, data.get("dealerRemark").getAsString(),"SALES", countrycode);
                	    this.updateReceiptIntrumentupdate(receiptNumber, instrumentNumbers);
                        this.getInsertNonOcrInstrumentData(data.get("instruments").toString(), receiptNumber,data.get("userid").getAsString(), countrycode, statuscollection);
                }
                JSONObject responseMap = new JSONObject();
                System.out.println(1);
                if(data.get("saveflag").getAsInt() == 1) {
                    String receptAzureUrl = this.getGenerateOpenpdf(
                    		receiptNumber, 
                    		data.get("userid").getAsString(), 
                    		fullPathImage,
                    		"noncash", 
                    		data.get("finalRecepitAmount").getAsString(), 
                    		data.get("paymentype").getAsString(), 
                    		devlogopath,dealerEmail, countrycode, companyName, userloggedName, data.get("lang").getAsString(),statuscollection);
                    if(receptAzureUrl != null) {
                    	 System.out.println(1.1);
                        this.updateReceiptPathtoReceipt(receptAzureUrl.toString(),receiptNumber);
                        System.out.println(1.2);
                        if(statuscollection.equals("approved") || statuscollection.equals("rejected")) {
                        	/** Ready to Post
                        	 * GET UPDATE THE STATUS FOR THE COLLECTION RECORDS INFORMATION
                        	 On Hold */
                        	String receiptnumber = receiptNumber;
                            String receiptstatus = statuscollection;
                            String receiptremarks = data.get("receiptremarks").getAsString();
                            String userid = data.get("userid").getAsString();
                            JSONObject pushtokenObj = new JSONObject();
                            String messageTokenPush;
                            
                            /**
                             * GET LOAD USER TOKEN INFORMATION TO SEND FIREBASE INFORMATION
                             */
                            Statement stmtRecipt = connection.createStatement();
                    		Boolean deleteStatus = false;
                    		String querySelect = "SELECT "
                					+ "L.USER_PUSH_TOKEN,"
                					+ "R.RECEIPT_AMOUNT,"
                					+ "R.MODE_OF_PAYMENT,"
                					+ "R.DEALER_COLLETION_STATUS,"
                					+ "R.DEPO_RECEIPT_STATUS "
                					+ "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
                							+ "INNER JOIN " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " R ON R.UPDATED_BY = L.USERID "
                							+ "WHERE "
                							+ "L.USER_DELETE_FLAG = "+deleteStatus+" AND L.USER_LEVEL IN (1,2) AND R.RECEIPT_NUMBER = '"+receiptNumber+"' "
                									+ "ORDER BY L.ID DESC";
                			ResultSet tokensListQuery = stmtRecipt.executeQuery(querySelect);
                			tokensListQuery.next();
                			/**
                        	 * GET UPDATE THE STATUS FOR THE COLLECTION RECORDS INFORMATION
                        	 */
                			ArrayList<String> tokensListArray = new ArrayList<String>();
                			if(!tokensListQuery.getString("USER_PUSH_TOKEN").isEmpty()) {
                				tokensListArray.add(tokensListQuery.getString("USER_PUSH_TOKEN").toString());
                			}
                			String depostatus = "";
                            if(receiptstatus.equals("approved")) {
                            	depostatus = "Ready to Post";
                                updateQuery = "UPDATE "
                                		+ "" + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE +" SET "
                                				+ "DEPO_RECEIPT_STATUS = 'Ready to Post', "
                                				+ "DEPO_REMARKS = '" + receiptremarks + "', "
                                						+ "APPROVED_BY = '" + userid + "', DEPO_UPDATED_AT = '" + createdAt +"' "
                                								+ "WHERE RECEIPT_NUMBER = '" + receiptnumber + "' ";
                                messageTokenPush = "Your receipt "+receiptnumber+" with amount of "+tokensListQuery.getString("RECEIPT_AMOUNT").toString()+" got approved successfully";
                                responseMap.put("message", messageTokenPush);
                            } else {
                            	depostatus = "On Hold";
                                updateQuery = "UPDATE "
                                		+ "" + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE +" SET "
                                				+ "DEPO_RECEIPT_STATUS = 'On Hold', "
                                				+ "APPROVED_BY = '"+ userid +"',"
                                						+ "DEPO_REMARKS = '" + receiptremarks + "', "
                                								+ "DEPO_UPDATED_AT = '" + createdAt +"' "
                                										+ "WHERE RECEIPT_NUMBER = '" + receiptnumber + "' ";
                                messageTokenPush = "Your receipt "+receiptnumber+" with amount of "+tokensListQuery.getString("RECEIPT_AMOUNT").toString()+" has been put on hold.";
                                responseMap.put("message", messageTokenPush);
                            }
                            String pushStatus = tokensListQuery.getString("MODE_OF_PAYMENT").toString();
                            pushtokenObj.put("registration_ids", tokensListArray);
                            pushtokenObj.put("priority", "high");
                            pushtokenObj.put("notification", 
                            		new JSONObject().put("title", "Asian Paints Receipt Update")
                            		.put("body", messageTokenPush)
                            		.put("sound", "default")
                            		.put("text", messageTokenPush)
                            		);
                            pushtokenObj.put("data", 
                            		new JSONObject().put("userid", userid)
                            		.put("receiptnumber", receiptnumber)
                            		.put("paymentmode",pushStatus)
                            		.put("status",tokensListQuery.getString("DEPO_RECEIPT_STATUS").toString())
                            				.put("click_action","FLUTTER_NOTIFICATION_CLICK")
                            				);
                            if(tokensListArray.size() > 0) {
                            	pushrepo.send(pushtokenObj);
                            }
                            PreparedStatement updateQryPrepareStatus = connection.prepareStatement(updateQuery);
                            updateQryPrepareStatus.execute();
                            updateQryPrepareStatus.close();
                            stmtRecipt.close();
                            /**
                        	 * GET UPDATE THE STATUS FOR THE COLLECTION RECORDS INFORMATION
                        	 */
                            /**
                             * GET LOAD USER TOKEN INFORMATION TO SEND FIREBASE INFORMATION
                             */
                            this.insertReciptRemarks(data.get("userid").getAsString(), receiptnumber, receiptremarks,"DEPO",countrycode);
                        }
                    } else {
                    	connection.rollback();
                    }
                    responseMap.put("receipturl", receptAzureUrl.toString());
                    this.getdepouserdeviceTokens(data.get("userid").getAsString(),receiptNumber,"Non Cash",receiptStatussave);
                    responseMap.put(STATUS_MESSAGE, SUCCESS);
                } else {
                    responseMap.put("receipturl", "");
                    responseMap.put(STATUS_MESSAGE, "Receipt : "+receiptNumber+", Saved Successfully as Draft.");
                }
                responseMap.put("receiptNumber", receiptNumber);
                responseMap.put("receiptAmount", data.get("finalRecepitAmount").getAsString());
                responseMap.put(STATUS_CODE, SUCCESS_CODE);
                responseMap.put(STATUS_FLAG, 0);
                connection.commit();
                return responseMap;
            }
        } catch (Exception e) {
            // TODO: handle exception
        	try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	System.err.println(e.getMessage().toString());
        	this.storeLogsDataInformation("Something went wrong!, please try again "+e.getMessage().toString(),"","OCR",data.get("userid").getAsString(),"error");
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
    }
    
    private void getstoreHistoryCollectioninfo(String ocr, String dealerMobile, String paymentMethod, String dealerRepresentative, String dealerRemark, String countrycode, String dealerId, String receiptNumber, String finalRecepitAmount, String receptAzureUrl, Timestamp createdAt, String userId, String receiptremarks, String depostatus) {
    	try {
    		String insertQry = "INSERT INTO " + SCHEMA_TABLE + "." + DL_COLLECTION_RECEIPT_HISTORY_DETAILS + " "
                    + "("
                    + "OCRTYPE,"
                    + "DEALER_ID,"
                    + "DEALER_MOBILE,"
                    + "DEALER_EMAIL,"
                    + "RECEIPT_AMOUNT,"
                    + "DATE_OF_RECEVING,"
                    + "RECEIPT_NUMBER,"
                    + "DEALER_REPRESENTATION,"
                    + "DEALER_REMARKS,"
                    + "RECEIPT_FILENAME,"
                    + "DEPO_REMARKS,"
                    + "COMPANY_CODE,"
                    + "MODE_OF_PAYMENT,"
                    + "DEPO_RECEIPT_STATUS,"
                    + "CREATED_AT,"
                    + "CREATED_BY,"
                    + "UPDATED_AT,UPDATED_BY"
                    + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    		System.err.println(insertQry);
            PreparedStatement pstUserlogin = connection.prepareStatement(insertQry);
            pstUserlogin.setString(1, ocr);
            pstUserlogin.setString(2, dealerId);
            pstUserlogin.setString(3, dealerMobile);
            pstUserlogin.setString(4, "");
            pstUserlogin.setString(5, finalRecepitAmount);
            pstUserlogin.setString(6, "");
            pstUserlogin.setString(7, receiptNumber);
            pstUserlogin.setString(8, dealerRepresentative);
            pstUserlogin.setString(9, dealerRemark);
            pstUserlogin.setString(10, receptAzureUrl);
            pstUserlogin.setString(11, receiptremarks);
            pstUserlogin.setString(12, countrycode);
            pstUserlogin.setString(13, paymentMethod);
            pstUserlogin.setString(14, depostatus);
            pstUserlogin.setString(15, createdAt.toString());
            pstUserlogin.setString(16, userId);
            pstUserlogin.setString(17, createdAt.toString());
            pstUserlogin.setString(18, userId);
            System.err.println(pstUserlogin.toString());
            pstUserlogin.executeUpdate();
            pstUserlogin.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage().toString());
		}
    }
    
    private void getdepouserdeviceTokens(String userId, String receiptNumber, String paymentMode, String receiptStatussave) {
		Statement stmt;
		Boolean deleteStatus = true;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.USER_PUSH_TOKEN "
					+ "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "WHERE "
							+ "L.USER_DELETE_FLAG = "+deleteStatus+" AND L.USER_LEVEL = 2 "
									+ "ORDER BY L.ID DESC";
			System.err.println(querySelect);
			ResultSet resultSet = stmt.executeQuery(querySelect);
			/***  Creating arraylist ***/
			ArrayList<String> tokensList = new ArrayList<String>();
			while(resultSet.next()) {
				if(resultSet.getString("USER_PUSH_TOKEN") != null)
				tokensList.add(resultSet.getString("USER_PUSH_TOKEN").toString());
			}
			System.err.println(tokensList.toString());
			/***  Creating arraylist ***/
			stmt.close();
			JSONObject pushtokenObj = new JSONObject();
            pushtokenObj.put("registration_ids", tokensList);
            pushtokenObj.put("priority", "high");
            pushtokenObj.put("notification", 
            		new JSONObject().put("title", "New Receipt Added")
            		.put("body", "User "+userId+" Added New Receipt:"+receiptNumber)
            		.put("sound", "default")
            		.put("text", "User "+userId+" Added New Receipt:"+receiptNumber)
            		);
            pushtokenObj.put("data", 
            		new JSONObject().put("userid", userId)
            		.put("receiptnumber", receiptNumber)
            		.put("paymentmode",paymentMode)
            		.put("status",receiptStatussave)
            		.put("click_action","FLUTTER_NOTIFICATION_CLICK") 
            		);
            if(tokensList.size() > 0) pushrepo.send(pushtokenObj);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage().toString());
		}
	}
    
    private void storeInstrumentHistoryinformation(String receiptNumber, String instrumentamount, String instrumentdate, String instrumentnumber, String instrumentremarks, String instrumenttype, String instrumentimageurl, String instrumentdateofreceving, String instrumentbankaccountnumber, String instrumentbankcode, String instrumentmicrno, String instrumentpayeename, String instrumentid, String countrycode, Timestamp createdAt, String userid) {
    	try {
    		String instrumentQry = "INSERT INTO " + SCHEMA_TABLE + "." + DL_RECEIPT_INSTRUMENT_HISTORY_DETAILS + " "
                    + "(RECEIPT_ID,"
                    + "INSTRUMENT_AMOUNT,"
                    + "INSTRUMENT_DATE,"
                    + "INSTRUMENT_NUMBER,"
                    + "INSTRUMENT_REMARKS,"
                    + "INSTRUMENT_TYPE,"
                    + "INSTRUMENT_IMAGE_URL,"
                    + "INSTRUMENT_DATE_OF_RECEVING,"
                    + "INSTRUMENT_BANK_ACCOUNT_NUMBER,"
                    + "INSTRUMENT_BANK_CODE,"
                    + "INSTRUMENT_BANK_BRANCH,"
                    + "INSTRUMENT_MICR_NUMBER,"
                    + "INSTRUMENT_PAYEE_NAME,"
                    + "INSTRUMENT_ID,"
                    + "COMPANY_CODE,"
                    + "CREATED_AT,"
                    + "CREATED_BY,"
                    + "UPDATED_BY,"
                    + "UPDATED_AT"
                    + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement pstUserlogin = connection.prepareStatement(instrumentQry);
                    pstUserlogin.setString(1, receiptNumber);
                    pstUserlogin.setString(2, instrumentamount);
                    pstUserlogin.setString(3, instrumentdate);
                    pstUserlogin.setString(4, instrumentnumber);
                    pstUserlogin.setString(5, instrumentremarks);
                    pstUserlogin.setString(6, instrumenttype);
                    pstUserlogin.setString(7, instrumentimageurl);
                    pstUserlogin.setString(8, instrumentdateofreceving);
                    pstUserlogin.setString(9, instrumentbankaccountnumber);
                    pstUserlogin.setString(10, instrumentbankcode);
                    pstUserlogin.setString(11, "");
                    pstUserlogin.setString(12, instrumentmicrno);
                    pstUserlogin.setString(13, instrumentpayeename);
                    pstUserlogin.setString(14, instrumentid);
                    pstUserlogin.setString(15, countrycode.toString());
                    pstUserlogin.setString(16, createdAt.toString());
                    pstUserlogin.setString(17, userid.toString());
                    pstUserlogin.setString(18, userid.toString());
                    pstUserlogin.setString(19, createdAt.toString());
                    pstUserlogin.executeUpdate();
                    pstUserlogin.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage().toString());
		}
    }
    
    public Object getInsertNonOcrInstrumentData(String instrumentstring, String receiptNumber, String userid, String countrycode, String statuscollection) {
        try {
            JSONArray instrumentArray = new JSONArray(instrumentstring.toString());
            for( int in=0; in<instrumentArray.length(); in++ ) {
            	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
//            	Timestamp createdAt = this.getCountryTimestamp(countrycode);
                JSONObject instrumentObj = new JSONObject(instrumentArray.get(in).toString());
                String instrumentamount = instrumentObj.get("instrument_amount").toString();
                String instrumentdate = instrumentObj.get("instrument_date").toString();
                String instrumentnumber = instrumentObj.get("instrument_number").toString();
                String instrumentremarks = instrumentObj.get("instrument_remarks").toString();
                String instrumenttype = instrumentObj.get("instrument_type").toString();
                String instrumentimageurl = instrumentObj.get("instrument_image_url").toString();
                String instrumentdateofreceving = instrumentObj.get("instrument_date_of_receving").toString();
                String instrumentbankaccountnumber = instrumentObj.get("instrument_bank_account_number").toString();
                String instrumentbankcode = instrumentObj.get("instrument_bank_code").toString();
                String instrumentmicrno = instrumentObj.get("instrument_micr_no").toString();
                String instrumentpayeename = instrumentObj.get("instrument_payee_name").toString();
                String instrumentid = instrumentObj.get("instrumentid").toString();
                String genId = instrumentObj.get("genId").toString();
                /*
                 * GET STORE INTO HISTORY TABLE FIRST...
                 */
//                if(statuscollection.equals("approved") || statuscollection.equals("rejected")) {
                if(!instrumentObj.get("id").toString().isEmpty()) {
                	Statement stmtReceipt = connection.createStatement();
                	String instrumentSql = "SELECT x.* FROM " + SCHEMA_TABLE + "." + INSTRUMENT_TABLE + " x "
                            + "WHERE RECEIPT_ID = '" + receiptNumber + "' AND ID = "+instrumentObj.get("id").toString()+"";
                	System.out.println(instrumentSql);
                    ResultSet instrumentresultSet = stmtReceipt.executeQuery(instrumentSql);
                    instrumentresultSet.next();
                    this.storeInstrumentHistoryinformation(receiptNumber,instrumentresultSet.getString("INSTRUMENT_AMOUNT").toString(),instrumentresultSet.getString("INSTRUMENT_DATE").toString(),instrumentresultSet.getString("INSTRUMENT_NUMBER").toString(),instrumentresultSet.getString("INSTRUMENT_REMARKS").toString(),instrumentresultSet.getString("INSTRUMENT_TYPE").toString(),instrumentresultSet.getString("INSTRUMENT_IMAGE_URL").toString(),instrumentresultSet.getString("INSTRUMENT_DATE_OF_RECEVING").toString(),instrumentresultSet.getString("INSTRUMENT_BANK_ACCOUNT_NUMBER").toString(),instrumentresultSet.getString("INSTRUMENT_BANK_CODE").toString(),instrumentresultSet.getString("INSTRUMENT_MICR_NUMBER").toString(),instrumentresultSet.getString("INSTRUMENT_PAYEE_NAME").toString(),instrumentresultSet.getString("INSTRUMENT_ID").toString(),countrycode,createdAt,userid);
                }
                	
                	
//                }
                /*
                 * GET STORE INTO HISTORY TABLE FIRST...
                 */
                
                if(instrumentObj.get("id").toString().isEmpty()) {
                	if(instrumentid.isEmpty()) {
                    	ResultSet resultSet = this.getMaxInstIdData();
        	            int instrumentTokenumber;
        	            if( resultSet.next() && resultSet.getString("maxId") != null ) {
        	            	String maxIdInstr = resultSet.getString("maxId").toString();
        	            	instrumentTokenumber = Integer.parseInt(maxIdInstr)+1;
        	            	
        	            } else {
        	            	instrumentTokenumber = 1;
        	            }
        	            
        	            instrumentid = this.generateInstrumentReceiptNumber(countrycode,instrumentTokenumber);	
                    }
                	String instrumentQry = "INSERT INTO " + SCHEMA_TABLE + "." + INSTRUMENT_TABLE + " "
                            + "(RECEIPT_ID,"
                            + "INSTRUMENT_AMOUNT,"
                            + "INSTRUMENT_DATE,"
                            + "INSTRUMENT_NUMBER,"
                            + "INSTRUMENT_REMARKS,"
                            + "INSTRUMENT_TYPE,"
                            + "INSTRUMENT_IMAGE_URL,"
                            + "CREATED_AT,"
                            + "INSTRUMENT_DATE_OF_RECEVING,"
                            + "INSTRUMENT_BANK_ACCOUNT_NUMBER,"
                            + "INSTRUMENT_BANK_CODE,"
                            + "INSTRUMENT_BANK_BRANCH,"
                            + "INSTRUMENT_MICR_NUMBER,"
                            + "INSTRUMENT_PAYEE_NAME,"
                            + "INSTRUMENT_ID,"
                            + "CREATED_BY,"
                            + "INSTRUMENT_GENID,"
                            + "COMPANY_CODE,"
                            + "UPDATED_BY,"
                            + "UPDATED_AT"
                            + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		                    PreparedStatement pstUserlogin = connection.prepareStatement(instrumentQry);
		                    pstUserlogin.setString(1, receiptNumber);
		                    pstUserlogin.setString(2, instrumentamount);
		                    pstUserlogin.setString(3, instrumentdate);
		                    pstUserlogin.setString(4, instrumentnumber);
		                    pstUserlogin.setString(5, instrumentremarks);
		                    pstUserlogin.setString(6, instrumenttype);
		                    pstUserlogin.setString(7, instrumentimageurl);
		                    pstUserlogin.setString(8, createdAt.toString());
		                    pstUserlogin.setString(9, instrumentdateofreceving);
		                    pstUserlogin.setString(10, instrumentbankaccountnumber);
		                    pstUserlogin.setString(11, instrumentbankcode);
		                    pstUserlogin.setString(12, "");
		                    pstUserlogin.setString(13, instrumentmicrno);
		                    pstUserlogin.setString(14, instrumentpayeename);
		                    pstUserlogin.setString(15, instrumentid);
		                    pstUserlogin.setString(16, userid.toString());
		                    pstUserlogin.setString(17, genId);
		                    pstUserlogin.setString(18, countrycode.toString());
		                    pstUserlogin.setString(19, userid.toString());
		                    pstUserlogin.setString(20, createdAt.toString());
		                    pstUserlogin.executeUpdate();
		                    pstUserlogin.close();
                } else {
                	PreparedStatement updateQryPrepare;
                	String updateQry = " UPDATE " + SCHEMA_TABLE + "." + INSTRUMENT_TABLE + " I "
                            + "SET "
                            + " UPDATED_AT = '" + createdAt.toString() + "' "
                            				+ ", RECEIPT_ID = '"+receiptNumber+"' "
                            						+ ", INSTRUMENT_AMOUNT = '"+instrumentamount+"' "
                            						+ ", INSTRUMENT_DATE = '"+instrumentdate+"' "
                            						+ ", INSTRUMENT_NUMBER = '"+instrumentnumber+"' "
                            						+ ", INSTRUMENT_REMARKS = '"+instrumentremarks+"' "
                            						+ ", INSTRUMENT_TYPE = '"+instrumenttype+"' "
                            						+ ", INSTRUMENT_IMAGE_URL = '"+instrumentimageurl+"' "
                            						+ ", INSTRUMENT_DATE_OF_RECEVING = '"+instrumentdateofreceving+"' "
                            						+ ", INSTRUMENT_BANK_ACCOUNT_NUMBER = '"+instrumentbankaccountnumber+"' "
                            						+ ", INSTRUMENT_BANK_CODE = '"+instrumentbankcode+"' "
                            						+ ", INSTRUMENT_MICR_NUMBER = '"+instrumentmicrno+"' "
                            						+ ", INSTRUMENT_PAYEE_NAME = '"+instrumentpayeename+"' "
                            						+ ", INSTRUMENT_ID = '"+instrumentid+"' "
                            						+ ", UPDATED_BY = '"+userid.toString()+"' "
                            						+ ", INSTRUMENT_GENID = '"+genId+"' "
                            						+ ", COMPANY_CODE = '"+countrycode.toString()+"' "
                                    + "WHERE I.ID = '"+instrumentObj.get("id").toString()+"'";
                    updateQryPrepare = connection.prepareStatement(updateQry);
                    updateQryPrepare.executeUpdate();
                    updateQryPrepare.close();
                }
                
            }
        } catch (JSONException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("NON OCR Error:-"+e.toString(),"","OCR",userid.toString(),"error");
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
        return null;
    }
    
    private Object getloadPreviousInstrumentsData(JsonObject data, String fullPathImage, String devlogopath, String companyName, String userloggedName) {
		try {
			Object dataNonOcr;
			JSONArray instrumentArray = new JSONArray(data.get("instruments").toString());
			String instrumentNumbers = "";
			for(int in=0;in<instrumentArray.length();in++) {
				JSONObject instrumentObj = new JSONObject(instrumentArray.get(in).toString());
				instrumentNumbers += instrumentObj.get("id").toString();
				if(in<(instrumentArray.length()-1)) {
					instrumentNumbers += ",";
				}
			}
	        if(data.get("receiptid").getAsString().isEmpty()) {
	        	dataNonOcr = this.getSaveNonOcrData(data, fullPathImage, devLogoPath,instrumentNumbers,companyName, userloggedName);
	        } else {
	        	dataNonOcr = this.getUpdateNonOcrData(data, fullPathImage, devLogoPath,instrumentNumbers,companyName, userloggedName);
	        }
	        return dataNonOcr;
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    private ResultSet loadfontInformation() {
		Statement stmt;
		Boolean isTrash = false;
		try {
			stmt = connection.createStatement();
			String countrySql = "SELECT DI.* FROM "+SCHEMA_TABLE+"."+DL_FONT_COLLECTION_URL+" DI WHERE DI.IS_TRASH = "+isTrash+"";
			ResultSet resultSet = stmt.executeQuery(countrySql);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    @SuppressWarnings("unused")
    private String loadPdfhtmlnonCash(String receiptId, String userId, String fullPathImage, String finalReceiptAmount, String paymentTyper, String devlogopath, String countrycode, String lang) {
        Object pdfObjectdata = this.getloadPdfData(receiptId, userId);
        JSONObject mainObject;
        try {
            mainObject = new JSONObject( pdfObjectdata.toString() );
            String dealerId = mainObject.get("dealerId").toString();
            String dealerName = mainObject.get("dealerName").toString();
            String createdAt =  (mainObject.get("createdAt") != null ? jsonValit.getDateTimestamp(mainObject.get("createdAt").toString()) : "");
            String representative = mainObject.get("representative").toString();
            String receiptNumber = mainObject.get("receiptNumber").toString();
            String dealerRemarks = mainObject.get("dealerRemarks").toString();
            int finalAmountinstrument = 0;
            ResultSet userAddressDetails = this.getLoginUserCountryData(userId, countrycode);
            userAddressDetails.next();
            String pattern = userAddressDetails.getString("NUMBER_FORMATE") != null ? userAddressDetails.getString("NUMBER_FORMATE").toString() : "";
            String userNamesales = userAddressDetails.getString("USERNAME") != null ? userAddressDetails.getString("USERNAME").toString() : "";
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
            Boolean decimalPoint = false;
            List<Instrumentmodel> listInstruments = this.getLoadInstrumentdata( mainObject.get("receiptNumber").toString() );
            String s = userAddressDetails.getString("RECEIPT_EMAIL") != null ? userAddressDetails.getString("RECEIPT_EMAIL").toString() : "";
        	String mail1 = null;
        	if(s.length() > 25) {
        		mail1 = s.substring(0,25)+"<br/>"+s.substring(25,s.length());
        	} else {
        		mail1 = s;
        	}
        	ResultSet metafontData = this.loadfontInformation();
        	
            String htmlContext = "<!DOCTYPE html PUBLIC \"//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
            		+ "<html lang=\"h\" xmlns=\"http://www.w3.org/1999/xhtml\">"
                        + "<head>";
    		String fontFamilydata="";
    		while(metafontData.next()) {
    			htmlContext+= "<link rel=\"stylesheet\" href=\""+metafontData.getString("FONT_URL").toString()+"\" />";
    			fontFamilydata += "'"+metafontData.getString("FONT_NAME").toString()+"', ";
    		}
    		htmlContext += "<meta charset=\"UTF-8\">"
					    + "<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> "
					    + "<style>"
					    + "body {"
					    + "font-family:"+fontFamilydata+" serif;"
					    + "}"
					    + ""
					    + "</style>"
					    + "</head>"
                        + "<body>"
                        + "<table style='width: 645px;margin: 30px auto;border:2px solid #431A80;border-collapse:collapse; font-size:11px;'>" ;
    		if(lang.equals("ar")) {
    			htmlContext += "<tr style='border-bottom:2px solid #431A80;'>"
    					+ "<td style='width:290px;padding:20px;border-bottom:2px solid #431A80;' ></td>"
    					+ "<td style='width:65px;text-align:center;border-bottom:2px solid #431A80;'>"
                        + "<img style='width:100px;margin: 0 auto;' alt='Asian Paints Logo' src='"+(userAddressDetails.getString("COMPANY_LOGO") != null ? userAddressDetails.getString("COMPANY_LOGO").toString() : "")+"'>"
                        + "</td>"
                        
                        + "<td style='width:290px;padding:20px;border-bottom:2px solid #431A80;'>"
                        + "<table style='width:100%;border:0;text-align: right;' >"
                        + "<tr>"
                        + "<td colspan='3' style='text-align: left; font-weight: bold; color: #431A80; text-transform: uppercase;'>"
                        + (userAddressDetails.getString("COMPANY_NAME_IN_RECEIPT") != null ? userAddressDetails.getString("COMPANY_NAME_IN_RECEIPT").toString() : "")+" "
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style='vertical-align:top;width:100%;'>"+( userAddressDetails.getString("RECEIPT_ADDRESS") != null ? userAddressDetails.getString("RECEIPT_ADDRESS").toString() : "")+",<br/>"
                		+ ""+(userAddressDetails.getString("RECEIPT_ADDRESS_LINE_2") != null ? userAddressDetails.getString("RECEIPT_ADDRESS_LINE_2").toString() : "")+",<br/>"
                		+ ""+(userAddressDetails.getString("RECEIPT_ADDRESS_LINE_3") != null ? userAddressDetails.getString("RECEIPT_ADDRESS_LINE_3").toString() : "")+"."
                		+ "</td>"
                        + "<td style='vertical-align:top;'>:</td>"
                        + "<td style='vertical-align:top;'>"+langinfo.getloadLanguage("ar").get("address").toString()+"</td>"
                        + "</tr>"
                        + "<tr><td>+"+(userAddressDetails.getString("RECEIPT_PHONE_NUMBER") != null ? userAddressDetails.getString("RECEIPT_PHONE_NUMBER").toString() : "")+"</td><td>:</td><td>Phone </td></tr>"
                        + "<tr>"
                        + "<td>+"+(userAddressDetails.getString("RECEIPT_FAX") != null ? userAddressDetails.getString("RECEIPT_FAX").toString() : "")+"</td>"
                        + "<td>:</td>"
                        + "<td>"+langinfo.getloadLanguage("ar").get("fax").toString()+"</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>"+(mail1 != null ? mail1.toString() :"")+ "</td>"
                        + "<td>:</td>"
                        + "<td>"+langinfo.getloadLanguage("ar").get("email").toString()+"</td>"
                        + "</tr>";
			            if(userAddressDetails.getString("RECEIPT_CRNO") != null) {
	        	 htmlContext += "<tr>"
	        			 + "<td>"+userAddressDetails.getString("RECEIPT_CRNO").toString()+"</td>"
	                     + "<td>:</td>"
	                     + "<td>"+langinfo.getloadLanguage("ar").get("crno").toString()+"</td>"
	                     + "</tr>";	
			            }
                        htmlContext += "</table>"
                        + "</td>"
                        
                        + "</tr>";
                        
    		} else {
    			
    			htmlContext += "<tr style='border-bottom:2px solid #431A80;'><td style='width:290px;padding:20px;border-bottom:2px solid #431A80;'>"
                        + "<table style='width:100%;margin: 0 auto;' >"
                        + "<tr>"
                        + "<td colspan='3' style='text-align: left; font-weight: bold; color: #431A80; text-transform: uppercase;'>"
                        + (userAddressDetails.getString("COMPANY_NAME_IN_RECEIPT") != null ? userAddressDetails.getString("COMPANY_NAME_IN_RECEIPT").toString() : "")+" "
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style='vertical-align:top;'>"+langinfo.getloadLanguage(lang).get("crno").toString()+"</td>"
                        + "<td style='vertical-align:top;'>:</td>"
                        + "<td style='vertical-align:top;'>"+( userAddressDetails.getString("RECEIPT_ADDRESS") != null ? userAddressDetails.getString("RECEIPT_ADDRESS").toString() : "")+",<br/>"
                        		+ ""+(userAddressDetails.getString("RECEIPT_ADDRESS_LINE_2") != null ? userAddressDetails.getString("RECEIPT_ADDRESS_LINE_2").toString() : "")+",<br/>"
                        		+ ""+(userAddressDetails.getString("RECEIPT_ADDRESS_LINE_3") != null ? userAddressDetails.getString("RECEIPT_ADDRESS_LINE_3").toString() : "")+"."
                        		+ "</td>"
                        + "</tr>"
                        + "<tr><td>Phone </td><td>:</td><td>+"+(userAddressDetails.getString("RECEIPT_PHONE_NUMBER") != null ? userAddressDetails.getString("RECEIPT_PHONE_NUMBER").toString() : "")+"</td></tr>"
                        + "<tr>"
                        + "<td>"+langinfo.getloadLanguage(lang).get("fax").toString()+"</td>"
                        + "<td>:</td>"
                        + "<td>+"+(userAddressDetails.getString("RECEIPT_FAX") != null ? userAddressDetails.getString("RECEIPT_FAX").toString() : "")+"</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>"+langinfo.getloadLanguage(lang).get("email").toString()+"</td>"
                        + "<td>:</td>"
                        + "<td>"+(mail1 != null ? mail1.toString() : "")+ "</td>"
                        + "</tr>";
			            if(userAddressDetails.getString("RECEIPT_CRNO") != null) {
	        	 htmlContext += "<tr>"
	                     + "<td>"+langinfo.getloadLanguage(lang).get("crno").toString()+"</td>"
	                     + "<td>:</td>"
	                     + "<td>"+userAddressDetails.getString("RECEIPT_CRNO") != null ? userAddressDetails.getString("RECEIPT_CRNO").toString() : ""+"</td>"
	                     + "</tr>";	
			            }
                        htmlContext += "</table>"
                        + "</td>"
                        + "<td style='width:65px;text-align:center;border-bottom:2px solid #431A80;'>"
                        + "<img style='width:100px;margin: 0 auto;' alt='Asian Paints Logo' src='"+(userAddressDetails.getString("COMPANY_LOGO") != null ? userAddressDetails.getString("COMPANY_LOGO").toString() : "")+"'>"
                        + "</td>"
                        + "<td style='width:290px;padding:20px;border-bottom:2px solid #431A80;' ></td></tr>";
    		}
    		
                        htmlContext += "<tr>"
                        + "<td></td>"
                        + "<td><div style='background:#431a80;-webkit-print-color-adjust: exact;  border-radius:50px; width:100px;padding:5px;    color:#fff; position:relative;top:-15px;font-weight:bold;text-align:center;vertical-align: middle; margin: 0 auto;'>RECEIPT</div></td>"
                        + "<td></td>"
                        + "</tr>";
                        if(lang.equals("ar")) {
                        htmlContext += "<tr>"
                        + "<td style='padding:20px;'>"
                        + "<table style='border:0' >"
                        + "<tr><td>" + dealerId + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("customercode").toString()+"</td></tr>"
                        + "<tr><td>" + dealerName + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("customername").toString()+"</td></tr>"
                        + "<tr><td>" + paymentTyper + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("paymentmode").toString()+"</td></tr>"
                        + "</table>"
                        + "</td><td></td>"
                        + "<td style='text-align:right;padding:20px;' >"
                        + "<table style='width:95%;border:0;float:right;'>"
                        + "<tr><td>"+receiptNumber+"</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("receiptNumber").toString()+"</td></tr>"
                        + "<tr><td>" + createdAt + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("date").toString()+"</td></tr>"
                        + "</table>"
                        + " </td></tr>";
                        } else {
                        	htmlContext += "<tr>"
                                    + "<td style='padding:20px;'>"
                                    + "<table style='border:0' >"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("customercode").toString()+"</td><td>:</td><td>" + dealerId + "</td></tr>"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("customername").toString()+"</td><td>:</td><td>" + dealerName + "</td></tr>"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("paymentmode").toString()+"</td><td>:</td><td>" + paymentTyper + "</td></tr>"
                                    + "</table>"
                                    + "</td><td></td>"
                                    + "<td style='text-align:right;padding:20px;' >"
                                    + "<table style='width:95%;border:0;float:right;'>"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("receiptNumber").toString()+"</td><td>:</td><td>"+receiptNumber+"</td></tr>"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("date").toString()+"</td><td>:</td><td>" + createdAt + "</td></tr>"
                                    + "</table>"
                                    + " </td></tr>";
                        }
                        
                        htmlContext += "<tr><td style='width:100%;' colspan='3'>"
                        + "<table style='width: 95%;margin: 0px auto;border:0.5px solid #431a80;padding:5px;border-collapse:collapse;'> ";
                        if(lang.equals("ar")) {
                        htmlContext += "<tr>"
                        + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("no").toString()+"</th>"
                        + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("instrumenttype").toString()+"</th>"
                        + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("bankname").toString()+"</th>"
                        + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("instrumentno").toString()+"</th>"
                        + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("amount").toString()+"</th>"
                        + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("micr").toString()+"</th></tr>";
                        } else {
                        	htmlContext += "<tr>"
                                    + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("no").toString()+"</th>"
                                    + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("instrumenttype").toString()+"</th>"
                                    + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("bankname").toString()+"</th>"
                                    + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("instrumentno").toString()+"</th>"
                                    + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("amount").toString()+"</th>"
                                    + "<th style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'>"+langinfo.getloadLanguage(lang).get("micr").toString()+"</th></tr>";
                        }
                        for( int inst = 0;inst < listInstruments.size(); inst++) {
                            String instrumentAmount = listInstruments.get(inst).getInstrument_amount();
                            if(instrumentAmount.contains(".")) {
                                decimalPoint = true;
                                instrumentAmount = instrumentAmount.substring(0, instrumentAmount.indexOf("."));
                            }
                            Integer sumAmount = Integer.parseInt(instrumentAmount.replaceAll(",", ""));
                            finalAmountinstrument += sumAmount;
                            htmlContext += "<tr>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'>" + listInstruments.get(inst).getSqNumber() + "</td>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'>" + listInstruments.get(inst).getInstrument_type() + "</td>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'>" + listInstruments.get(inst).getInstrument_bank_code() + "</td>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'>" + listInstruments.get(inst).getInstrument_number() + "</td>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'>" + userAddressDetails.getString("CURRENCY").toString() + " " + listInstruments.get(inst).getInstrument_amount() + "</td>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'>" + listInstruments.get(inst).getInstrument_micr_no() + "</td>"
                                    + "</tr>";
                            if(lang.equals("ar")) {
                            	htmlContext += "<tr><td colspan=3 style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'> <span>"+ listInstruments.get(inst).getInstrument_remarks() +"</span></td><td colspan=3><span style='color:blue;'>"+langinfo.getloadLanguage(lang).get("instrumentremarks").toString()+"</span> </td></tr>";
                            } else {
                            	htmlContext += "<tr><td colspan=6 style='font-weight:normal; color: #431a80;border:0.5px solid #431a80;padding:5px;'> <span style='color:blue;'>"+langinfo.getloadLanguage(lang).get("instrumentremarks").toString()+" </span> : <span>"+ listInstruments.get(inst).getInstrument_remarks() +"</span></td></tr>";
                            }
                            
                        }
                        if(lang.equals("ar")) {
                        htmlContext += "<tr>"
                                + "<td style='border:0.5px solid #431a80;padding:5px;' colspan=5>" + userAddressDetails.getString("CURRENCY").toString()+ " " + finalReceiptAmount + "</td>"
                                + "<td style='border:0.5px solid #431a80;padding:5px;'> "+langinfo.getloadLanguage(lang).get("total").toString()+" </td>"
                                + "</tr>";
                        } else {
                        	htmlContext += "<tr>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;'> "+langinfo.getloadLanguage(lang).get("total").toString()+" </td>"
                                    + "<td style='border:0.5px solid #431a80;padding:5px;' colspan=5>" + userAddressDetails.getString("CURRENCY").toString()+ " " + finalReceiptAmount + "</td>"
                                    + "</tr>";
                        }
                        if(lang.equals("ar")) {
                        htmlContext += "<tr><td style='border:0.5px solid #431a80;padding:5px;' colspan='7'><p>"+dealerRemarks+" : <span style='color:blue;'>"+langinfo.getloadLanguage(lang).get("receiptremarks").toString()+"</span></p> &nbsp;</td></tr>";
                        } else {
                        	htmlContext += "<tr><td style='border:0.5px solid #431a80;padding:5px;' colspan='7'> <p><span style='color:blue;'>"+langinfo.getloadLanguage(lang).get("receiptremarks").toString()+" </span>: "+dealerRemarks+"</p> &nbsp;</td></tr>";
                        }
                        htmlContext += "</table></td></tr>"
                        + "<tr><td style='padding:20px;width:45%;' >";
                        if(lang.equals("ar")) {
                        htmlContext += "<table style='border:0' >"
                        + "<tr><td>" + (userAddressDetails.getString("CURRENCY") != null ? userAddressDetails.getString("CURRENCY").toString() : "") + " " + finalReceiptAmount + "</td><td>:</td><td>"+langinfo.getloadLanguage(lang).get("finalreceiptamount").toString()+"</td></tr>"
                        + "<tr><td>" + userNamesales + "</td><td>:</td><td>"+langinfo.getloadLanguage(lang).get("issuedby").toString()+"</td></tr>"
                        + "<tr><td>" + createdAt + "</td><td>:</td><td>"+langinfo.getloadLanguage(lang).get("date").toString()+"</td></tr>"
                        + "</table>";
                        } else {
                        	htmlContext += "<table style='border:0' >"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("finalreceiptamount").toString()+"</td><td>:</td><td>" + (userAddressDetails.getString("CURRENCY") != null ? userAddressDetails.getString("CURRENCY").toString() : "") + " " + finalReceiptAmount + "</td></tr>"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("issuedby").toString()+"</td><td>:</td><td>" + userNamesales + "</td></tr>"
                                    + "<tr><td>"+langinfo.getloadLanguage(lang).get("date").toString()+"</td><td>:</td><td>" + createdAt + "</td></tr>"
                                    + "</table>";
                        }
                        htmlContext += "</td><td></td><td style='text-align:right;padding:20px;width:55%;' >";
                        if(lang.equals("ar")) {
                        htmlContext += "<table style='border:0;float:right;' >"
                        + "<tr><td style='vertical-align:top;text-align:left'>"+representative+"</td><td style='vertical-align:top;'>: </td><td style='vertical-align:top;'>"+langinfo.getloadLanguage(lang).get("customerrepresentative").toString()+"</td></tr>"
                        + "</table>";
                        } else {
                        	htmlContext += "<table style='border:0;float:right;' >"
                                    + "<tr><td style='vertical-align:top;'>"+langinfo.getloadLanguage(lang).get("customerrepresentative").toString()+"</td><td style='vertical-align:top;'>: </td><td style='vertical-align:top;text-align:left'>"+representative+"</td></tr>"
                                    + "</table>";
                        }
                        htmlContext += "</td></tr>";
                        if(lang.equals("ar")) {
                        htmlContext += "<tr><td style='padding:20px;width:100%;'> "+(userAddressDetails.getString("DISCLAIMER") != null ? userAddressDetails.getString("DISCLAIMER").toString() : "")+" </td><td colspan=2> "+langinfo.getloadLanguage(lang).get("disclimer").toString()+" </td></tr>";
                        } else {
                        	htmlContext += "<tr><td style='padding:20px;width:100%;' colspan='3'>"+langinfo.getloadLanguage(lang).get("disclimer").toString()+" <br> "+(userAddressDetails.getString("DISCLAIMER") != null ? userAddressDetails.getString("DISCLAIMER").toString() : "")+"</td></tr>";
                        }
                        htmlContext += "<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>"
                        + "</table></body></html>";
                        System.out.println(htmlContext);
                        return htmlContext;
                        
        } catch (JSONException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("Error in generating the PDF :"+e.getMessage().toString(),"","receipt",userId,"error");
            return null;
        }
    }
    
    public String getGenerateOpenpdf(String receiptId, String userId, String fullPathImage, String paymentType, String finalReceiptAmount, String paymentTyper, String devlogopath, String dealerEmail, String countrycode, String companyName, String userloggedName, String lang, String checkStatus) {
        String loadHtmlPdf = this.loadPdfhtmlnonCash(receiptId, userId, fullPathImage, finalReceiptAmount, paymentTyper, devlogopath, countrycode, lang);
        int randonNumber = random.nextInt(1000);
        String PDF_FOLDER_PATH = receiptId+randonNumber;
        String PDF_OUTPUT = PDF_FOLDER_PATH+".pdf";//fullPathImage+PDF_FOLDER_PATH+".pdf";
        String createdAting = jsonValit.getStartDate(new Date());
        File outputPdf = new File(PDF_OUTPUT);
        Document document;
        try {
            document = Jsoup.parse(loadHtmlPdf.toString(), "UTF-8");
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            OutputStream os = new FileOutputStream(outputPdf);
            String baseUri = FileSystems.getDefault().getPath("/").toUri().toString();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withFile(outputPdf);
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(document), baseUri);
            builder.run();
            File pdffullpath = new File(PDF_OUTPUT);
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
              .addFormDataPart("fileData",PDF_OUTPUT,RequestBody.create(MediaType.parse("application/octet-stream"),pdffullpath))
              .addFormDataPart("fileContainer","aplms")
              .addFormDataPart("fileLoc","dealer_receipt/"+receiptId+"/"+createdAting+"/"+PDF_FOLDER_PATH)
              .build();
            Request requestcfr = new Request.Builder()
                    .url(CONTENTSTORAGE)
                    .method("POST", body)
                    .build();
            Response responsebht = client.newCall(requestcfr).execute();
            String jsonData = responsebht.body().string();
            JsonObject dataResponse = new Gson().fromJson(jsonData.toString(), JsonObject.class);
            String mailBody = "Dear Customer,"
            		+ "<br/>"
            		+ "<br/>"
            		+ "Please find the enclosed receipt "+receiptId+" dated "+jsonValit.getStartDate(new Date())+" created in your favour.<br/>"
            		+ "<br/>"
            		+ "Regards,<br/>"
            		+ userloggedName+"<br/>"
            		+ companyName+".";
            if(checkStatus.equals("approved") && !checkStatus.equals("rejected")) {
            	 mailling.sendMail(mailBody, dealerEmail,PDF_OUTPUT," Receipt "+receiptId+" Issued on "+jsonValit.getStartDate(new Date()),PDF_FOLDER_PATH+".pdf");
            }
            if(jsonData.toString().length() > 0) { pdffullpath.delete(); }
             return dataResponse.get("assetUrl").getAsString();
        } catch (Exception e) {
        	// TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("Error in generating the PDF :"+e.getMessage(),"","receipt",userId,"error");
            return null;
        }
    }
    
    @SuppressWarnings("unused")
    public List<Instrumentmodel> getLoadInstrumentdata(String receiptNumber) {
        Statement stmt;
        try {
        	Boolean deleteFlag = false;
            stmt = connection.createStatement();
            String instrumentSql = "SELECT x.* FROM " + SCHEMA_TABLE + "." + INSTRUMENT_TABLE + " x "
                    + "WHERE RECEIPT_ID = '" + receiptNumber + "'"
                            + " AND DELETE_FLAG = "+deleteFlag+" ";
            ResultSet instrumentresultSet = stmt.executeQuery(instrumentSql);
            List<Instrumentmodel> instrumentlist = new ArrayList<>();
            int sqNumber = 1;
            while(instrumentresultSet.next()) {
            	String ID = instrumentresultSet.getString("ID").toString();
                String receiptId = instrumentresultSet.getString("RECEIPT_ID").toString();
                String InstrumentAmount = instrumentresultSet.getString("INSTRUMENT_AMOUNT").toString();
                String instrumentDateOfReceving = instrumentresultSet.getString("INSTRUMENT_DATE_OF_RECEVING").toString();
                String instrumentAccountNumber = instrumentresultSet.getString("INSTRUMENT_BANK_ACCOUNT_NUMBER").toString();
                String instrumentBankCode = instrumentresultSet.getString("INSTRUMENT_BANK_CODE").toString();
                String instrumentbankBranch = instrumentresultSet.getString("INSTRUMENT_BANK_BRANCH").toString();
                String instrumentRemarks = instrumentresultSet.getString("INSTRUMENT_REMARKS").toString();
                String instrumenType = instrumentresultSet.getString("INSTRUMENT_TYPE").toString();
                String instrumentNumber = instrumentresultSet.getString("INSTRUMENT_NUMBER").toString();
                String instrumentDate = instrumentresultSet.getString("INSTRUMENT_DATE").toString();
                String instrumentDateofReceving = instrumentresultSet.getString("INSTRUMENT_DATE_OF_RECEVING").toString();
                String instrumentImage = instrumentresultSet.getString("INSTRUMENT_IMAGE_URL").toString();
                String instrumentmicrno = instrumentresultSet.getString("INSTRUMENT_MICR_NUMBER").toString();
                String instrumentpayeename = instrumentresultSet.getString("INSTRUMENT_PAYEE_NAME").toString();
                String instrumentid = instrumentresultSet.getString("INSTRUMENT_ID").toString();
                String genId = instrumentresultSet.getString("INSTRUMENT_GENID").toString();
                String INSTRUMNETSTATUS = instrumentresultSet.getString("INSTRUMNET_STATUS") != null ? instrumentresultSet.getString("INSTRUMNET_STATUS").toString() : "";
                instrumentlist.add(new Instrumentmodel(
                		ID,
                		sqNumber, 
                		receiptId, 
                		InstrumentAmount, 
                		instrumentBankCode, 
                		instrumentbankBranch, 
                		instrumentRemarks, 
                		instrumenType, 
                		instrumentNumber, 
                		instrumentDate, 
                		instrumentDateofReceving, 
                		instrumentImage,
                		instrumentAccountNumber,
                		instrumentmicrno,
                		instrumentpayeename,
                		instrumentid,
                		genId,INSTRUMNETSTATUS
                		));
                sqNumber++;
            }
            stmt.close();
            return instrumentlist;            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("getLoadInstrumentdata:"+e.getMessage(),"","receipt",receiptNumber,"error");
            return null;   
        }
    }
    
    private ResultSet getLoginUserCountryData(String username, String countrycode) {
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String querySelect = "SELECT "
                    + "L.*,"
                    + "C.RECEIPT_ADDRESS,"
                    + "C.RECEIPT_ADDRESS_LINE_2,"
                    + "C.RECEIPT_ADDRESS_LINE_3,"
                    + "C.RECEIPT_PHONE_NUMBER,"
                    + "C.RECEIPT_FAX,"
                    + "C.RECEIPT_EMAIL,"
                    + "C.RECEIPT_CRNO,"
                    + "C.CURRENCY,"
                    + "C.NUMBER_FORMATE,"
                    + "C.COMPANY_NAME_IN_RECEIPT,"
                    + "C.COMPANY_LOGO,"
                    + "C.DISCLAIMER "
                    + "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
                            + "INNER JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " "
                                    + "C ON C.COMPANY_CODE IN ('"+countrycode+"') "
                                            + " WHERE L.USERID = '" + username + "' ORDER BY ID DESC";
            ResultSet resultSet = stmt.executeQuery(querySelect);
            stmt.close();
            return resultSet;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    private ResultSet getLoadDealerData(String receiptid) {
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String receiptSql = "SELECT x.OCRTYPE,"
                    + "x.RECEIPT_NUMBER,"
                    + "d.DEALER_ID,"
                    + "d.DEALER_NAME,"
                    + "x.CREATED_AT,"
                    + "x.DEALER_REPRESENTATION,"
                    + "x.DEALER_REMARKS,"
                    + "x.DATE_OF_RECEVING,"
                    + "x.MODE_OF_PAYMENT,"
                    + "x.DEALER_MOBILE,"
                    + "x.DEALER_COLLETION_STATUS,"
                    + "x.RECEIPT_AMOUNT AS FINAL_RECEIPT_AMOUNT,"
                    + "x.RECEIPT_AMOUNT"
                    + " FROM " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " x"
                    + " INNER JOIN " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " d ON d.DEALER_ID = x.DEALER_ID "
                    + " WHERE x.RECEIPT_NUMBER ='" + receiptid + "'";
            this.storeLogsDataInformation("getLoadDealerData:"+receiptSql.toString(),"","receipt","","info");
            ResultSet resultSet = stmt.executeQuery(receiptSql);
            stmt.close();
            return resultSet;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    private Object getloadPdfData(String receiptid, String userid) {
        // TODO Auto-generated method stub
        try {
            JSONObject dealerReceiptInfo = new JSONObject();
            ResultSet resultSet = this.getLoadDealerData(receiptid);
            if(resultSet.next()) {
            	dealerReceiptInfo.put("dealerId", (resultSet.getString("DEALER_ID") != null ? resultSet.getString("DEALER_ID").toString() : ""));
                dealerReceiptInfo.put("dealerName", (resultSet.getString("DEALER_NAME") != null ? resultSet.getString("DEALER_NAME").toString() : ""));
                dealerReceiptInfo.put("receiptNumber", resultSet.getString("RECEIPT_NUMBER").toString());
                dealerReceiptInfo.put("createdAt", (resultSet.getString("CREATED_AT") != null ? resultSet.getString("CREATED_AT").toString() : ""));
                dealerReceiptInfo.put("representative", resultSet.getString("DEALER_REPRESENTATION").toString());
                dealerReceiptInfo.put("dealerRemarks", resultSet.getString("DEALER_REMARKS").toString());
                return dealerReceiptInfo;
            } else {
            	return null;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    /* * UPDATE RECEIPT URL INTO THE RECEIPT TALBE WITH RESPECT RECEIPT ID. * */
    private void updateReceiptPathtoReceipt(String receptAzureUrl, String receiptNumber) {
        
        PreparedStatement updateQryPrepare;
        try {
            String receptAzurelink = receptAzureUrl.toString();
            String receiptDataNumber = receiptNumber.toString();
            String updateQry = " UPDATE " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " C "
                    + "SET "
                    + "C.RECEIPT_FILENAME = '"+receptAzurelink+"' "
                    + "WHERE C.RECEIPT_NUMBER = '"+receiptDataNumber+"'";
            updateQryPrepare = connection.prepareStatement(updateQry);
            updateQryPrepare.execute();
            updateQryPrepare.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /* * UPDATE RECEIPT INSTRUMENT STATUS TO TRASH . * */
    private Boolean updateReceiptIntrumentupdate(String receiptId, String instrumentNumbers) {
        PreparedStatement updateQryPrepare;
        try {
        	if(instrumentNumbers.length()>0) {
        		if(instrumentNumbers.endsWith(",")) {
        			instrumentNumbers = instrumentNumbers.substring(0,instrumentNumbers.length()-1);
        		}
        		String receiptDataNumber = receiptId.toString();
                String updateQry = " DELETE FROM " + SCHEMA_TABLE + "." + INSTRUMENT_TABLE + " I "
                        + "WHERE I.RECEIPT_ID = '"+receiptDataNumber+"' AND I.ID NOT IN ("+instrumentNumbers+")";
                updateQryPrepare = connection.prepareStatement(updateQry);
                updateQryPrepare.execute();
                updateQryPrepare.close();	
        	}
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
    
    private ResultSet getExistingUserDataById(String userId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.* FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L WHERE L.USERID = '"+userId+"' ORDER BY ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    private ResultSet checkDealerinfo(String dealerId) {
	    Statement stmt;
        try {
            stmt = connection.createStatement();
            String querySelect = "SELECT L.* FROM " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " L WHERE L.DEALER_ID = '"+dealerId+"' ";
            ResultSet resultSet = stmt.executeQuery(querySelect);
            stmt.close();
            return resultSet;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}
    
    private ResultSet getMaxIdData() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT MAX(R.Id) AS maxId FROM " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " R";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
    
    private String generateReceiptNumber(Integer receiptNumber, String userCompanyCode) {
		// TODO Auto-generated method stub
		String x = userCompanyCode.concat(receiptNumber.toString());
	    return x;
	}
    
    private void storeLogsDataInformation(String messageType, String excutionTime, String category, String userId, String msgType) {
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
            pstUserlogin.setString(1, messageType.toString());
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
    
    private ResultSet getMaxInstIdData() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT IFNULL(I.ID,0) AS maxId FROM " + SCHEMA_TABLE + "." + INSTRUMENT_TABLE + " I ORDER BY I.ID DESC LIMIT 1";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    private String generateInstrumentReceiptNumber(String azureUser, Integer instrumentTokenumber) {
		// TODO Auto-generated method stub
    	String x = "INN".concat(azureUser + instrumentTokenumber.toString());
	    return x;
	}
    
    private ResultSet getLoggeduserinfo(String username) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.USER_DELETE_FLAG, C.COMPANY_NAME_IN_RECEIPT,L.USERNAME "
					+ "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "INNER JOIN "+SCHEMA_TABLE+"."+COUNTRIES_MASTER+" C ON C.COMPANY_CODE = SUBSTR_REGEXPR('[^,]+' IN L.COMPANY_ID FROM 1 OCCURRENCE 1 )"
							+ "WHERE "
							+ "L.USERID = '"+username+"' "
									+ "ORDER BY L.ID DESC LIMIT 1";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
