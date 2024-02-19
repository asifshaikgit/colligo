package Repository;

import static Constants.AsianConstants.COLLECTION_RECEIPT_TABLE;
import static Constants.AsianConstants.CONTENTSTORAGE;
import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DEALER_MOBILE;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.DL_COLLECTION_RECEIPT_HISTORY_DETAILS;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.DL_FONT_COLLECTION_URL;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import Services.saleservice;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Salesrepository implements saleservice {
	
	private Jsonresponse jsonValit;
	private ValidationCtrl validationlog;
	private Pushnotificationrepo pushrepo;
	private Connection connection;
	Random random;
	private Mailrepository mailling;
	private Languageslist langinfo;
	
	@SuppressWarnings("static-access")
	public Salesrepository() {
		// TODO Auto-generated constructor stub
		jsonValit = new Jsonresponse();
		validationlog = new ValidationCtrl();
		connection = new Database().Connection();
		random = new Random(); 
		pushrepo = new Pushnotificationrepo();
		mailling = new Mailrepository();
		langinfo = new Languageslist();
	}
	
	public Object cashreceiptflowmanage(JSONObject salesObject, String fullPathImage, String devlogopath) {
	    try {
	    	
	    	ResultSet usercheckResult = this.getLoggeduserinfo(salesObject.getString("userid").toString());
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
				if(salesObject.get("receiptid").toString().isEmpty()) {
					System.out.println("save");
	                return this.saveCollectionData(salesObject, fullPathImage, devlogopath,companyName,userloggedName);
	            } else {
	            	System.out.println("update");
	                return this.updateCashflowdata(salesObject, fullPathImage, devlogopath,companyName,userloggedName);
	            }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
			}
			
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
	}
	
	@SuppressWarnings("unused")
    public Object saveCollectionData(JSONObject salesObject, String fullPathImage, String devlogopath, String companyName, String userloggedName) {
		// TODO Auto-generated method stub
//		Timestamp createdAt = new Timestamp(System.currentTimeMillis());
		Integer userLoggedid = 0;
		try {
			String userId = salesObject.getString("userid");
		    ResultSet getUserinformation = this.getExistingUserDataById(userId);
		    getUserinformation.next();
		    int userLevel = getUserinformation.getInt("USER_LEVEL");
		    String userCompanyCode = getUserinformation.getString("COMPANY_ID").toString();
		    ResultSet dealerCheck = this.checkDealerinfo(salesObject.getString("dealerId").toString());
		    if(!dealerCheck.next() && salesObject.getInt("saveflag") == 1) {
		        return validationlog.errorValidationResponse(ERROR_CODE_500, "Dealer information not found");
		    } else {
		    	connection.setAutoCommit(false);
		    	String dealerEmail;
		    	if(salesObject.getString("dealerId").toString().isEmpty()) {
		    		dealerEmail = "";
		    	} else {
		    		dealerEmail = dealerCheck.getString("DEALER_EMAIL") != null ? dealerCheck.getString("DEALER_EMAIL").toString() : "";
		    	}
	            ResultSet resultSet = this.getMaxIdData();
	            resultSet.next();
	            userLoggedid = resultSet.getInt("maxId")+1;
	            String countrycode = salesObject.has("countrycode") ? salesObject.getString("countrycode").toString() : "";
	            String receiptNumber = this.generateReceiptNumber(userLoggedid, countrycode);
	            String paymentMethod = "Cash";
	            Timestamp createdAt = this.getCountryTimestamp(countrycode);
	            System.out.println(countrycode);
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
	            		+ "DATE_OF_RECEVING,"
	            		+ "UPDATED_AT,"
	            		+ "DEPO_RECEIPT_STATUS,"
	            		+ "COMPANY_CODE,UPDATED_BY"
	            		+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	            
	            int saveFlag = salesObject.getInt("saveflag");
	            String receiptStatussave = "draft";
	            if(saveFlag == 1) {
	            	receiptStatussave = "issued";
	            }
	            
	            PreparedStatement pstUserlogin = connection.prepareStatement(insertQry);
	            pstUserlogin.setString(1, salesObject.getString("dealerId"));
	            pstUserlogin.setString(2, salesObject.getString("dealerMobile"));
	            pstUserlogin.setString(3, "");
	            pstUserlogin.setString(4, salesObject.getString("receivedAmount"));
	            pstUserlogin.setString(5, createdAt.toString());
	            pstUserlogin.setString(6, userId);
	            pstUserlogin.setString(7, paymentMethod);
	            pstUserlogin.setString(8, receiptNumber);
	            pstUserlogin.setString(9, salesObject.getString("otptoken"));
	            pstUserlogin.setString(10, salesObject.getString("dealerRepresentative"));
	            pstUserlogin.setString(11, salesObject.getString("dealerRemark"));
	            pstUserlogin.setString(12, receiptStatussave);
	            pstUserlogin.setString(13, salesObject.getString("ocr"));
	            pstUserlogin.setString(14, salesObject.getString("dateofreceving"));
	            pstUserlogin.setString(15, createdAt.toString());
	            pstUserlogin.setString(16, "");
	            pstUserlogin.setString(17, countrycode);
	            pstUserlogin.setString(18, userId);
	            pstUserlogin.executeUpdate();
	            pstUserlogin.close();
	            JSONObject responseMap = new JSONObject();
	            System.err.println(23);
	            if(salesObject.getInt("saveflag") == 1) {
	                String getReceiptReport = "";
	                getReceiptReport = this.getGenerateOpenpdf(
	                		receiptNumber, 
	                		salesObject.getString("userid"), 
	                		fullPathImage,"cash", 
	                		salesObject.getString("receivedAmount").toString(), 
	                		salesObject.getString("paymentype"), 
	                		devlogopath, dealerEmail, countrycode, salesObject.getString("dealerRemark").toString(),companyName, userloggedName, salesObject.getString("lang").toString());
	                ArrayList<String> dealerMobile = this.getDealerMobileInfoData(salesObject.getString("dealerId"));
	                String dealerMobileNum = "";
	                if(!dealerMobile.isEmpty()) {
	                    dealerMobileNum = dealerMobile.get(0);
	                }
	                if(getReceiptReport != null) {
                        this.updateReceiptPathtoReceipt(getReceiptReport.toString(),receiptNumber);
                    } else {
                    	connection.rollback();
                    }
	                this.getdepouserdeviceTokens(userId,receiptNumber,"Cash",receiptStatussave);
                    responseMap.put("receipturl", getReceiptReport.toString());
                    responseMap.put(STATUS_MESSAGE, SUCCESS);
                } else {
                    responseMap.put("receipturl", "");
                    responseMap.put(STATUS_MESSAGE, "Receipt : "+receiptNumber+", Saved Successfully as Draft.");
                }
	            responseMap.put("receiptNumber", receiptNumber);
	            responseMap.put("receiptAmount", salesObject.getString("receivedAmount"));
	            responseMap.put(STATUS_CODE, SUCCESS_CODE);
	            responseMap.put(STATUS_FLAG, 0);
	            connection.commit();
	            return responseMap;
		    }
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.storeLogsDataInformation(e.getMessage().toString(),"","receipt","","error");
			return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
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
			ResultSet resultSet = stmt.executeQuery(querySelect);
			ArrayList<String> tokensList =new ArrayList<String>(); //Creating arraylist   
			while(resultSet.next()) {
				tokensList.add(resultSet.getString("USER_PUSH_TOKEN") != null ? resultSet.getString("USER_PUSH_TOKEN").toString() : "");
			}
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
            pushtokenObj.put("data", new JSONObject().put("userid", userId).put("receiptnumber", receiptNumber).put("paymentmode",paymentMode).put("status",receiptStatussave).put("click_action","FLUTTER_NOTIFICATION_CLICK"));
            
            if(tokensList.size() > 0) pushrepo.send(pushtokenObj);
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object getloadPdfData(String receiptid, String userid) {
        // TODO Auto-generated method stub
        try {
        	this.storeLogsDataInformation("PDF Generate Collection Id:"+receiptid.toString(),"","receipt",userid,"info");
            JSONObject dealerReceiptInfo = new JSONObject();
            ResultSet resultSet = this.getLoadDealerData(receiptid);
            resultSet.next();
            dealerReceiptInfo.put("dealerId", resultSet.getString("DEALER_ID").toString());
            dealerReceiptInfo.put("dealerName", resultSet.getString("DEALER_NAME") != null ? resultSet.getString("DEALER_NAME").toString() : "");
            dealerReceiptInfo.put("receiptNumber", resultSet.getString("RECEIPT_NUMBER") != null ? resultSet.getString("RECEIPT_NUMBER").toString() : "");
            dealerReceiptInfo.put("createdAt", ( resultSet.getString("CREATED_AT") != null ? resultSet.getString("CREATED_AT").toString() : "") );
            dealerReceiptInfo.put("representative", resultSet.getString("DEALER_REPRESENTATION") != null ? resultSet.getString("DEALER_REPRESENTATION").toString() : "");
            dealerReceiptInfo.put("dealerRemarks", resultSet.getString("DEALER_REMARKS") != null ? resultSet.getString("DEALER_REMARKS").toString() : "");
            dealerReceiptInfo.put("dateofReceving", resultSet.getString("DATE_OF_RECEVING") != null ? resultSet.getString("DATE_OF_RECEVING").toString() : "");
            return dealerReceiptInfo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("getloadPdfData:"+e.getMessage(),"","receipt",userid,"error");
        }
        return null;
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
                    + "x.RECEIPT_AMOUNT"
                    + " FROM " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " x"
                    + " INNER JOIN " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " d ON d.DEALER_ID = x.DEALER_ID "
                    + " WHERE x.RECEIPT_NUMBER ='" + receiptid + "'";
            this.storeLogsDataInformation("getLoadDealerData:"+receiptSql.toString(),"","receipt",receiptid,"error");
            ResultSet resultSet = stmt.executeQuery(receiptSql);
            stmt.close();
            return resultSet;
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
	private String loadPdfhtmlcash(String receiptId, String userId, String fullPathImage, String finalReceiptAmount, String paymentTyper, String devlogopath, String countrycode, String dealerRemark, String lang) {
		Object pdfObjectdata = this.getloadPdfData(receiptId, userId);
        JSONObject mainObject;
        try {
            ResultSet userAddressDetails = this.getLoginUserCountryData(userId, countrycode);
            userAddressDetails.next();
            String userNamesales = userAddressDetails.getString("USERNAME").toString();
            String s = userAddressDetails.getString("RECEIPT_EMAIL").toString();
            mainObject = new JSONObject( pdfObjectdata.toString() );
            String dealerId = mainObject.get("dealerId").toString();
            String dealerName = mainObject.get("dealerName").toString();
            String createdAt =  (mainObject.get("createdAt") != null ? jsonValit.getDateTimestamp(mainObject.get("createdAt").toString()) : "");
            String representative = mainObject.getString("representative").toString();
            String receiptNumber = mainObject.get("receiptNumber").toString();
            String dateofReceving = mainObject.get("dateofReceving").toString();
            String mailString = "";
        	for(int k=0;k<s.length();k++) {
        		mailString += s.charAt(k);
        		if(k == 25) { mailString += "<br/>"; }
        	}
        	ResultSet metafontData = this.loadfontInformation();
        	
    		String htmlContext = "<!DOCTYPE html PUBLIC \"//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
    + "<html lang=\"hi\" xmlns=\"http://www.w3.org/1999/xhtml\">"
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
					    + "<table style='width: 645px;margin: 30px auto;border:2px solid #431A80;border-collapse:collapse; font-size:11px;'>";
					    if(lang.equals("ar")) {
					    	htmlContext += "<tr style='border-bottom:2px solid #431A80;text-align: right;'><td style='width:290px;padding:20px;border-bottom:2px solid #431A80;' > &nbsp; </td>"
					    	+ "<td style='width:65px;text-align:center;border-bottom:2px solid #431A80;'>"
	                        + "<img style='width:100%;margin: 0 auto;' alt='Asian Paints Logo' src='"+(userAddressDetails.getString("COMPANY_LOGO") != null ? userAddressDetails.getString("COMPANY_LOGO").toString() : "")+"'>"
	                        + "</td>"
					    	+"<td style='width:290px;padding:20px;border-bottom:2px solid #431A80;'>"
                        + "<table style='width:100%;margin: 0 auto;' >"
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
                        + "</tr><tr>"
                        + "<td>+"+(userAddressDetails.getString("RECEIPT_PHONE_NUMBER") != null ? userAddressDetails.getString("RECEIPT_PHONE_NUMBER").toString() : "")+"</td>"
                        + "<td>:</td>"
                        + "<td>"+langinfo.getloadLanguage("ar").get("phone").toString()+"</td>"
                        		+ "</tr>"
                        + "<tr>"
                        + "<td>+"+(userAddressDetails.getString("RECEIPT_FAX") != null ? userAddressDetails.getString("RECEIPT_FAX").toString() : "")+"</td>"
                        + "<td>:</td>"
                        + "<td>"+langinfo.getloadLanguage("ar").get("fax").toString()+"</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td>"+mailString.toString()+"</td>"
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
                        + "</td></tr>";
					    } else {
					    	htmlContext += "<tr style='border-bottom:2px solid #431A80;'><td style='width:290px;padding:20px;border-bottom:2px solid #431A80;'>"
			                        + "<table style='border:0' >"
			                        + "<tr>"
			                        + "<td colspan='3' style='text-align: left; font-weight: bold; color: #431A80; text-transform: uppercase;'>"
			                        + (userAddressDetails.getString("COMPANY_NAME_IN_RECEIPT") != null ? userAddressDetails.getString("COMPANY_NAME_IN_RECEIPT").toString() : "")+" "
			                        + "</td>"
			                        + "</tr>"
			                        + "<tr>"
			                        + "<td style='vertical-align:top;'>"+langinfo.getloadLanguage(lang).get("address").toString()+"</td>"
			                        + "<td style='vertical-align:top;'>:</td>"
			                        + "<td style='vertical-align:top;'>"+( userAddressDetails.getString("RECEIPT_ADDRESS") != null ? userAddressDetails.getString("RECEIPT_ADDRESS").toString() : "")+",<br/>"
			                        		+ ""+(userAddressDetails.getString("RECEIPT_ADDRESS_LINE_2") != null ? userAddressDetails.getString("RECEIPT_ADDRESS_LINE_2").toString() : "")+",<br/>"
			                        		+ ""+(userAddressDetails.getString("RECEIPT_ADDRESS_LINE_3") != null ? userAddressDetails.getString("RECEIPT_ADDRESS_LINE_3").toString() : "")+"."
			                        		+ "</td>"
			                        + "</tr>"
			                        + "<tr><td>"+langinfo.getloadLanguage(lang).get("phone").toString()+"</td><td>:</td><td>+"+(userAddressDetails.getString("RECEIPT_PHONE_NUMBER") != null ? userAddressDetails.getString("RECEIPT_PHONE_NUMBER").toString() : "")+"</td></tr>"
			                        + "<tr>"
			                        + "<td>"+langinfo.getloadLanguage(lang).get("fax").toString()+"</td>"
			                        + "<td>:</td>"
			                        + "<td>+"+(userAddressDetails.getString("RECEIPT_FAX") != null ? userAddressDetails.getString("RECEIPT_FAX").toString() : "")+"</td>"
			                        + "</tr>"
			                        + "<tr>"
			                        + "<td>"+langinfo.getloadLanguage(lang).get("email").toString()+"</td>"
			                        + "<td>:</td>"
			                        + "<td>"+mailString.toString()+"</td>"
			                        + "</tr>";
						            if(userAddressDetails.getString("RECEIPT_CRNO") != null) {
				        	 htmlContext += "<tr>"
				                     + "<td>"+langinfo.getloadLanguage(lang).get("crno").toString()+"</td>"
				                     + "<td>:</td>"
				                     + "<td>"+userAddressDetails.getString("RECEIPT_CRNO").toString()+"</td>"
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
					    + "<td><div style='background:#431a80;-webkit-print-color-adjust: exact;  border-radius:50px; width:100px;padding:5px;color:#fff; position:relative;top:-15px;font-weight:bold;text-align:center;vertical-align: middle; margin: 0 auto;'>RECEIPT</div></td>"
					    + "<td></td>"
					    + "</tr>"
					    + "<tr>"
					    + "<td style='padding:20px;width:45%;' >";
					    if(lang.equals("ar")) {
					    	htmlContext += "<table style='width:100%;' >"
								    + "<tr>"
								    + "<td>" + dealerId + "</td>"
								    + "<td>:</td>"
								    + "<td>"+langinfo.getloadLanguage("ar").get("customercode").toString()+"</td>"
								    + "</tr>"
								    + "<tr><td>" + dealerName + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("customername").toString()+"</td></tr>"
								    + "<tr><td>" + paymentTyper + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("paymentmode").toString()+"</td></tr>"
								    + "</table>";
					    } else {
					    	htmlContext += "<table style='width:100%;' >"
								    + "<tr>"
								    + "<td>"+langinfo.getloadLanguage(lang).get("customercode").toString()+"</td>"
								    + "<td>:</td>"
								    + "<td>" + dealerId + "</td>"
								    + "</tr>"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("customername").toString()+"</td><td>:</td><td>" + dealerName + "</td></tr>"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("paymentmode").toString()+"</td><td>:</td><td>" + paymentTyper + "</td></tr>"
								    + "</table>";
					    }
					    htmlContext += "</td>"
					    + "<td colspan=2 style='padding:0px;width:45%;padding:15px;' >";
					    if(lang.equals("ar")) {
					    	htmlContext += "<table style='border:0;float:right;'>"
					    + "<tr><td>"+receiptNumber+"</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("receiptNumber").toString()+"</td></tr>"
					    + "<tr><td>" + dateofReceving + "</td><td>:</td><td>"+langinfo.getloadLanguage("ar").get("date").toString()+"</td></tr>"
					    + "</table>";
					    } else {
					    	htmlContext += "<table style='border:0;float:right;'>"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("receiptNumber").toString()+"</td><td>:</td><td>"+receiptNumber+"</td></tr>"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("date").toString()+"</td><td>:</td><td>" + dateofReceving + "</td></tr>"
								    + "</table>";
					    }
					    htmlContext += " </td></tr>"
					    + "<tr><td style='width:100%;' colspan='3'><table></table></td></tr>"
					    + "<tr><td style='padding:20px;' >";
					    if(lang.equals("ar")) {
					    	htmlContext += "<table style='border:0' >"
					    + "<tr><td>" + userAddressDetails.getString("CURRENCY").toString() + " " +finalReceiptAmount + "</td><td>:</td><td>"+langinfo.getloadLanguage(lang).get("finalreceiptamount").toString()+"</td></tr>"
					    + "<tr><td>" + userNamesales + "</td><td>:</td><td>"+langinfo.getloadLanguage(lang).get("issuedby").toString()+"</td></tr>"
					    + "<tr><td>" + createdAt + "</td><td>:</td><td>"+langinfo.getloadLanguage(lang).get("date").toString()+"</td></tr>"
					    + "</table>";
					    } else {
					    	htmlContext += "<table style='border:0' >"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("finalreceiptamount").toString()+"</td><td>:</td><td>" + userAddressDetails.getString("CURRENCY").toString() + " " +finalReceiptAmount + "</td></tr>"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("issuedby").toString()+"</td><td>:</td><td>" + userNamesales + "</td></tr>"
								    + "<tr><td>"+langinfo.getloadLanguage(lang).get("date").toString()+"</td><td>:</td><td>" + createdAt + "</td></tr>"
								    + "</table>";
					    }
					    htmlContext += "</td><td style='width:10%;'></td><td style='text-align:right;padding:20px;width:45%;' >";
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
					    	htmlContext += "<tr><td style='padding:20px;width:100%;text-align:right;' colspan='2'>"+ dealerRemark +"</td><td><span style='color:blue;'>"+langinfo.getloadLanguage(lang).get("receiptremarks").toString()+"</span></td></tr>"
								    + "<tr><td style='padding:20px;width:100%;text-align:right;' colspan='2'>"+userAddressDetails.getString("DISCLAIMER").toString()+"</td><td>"+langinfo.getloadLanguage(lang).get("disclimer").toString()+"</td></tr>";
					    } else {
					    	htmlContext += "<tr><td style='padding:20px;width:100%;' colspan='3'><span style='color:blue;'>"+langinfo.getloadLanguage(lang).get("receiptremarks").toString()+"</span> : "+ dealerRemark +"</td></tr>"
								    + "<tr><td style='padding:20px;width:100%;' colspan='3'>"+langinfo.getloadLanguage(lang).get("disclimer").toString()+" <br> "+userAddressDetails.getString("DISCLAIMER").toString()+"</td>    </tr>";
					    }
					    
					    htmlContext += "<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>"
					    + "</table></body>"
					    + "</html>";
					    System.err.println(htmlContext);
					   return htmlContext;
        } catch (JSONException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                    + "AC.CURRENCY,"
                    + "C.NUMBER_FORMATE,"
                    + "C.COMPANY_NAME_IN_RECEIPT,"
                    + "C.COMPANY_LOGO,"
                    + "C.DISCLAIMER "
                    + "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
                            + "INNER JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " "
                                    + "C ON C.COMPANY_CODE IN ('"+countrycode+"') "
                                    + "INNER JOIN " +SCHEMA_TABLE+ "." + COUNTRIES_MASTER + " AC "
                                    + (!countrycode.toString().isEmpty() ? "ON AC.COMPANY_CODE IN ('"+countrycode+"') " : "ON AC.COMPANY_CODE IN ('"+countrycode+"') ")
                                            + "WHERE L.USERID = '" + username + "' ORDER BY ID DESC";
            ResultSet resultSet = stmt.executeQuery(querySelect);
            stmt.close();
            return resultSet;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
	
	private String getGenerateOpenpdf(String receiptId, String userId, String fullPathImage, String paymentType, String finalReceiptAmount, String paymentTyper, String devlogopath, String dealerEmail, String countrycode, String dealerRemark, String companyName, String userloggedName, String lang) {
    	this.storeLogsDataInformation("fullPathImage :"+fullPathImage.toString(),"","receipt",userId,"info");
        String loadHtmlPdf = this.loadPdfhtmlcash(receiptId, userId, fullPathImage, finalReceiptAmount, paymentTyper, devlogopath, countrycode, dealerRemark, lang);
        int randonNumber = random.nextInt(1000);
        String PDF_FOLDER_PATH = receiptId+randonNumber;
//        String PDF_OUTPUT = fullPathImage+PDF_FOLDER_PATH+".pdf";
        String createdAting = jsonValit.getStartDate(new Date());
        Document document;
        try {
        	File outputPdf = File.createTempFile(PDF_FOLDER_PATH, ".pdf", null); //new File(PDF_FOLDER_PATH+".pdf"); //
        	System.out.println("File path: "+outputPdf.getAbsolutePath());
            document = Jsoup.parse(loadHtmlPdf.toString(),"UTF-8");         
            document.outputSettings().syntax(Document.OutputSettings.Syntax.html);
            OutputStream os = new FileOutputStream(outputPdf);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withFile(outputPdf);
            builder.withW3cDocument(new W3CDom().fromJsoup(document),null);
            builder.toStream(os);
            builder.run();
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
              .addFormDataPart("fileData",PDF_FOLDER_PATH+".pdf",RequestBody.create(MediaType.parse("application/octet-stream"),outputPdf))
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
//            mailling.sendMail(mailBody, dealerEmail,PDF_FOLDER_PATH, ".pdf"," Receipt "+receiptId+" Issued on "+jsonValit.getStartDate(new Date()),PDF_FOLDER_PATH+".pdf");
//            if(jsonData.toString().length() > 0) {
            outputPdf.delete();
            	outputPdf.deleteOnExit();
//            }
            return dataResponse.get("assetUrl").getAsString();
        } catch (Exception e) {
//             TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("PDF Generate Collection Id:"+e.toString(),"","receipt",userId,"error");
            return null;
        }
    }
	
	protected ResultSet checkDealerinfo(String dealerId) {
	    Statement stmt;
        try {
            stmt = connection.createStatement();
            String querySelect = "SELECT L.* FROM " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " L WHERE L.DEALER_ID = '"+dealerId+"' ";
            ResultSet resultSet = stmt.executeQuery(querySelect);
            return resultSet;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}
	protected ResultSet getMaxIdData() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT MAX(R.Id) AS maxId FROM " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " R";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	protected String generateReceiptNumber(Integer receiptNumber, String userCompanyCode) {
		// TODO Auto-generated method stub
		String x = userCompanyCode.concat(receiptNumber.toString());
	    return x;
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

	/* * UPDATE THE RECEIPT INFORMATION WITH RESPECT RECEIPT ID. * */
	@SuppressWarnings("unused")
	protected Object updateCashflowdata(JSONObject salesObject, String fullPathImage, String devlogopath, String companyName, String userloggedName) {
	 // TODO Auto-generated method stub
//		Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        try {
            String userId = salesObject.getString("userid");
            ResultSet getUserinformation = this.getExistingUserDataById(userId);
            getUserinformation.next();
			int userLevel = getUserinformation.getInt("USER_LEVEL");
            ResultSet dealerCheck = this.checkDealerinfo(salesObject.getString("dealerId").toString());
            dealerCheck.next();
            String dealerEmail = dealerCheck.getString("DEALER_EMAIL") != null ? dealerCheck.getString("DEALER_EMAIL").toString() : "";
                String receiptNumber = salesObject.getString("receiptid").toString();
                String paymentMethod = "Cash";
                int saveFlag = salesObject.getInt("saveflag");
	            
	            String statuscollection = salesObject.getString("statuscollection").toString();
	            connection.setAutoCommit(false);
	            /**** FIRST INSERTING INTO HISTORY TABLE **/
	            ResultSet receiptInformation = this.getloadReceiptInformation(receiptNumber);
	            receiptInformation.next();
	            
	            String receiptStatussave = "draft";
	            if(saveFlag == 1) {
	            	receiptStatussave = "issued";
	            }
	            String countrycode = salesObject.has("countrycode") ? salesObject.getString("countrycode").toString() : "";
	            String receiptremarks = salesObject.has("receiptremarks") ? salesObject.getString("receiptremarks").toString() : "";
	            Timestamp createdAt = this.getCountryTimestamp(countrycode);
	            System.out.println("current time "+createdAt);
                this.getstoreHistoryCollectioninfo(receiptInformation.getString("OCRTYPE").toString(),
                		receiptInformation.getString("DEALER_MOBILE").toString(),
                		receiptInformation.getString("MODE_OF_PAYMENT").toString(),
                		receiptInformation.getString("DEALER_REPRESENTATION").toString(),
                		receiptInformation.getString("DEALER_REMARKS").toString(),
                		receiptInformation.getString("COMPANY_CODE").toString(),
                		receiptInformation.getString("DEALER_ID").toString(),
            			receiptNumber, receiptInformation.getString("RECEIPT_AMOUNT") != null ? receiptInformation.getString("RECEIPT_AMOUNT").toString() : "",receiptInformation.getString("RECEIPT_FILENAME") != null ? receiptInformation.getString("RECEIPT_FILENAME").toString() : "", createdAt, userId, receiptInformation.getString("DATE_OF_RECEVING") != null ? receiptInformation.getString("DATE_OF_RECEVING").toString(): "", receiptInformation.getString("DEPO_REMARKS") != null ? receiptInformation.getString("DEPO_REMARKS").toString() : "", receiptInformation.getString("DEPO_RECEIPT_STATUS") != null ? receiptInformation.getString("DEPO_RECEIPT_STATUS").toString() : "");
                /**** FIRST INSERTING INTO HISTORY TABLE **/
                String updateQuery = "UPDATE " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " SET "
                        + "DEALER_ID = '" + salesObject.getString("dealerId").toString() + "',"
                        + "DEALER_MOBILE = '" + salesObject.getString("dealerMobile").toString() + "',"
                        + "RECEIPT_AMOUNT = '" + salesObject.getString("receivedAmount").toString() + "',"
                        + "UPDATED_AT = '" + createdAt.toString() + " ',"
                        + "UPDATED_BY = '" + userId + "',"
                        + "MODE_OF_PAYMENT = '"+paymentMethod+"', "
                        + "DATE_OF_RECEVING = '"+ salesObject.getString("dateofreceving").toString() +"', "
                        + "OTP = '" + salesObject.getString("otptoken").toString() + "',"
                        + "DEALER_REPRESENTATION = '" + salesObject.getString("dealerRepresentative").toString() + "',"
                        + "DEALER_REMARKS = '" + salesObject.getString("dealerRemark").toString() + "',"
                        + "DEALER_COLLETION_STATUS = '" + receiptStatussave + "', COMPANY_CODE = '" + countrycode + "', "
                        + "OCRTYPE = '" + salesObject.getString("ocr") + "' WHERE RECEIPT_NUMBER = '" + receiptNumber + "' ";
                PreparedStatement updateQryPrepare = connection.prepareStatement(updateQuery);
                updateQryPrepare.execute();
                updateQryPrepare.close();
                JSONObject responseMap = new JSONObject();
                if(salesObject.getInt("saveflag") == 1) {
                    String getReceiptReport = "";
                    getReceiptReport = this.getGenerateOpenpdf(
                    		receiptNumber, 
                    		salesObject.getString("userid"), 
                    		fullPathImage,"cash", 
                    		salesObject.getString("receivedAmount").toString(), 
                    		salesObject.getString("paymentype"), 
                    		devlogopath, dealerEmail, countrycode, salesObject.getString("dealerRemark").toString(),companyName, userloggedName, salesObject.getString("lang").toString());
                    if(getReceiptReport != null) {
                        this.updateReceiptPathtoReceipt(getReceiptReport.toString(),receiptNumber);
                    } else {
                    	connection.rollback();
                    }
                    
                    if(statuscollection.equals("approved") || statuscollection.equals("rejected")) {
                    	
                    	/** Ready to Post
                    	 * GET UPDATE THE STATUS FOR THE COLLECTION RECORDS INFORMATION
                    	 On Hold */
                    	String receiptnumber = receiptNumber;
                        String receiptstatus = statuscollection;
                        String userid = userId;
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
            			String depostatus = "";
            			ArrayList<String> tokensListArray = new ArrayList<String>();
            			if(!tokensListQuery.getString("USER_PUSH_TOKEN").isEmpty()) {
            				tokensListArray.add(tokensListQuery.getString("USER_PUSH_TOKEN").toString());
            			}
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
                    }
                    
                    responseMap.put("receipturl", getReceiptReport.toString());
                    responseMap.put(STATUS_MESSAGE, SUCCESS);
                } else {
                    responseMap.put("receipturl", "");
                    responseMap.put(STATUS_MESSAGE, "Receipt : "+receiptNumber+", Saved Successfully as Draft.");
                }
                connection.commit();
                this.getdepouserdeviceTokens(salesObject.getString("userid").toString(),receiptNumber,"Cash",receiptStatussave);
                responseMap.put("receiptNumber", receiptNumber);
                responseMap.put("receiptAmount", salesObject.getString("receivedAmount"));
                responseMap.put(STATUS_CODE, SUCCESS_CODE);
                responseMap.put(STATUS_FLAG, 0);
                return responseMap;
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.err.println(e.getMessage().toString());
            return validationlog.errorValidationResponse(ERROR_CODE_500, e.getMessage().toString()+"Something went wrong!, please try again..");
        }
	}
	
	private void getstoreHistoryCollectioninfo(String ocr, String dealerMobile, String paymentMethod, String dealerRepresentative, String dealerRemark, String countrycode, String dealerId, String receiptNumber, String finalRecepitAmount, String receptAzureUrl, Timestamp createdAt, String userId, String dateofreceving, String receiptremarks, String depostatus) {
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
            PreparedStatement pstUserlogin = connection.prepareStatement(insertQry);
            pstUserlogin.setString(1, ocr);
            pstUserlogin.setString(2, dealerId);
            pstUserlogin.setString(3, dealerMobile);
            pstUserlogin.setString(4, "");
            pstUserlogin.setString(5, finalRecepitAmount);
            pstUserlogin.setString(6, dateofreceving);
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
            pstUserlogin.executeUpdate();
            pstUserlogin.close();
		} catch (Exception e) {
			// TODO: handle exception
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
	
	private void storeLogsDataInformation(String messageType, String excutionTime, String category, String userId, String logType) {
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + "MESSAGE,"
                    + "EXEXUTION_TIME,"
                    + "CREATED_AT,"
                    + "CATEGORY,"
                    + "USERID, LOG_TYPE "
                    + ") VALUES (?,?,?,?,?,?) ";
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, messageType.toString());
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

	   private Timestamp getCountryTimestamp(String cOMPANYCODE) {
			Timestamp createAt =null;
			try {
				String getTimestamp="SELECT dcm.TIMEZONE FROM "+ SCHEMA_TABLE +" . "+ COUNTRIES_MASTER +" dcm WHERE dcm.COUNTRY_CODE = ? OR dcm.COMPANY_CODE = ?";
				PreparedStatement pst = connection.prepareStatement(getTimestamp);
				pst.setString(1, cOMPANYCODE);
				pst.setString(2, cOMPANYCODE);
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
}
