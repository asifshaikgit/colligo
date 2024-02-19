package Repository;

import static Constants.AsianConstants.COLLECTION_RECEIPT_TABLE;
import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.DEALER_MOBILE;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.DL_DEALER_TABLE;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.INSTRUMENT_TABLE;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;
import static Constants.AsianConstants.SETTINGS_TABLE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.Salesdashboard;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;
import models.Delarmodel;
import models.Instrumentmodel;
import models.Saleslistdashboardmodel;

public class SalesDashboardlistrepo implements Salesdashboard {
    
	private Connection connection;
    private ValidationCtrl validate;
    private Jsonresponse jsonValit;
    private Pushnotificationrepo pushrepo;
    
    @SuppressWarnings("static-access")
	public SalesDashboardlistrepo() {
		// TODO Auto-generated constructor stub
    	connection = new Database().Connection();
    	validate = new ValidationCtrl();
    	jsonValit = new Jsonresponse();
    	pushrepo = new Pushnotificationrepo();
	}
    
    /* * GET THE RECEIPT DATA NUMBER OF RECORDS COUNT * */
    
    private int getRowsCount(String sqlCommand) {
        Statement stmt;
        int noOfRecords = 0;
        try {
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sqlCommand);
            while(resultSet.next()) {
                ++noOfRecords;
            }
            stmt.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return noOfRecords;
    }
    
    /**** FILTER INPUTS VALIDATE THE INPUT 
     * @param jsonArray *****/
    private ResultSet checkfilterSettings(JsonArray jsonArray) {
    	// TODO Auto-generated method stub
			Statement stmt;
			try {
				stmt = connection.createStatement();
				String querySelect = "SELECT "
						+ "L.*"
						+ " FROM " + SCHEMA_TABLE + " ." + SETTINGS_TABLE + " L "
								+ "WHERE ";
				for(int f=0;f<jsonArray.size();f++) {
					querySelect += " LOWER(L.NAME) = '"+jsonArray.get(f).getAsString().toLowerCase()+"' ";
					if(f < ( jsonArray.size() - 1 )) {
						querySelect += " OR ";
					}
				}
				querySelect += " ORDER BY L.ID DESC ";
				ResultSet resultSet = stmt.executeQuery(querySelect);
				stmt.close();
				return resultSet;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
    }
    /**** FILTER INPUTS VALIDATE THE INPUT *****/
    
    /* * GET THE RECEIPT DATA LIST * */
    
    private String UserMappedCountriesData(String userid) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String countrySql = "SELECT DI.COMPANY_ID FROM "+SCHEMA_TABLE+"."+DL_APP_USER_INFO+" DI "
	    					+ "WHERE DI.USERID = '"+userid+"'";
			ResultSet resultSet = stmt.executeQuery(countrySql);
			resultSet.next();
			String[] countriesList = resultSet.getString("COMPANY_ID").toString().split(",");
			String arrayCountriesList = "";
			for( int c=0;c<countriesList.length;c++ ) {
				arrayCountriesList += "'"+countriesList[c].toString()+"'";
				if(c < (countriesList.length-1)) {
					arrayCountriesList += ",";
				}
			}
			stmt.close();
			return arrayCountriesList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    @Override
    public Object ListSalesdashboard(JsonObject data, String typeofData) {
        // TODO Auto-generated method stub
        String userid = data.get("userid").getAsString();
        Boolean usercheck = this.getLoggeduserinfo(userid.toString());
    	if(usercheck.equals(true)) {
    		return jsonValit.loginerrorResponseCall();
    	}
        String page = data.get("page").getAsString();
        String countrycode = data.get("companycode").getAsString();
        String datefrom = data.get("datefrom").getAsString();
        String dateto = (data.get("dateto").getAsString().length() > 0 ? jsonValit.addoneDaytoDate(data.get("dateto").getAsString()) : "");
        Statement stmt;
        try {
            ResultSet getLoginUserinfo = this.getExistingUserDataById(userid);
            getLoginUserinfo.next();
            /* * CHECK THE USER IF SALES LOGIN ACCESS THE DEPO LIST DATA. * */
            if(getLoginUserinfo.getInt("USER_LEVEL") == 1 && typeofData.equals("depo")) {
                return validate.errorValidationResponse(ERROR_CODE_500, "Depo user only have the rights to see the list.");
            }
            /* * LIMIT PER PAGE * */
            int perPageLimit = 20;
            int offset = (Integer.parseInt(page) - 1) * (perPageLimit);
            String sqlQuery = "SELECT "
                    + "R.RECEIPT_NUMBER,"
                    + "R.DEALER_ID,"
                    + "D.DEALER_EMAIL,"
                    + "R.UPDATED_AT AS CREATED_AT,"
                    + "R.RECEIPT_AMOUNT,"
                    + "R.MODE_OF_PAYMENT,"
                    + "R.DEPO_RECEIPT_STATUS AS DEPO_RECEIPT_STATUS,"
                    + "R.DEALER_COLLETION_STATUS AS DEALER_COLLETION_STATUS,"
                    + "D.DEALER_NAME,"
                    + "C.CURRENCY,"
                    + "R.DEALER_COLLETION_STATUS AS SALESSTATUS,"
                    + "R.DEPO_RECEIPT_STATUS AS DEPOSTATUS "
                    + "FROM "
                    + "" + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " R "
                            + " LEFT JOIN " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " D ON D.DEALER_ID = R.DEALER_ID "
                            + " LEFT JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " C ON D.DEALER_COMPANY_CODE = C.COMPANY_CODE "
                            		+ " INNER JOIN " + SCHEMA_TABLE + "." + DL_APP_USER_INFO + " S ON S.USERID = R.CREATED_BY ";
            Boolean deleteFlag = false;
            sqlQuery += " WHERE S.USER_DELETE_FLAG = " + deleteFlag + " AND R.COMPANY_CODE = '"+countrycode+"'";
            if( typeofData.equals("depo") ) {
            	String listCountrieslist = this.UserMappedCountriesData(userid);
            	sqlQuery += " AND R.COMPANY_CODE IN  ("+listCountrieslist+") ";
            }
            if(data.has("filter")) {
            	JsonArray arralyFilter = data.get("filter").getAsJsonArray();
                String receiptStatusTypeOptions = "";
                String paymentTypeOptions = "";
                if( arralyFilter.size() > 0 ) {
                	for(int a=0;a<arralyFilter.size();a++) {
                		receiptStatusTypeOptions += "'"+arralyFilter.get(a).getAsString()+"'";
                		if(a < (arralyFilter.size()-1)) {
                			receiptStatusTypeOptions += ",";
                		}
                	}
                	ResultSet settingsFilter = this.checkfilterSettings(arralyFilter);
                	while(settingsFilter.next()) {
                		int categorySno = settingsFilter.getInt("ID");
                		String category = settingsFilter.getString("CATEGORY");
                		if( category.toString().equals("payment") ) {
                			paymentTypeOptions += categorySno + ",";
                		}
                	}
                }
                //remvoe the comma from the end of each if have the comma
                if( receiptStatusTypeOptions.endsWith(",") ) {
                	receiptStatusTypeOptions = receiptStatusTypeOptions.substring(0, receiptStatusTypeOptions.length() - 1);
                }
                
                //remvoe the comma from the end of each if have the comma
                if( paymentTypeOptions.endsWith(",") ) {
                	paymentTypeOptions = paymentTypeOptions.substring(0, paymentTypeOptions.length() - 1);
                }
                /** CASE Statment for depo and sales receipt actions status*************/
                if(receiptStatusTypeOptions.length() > 0) {
                	if(arralyFilter.toString().contains("issued") 
                			|| arralyFilter.toString().contains("draft") 
                			|| arralyFilter.toString().contains("On Hold") 
                			|| arralyFilter.toString().contains("Mark AS Invalid") 
                			|| arralyFilter.toString().contains("Post to SAP") 
                			|| arralyFilter.toString().contains("Ready to Post")) {
                			sqlQuery += " AND CASE "
                        			+ "WHEN R.DEPO_RECEIPT_STATUS IN ('Ready to Post','On Hold','Post to SAP','Mark AS Invalid') THEN R.DEPO_RECEIPT_STATUS "
                        			+ "ELSE R.DEALER_COLLETION_STATUS END IN (" + receiptStatusTypeOptions + ")";
                	}
                }
                /** CASE Statment for depo and sales receipt actions status*************/
                /** CASE Statment for mode of payment actions status*************/
                if(arralyFilter.toString().contains("Non Cash") || arralyFilter.toString().contains("Cash")) {
                	sqlQuery += " AND "
                			+ "CASE "
                			+ "WHEN R.MODE_OF_PAYMENT IN ('Cash') THEN 'Cash' "
                			+ "WHEN R.MODE_OF_PAYMENT IN ('Non Cash') THEN 'Non Cash' END IN ( "+receiptStatusTypeOptions+" )";
                }
                /** CASE Statment for mode of payment actions status*************/
            }
            if(data.has("receiptfilter")) {
            	String receiptfilter = data.get("receiptfilter").getAsString();
            	if(!receiptfilter.isEmpty()) {
            		if(receiptfilter.contains(",")) {
            			receiptfilter = receiptfilter.replaceAll(",", "");
            		}
            	
            	sqlQuery += " AND ("
            			+ "REPLACE(REPLACE(REPLACE(REPLACE(("
            			+ "CASE WHEN R.MODE_OF_PAYMENT IN ('Cash') THEN R.RECEIPT_AMOUNT ELSE R.RECEIPT_AMOUNT END"
            			+ "),',',''),'.0',''),'.00',''),'.000','') LIKE '%"+receiptfilter+"%' "
            					+ "OR"
            					+ " LOWER(R.DEALER_ID) LIKE '%"+receiptfilter.toLowerCase()+"%' "
            							+ "OR"
            							+ " LOWER(D.DEALER_NAME) LIKE '%"+receiptfilter.toLowerCase()+"%' "
            									+ "OR"
            									+ " LOWER(R.RECEIPT_NUMBER) LIKE '%"+receiptfilter.toLowerCase()+"%' "
            					+ ") ";
            	}
            }
            if(data.has("salesfilter")) {
            	String salesfilter = data.get("salesfilter").getAsString();
            	if(!salesfilter.isEmpty()) {
            		sqlQuery += " AND ( "
            				+ "LOWER(S.USERID) LIKE '%" + salesfilter.toLowerCase() + "%' "
            						+ "OR"
            						+ " LOWER(S.USERNAME) LIKE '%" + salesfilter.toLowerCase() + "%' "
            								+ ") ";
            	}
            }
            /** CASE Statment for depo and sales receipt actions status*************/
            if( typeofData.equals("sales") ) {
                sqlQuery += " AND R.CREATED_BY = '" + userid + "' ";   
            }
            
            if( !datefrom.isEmpty() ) {
                if( !dateto.isEmpty() ) {
                    sqlQuery += "AND  R.CREATED_AT >= '" + datefrom + "' AND R.CREATED_AT <= '" + dateto + "' ";
                }
            }
            
            /* * LOGGED USER IS DEPO USER CHECK AND DISPLAY INITATED RECEIPTS DATA * */
            if( typeofData.equals("depo") ) {
                sqlQuery += "AND R.DEPO_RECEIPT_STATUS IN ('','Ready to Post','On Hold','Mark AS Invalid','Post to SAP','Error from SAP') AND R.DEALER_COLLETION_STATUS = 'issued' ";
            }
            sqlQuery += " ORDER BY R.UPDATED_AT DESC ";
            String originalSqlQuery = sqlQuery + " limit " + perPageLimit + " offset " + offset;
            System.out.println(sqlQuery);
            this.storeLogsDataInformation(originalSqlQuery.toString(),"","depoList","","info");
            stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(originalSqlQuery);
            int numberOfColumns = this.getRowsCount(sqlQuery);
            List<Saleslistdashboardmodel> salesListData = new ArrayList<>();
            while(resultSet.next()) {
                String receiptNumber = resultSet.getString("RECEIPT_NUMBER");
                String createdAt = (resultSet.getString("CREATED_AT") != null ? jsonValit.getDatewithonly(resultSet.getString("CREATED_AT").toString()) : "");
                String finalReceiptAmount;
                String modeOfPayment = resultSet.getString("MODE_OF_PAYMENT").toString();
                String paymentMode = modeOfPayment;
                finalReceiptAmount = resultSet.getString("RECEIPT_AMOUNT");
                String reportSalesStatus = (resultSet.getString("SALESSTATUS") != null ? resultSet.getString("SALESSTATUS").toString() : "");
                String reportDepoStatus = (resultSet.getString("DEPOSTATUS") != null ? resultSet.getString("DEPOSTATUS").toString() : "");
                String collectionDraftstatus = "";
                
                if(reportSalesStatus.equals("draft") && reportDepoStatus.toString().isEmpty()) {
                	collectionDraftstatus = reportSalesStatus;
                } else if(reportSalesStatus.equals("issued") && reportDepoStatus.toString().isEmpty()) {
                	collectionDraftstatus = reportSalesStatus;
                } else if(reportSalesStatus.equals("issued") && !reportDepoStatus.toString().isEmpty()) {
                	collectionDraftstatus = reportDepoStatus;
                }
                String dealerName = resultSet.getString("DEALER_NAME") != null ? resultSet.getString("DEALER_NAME").toString() : "";
                String currency = resultSet.getString("CURRENCY");
                String dealerId = resultSet.getString("DEALER_ID").toString();
                salesListData.add( new Saleslistdashboardmodel(receiptNumber,
                		createdAt,
                		finalReceiptAmount,
                		paymentMode,
                		collectionDraftstatus,
                		dealerName,
                		currency,
                		collectionDraftstatus,
                		dealerId
                		) );
            }
            stmt.close();
            double numofpages = (double)numberOfColumns / perPageLimit;
            JSONObject responseMap = new JSONObject();
            responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
            responseMap.put("salesList",salesListData);
            responseMap.put("numofpages",Math.ceil(numofpages));
            responseMap.put("pagelimit",perPageLimit);
            responseMap.put("totalRows",numberOfColumns);
            return responseMap;
            
        } catch (SQLException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation(e.getMessage().toString(),"","depoList","","error");
            return validate.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
    }
    
    /* * GET THE RECEIPT DATA LIST * */
    
    @Override
    public Object getSalesReceiptData(String receiptId, String userid) {
        // TODO Auto-generated method stub
        JSONObject dealerReceiptInfo = new JSONObject();
        try {
            ResultSet resultSet = this.getLoadDealerData(receiptId);
            resultSet.next();
            String paymenttype = resultSet.getString("MODE_OF_PAYMENT").toString();
            String draftStatus = resultSet.getString("DEALER_COLLETION_STATUS").toString();
            String collectionDraftstatus = "";
            String reportDepoStatus = (resultSet.getString("DEPO_RECEIPT_STATUS") != null ? resultSet.getString("DEPO_RECEIPT_STATUS").toString() : "");
            if(draftStatus.equals("draft") && reportDepoStatus.toString().isEmpty()) {
            	collectionDraftstatus = draftStatus;
            } else if(draftStatus.equals("issued") && reportDepoStatus.toString().isEmpty()) {
            	collectionDraftstatus = draftStatus;
            } else if(draftStatus.equals("issued") && !reportDepoStatus.toString().isEmpty()) {
            	collectionDraftstatus = reportDepoStatus;
            }
            ResultSet countryActiveInfo = this.getByCountryCodeData(receiptId);
            countryActiveInfo.next();
		    String activeName;
            if(countryActiveInfo.getBoolean("COUNTRY_OTP_STATUS") == true) {
                activeName = "Active";
            } else {
                activeName = "In-Active";
            }
            dealerReceiptInfo.put("dealerOtpSendStatus", activeName);
            dealerReceiptInfo.put("salesId", resultSet.getString("CREATEDBY") != null ? resultSet.getString("CREATEDBY").toString() : "");
            dealerReceiptInfo.put("salesname", resultSet.getString("SALESNAME") != null ? resultSet.getString("SALESNAME").toString() : "");
            dealerReceiptInfo.put("dealerId", resultSet.getString("DEALER_ID") != null ? resultSet.getString("DEALER_ID").toString() : "");
            dealerReceiptInfo.put("dealerName", resultSet.getString("DEALER_NAME") != null ? resultSet.getString("DEALER_NAME").toString() : "");
            dealerReceiptInfo.put("dealerMobile", resultSet.getString("DEALER_MOBILE") != null ? resultSet.getString("DEALER_MOBILE").toString() : "");
            dealerReceiptInfo.put("receiptNumber", resultSet.getString("RECEIPT_NUMBER") != null ? resultSet.getString("RECEIPT_NUMBER").toString() : "");
            dealerReceiptInfo.put("createdAt", (resultSet.getString("CREATED_AT") != null ? jsonValit.getDatewithonly(resultSet.getString("CREATED_AT").toString()) : ""));
            dealerReceiptInfo.put("representative", (resultSet.getString("DEALER_REPRESENTATION") != null) ? resultSet.getString("DEALER_REPRESENTATION").toString() : "");
            dealerReceiptInfo.put("dealerRemarks", (resultSet.getString("DEALER_REMARKS") != null) ? resultSet.getString("DEALER_REMARKS").toString() : "");
            dealerReceiptInfo.put("dealerOcrtype", (resultSet.getString("OCRTYPE") != null) ? resultSet.getString("OCRTYPE").toString() : "");
            dealerReceiptInfo.put("paymenttype", paymenttype);
            dealerReceiptInfo.put("collectionDraftstatus", collectionDraftstatus);
            dealerReceiptInfo.put("receivedamount", (resultSet.getString("RECEIPT_AMOUNT") != null) ? resultSet.getString("RECEIPT_AMOUNT").toString() : "");
            dealerReceiptInfo.put("finalreceiptamount", (resultSet.getString("RECEIPT_AMOUNT") != null) ? resultSet.getString("RECEIPT_AMOUNT").toString() : "");
            dealerReceiptInfo.put("dateofreceving", ( resultSet.getString("DATE_OF_RECEVING") == null ) ? "" : resultSet.getString("DATE_OF_RECEVING").toString() );
            dealerReceiptInfo.put("receiptpdf", ( resultSet.getString("RECEIPT_FILENAME") == null ) ? "" : resultSet.getString("RECEIPT_FILENAME").toString() );
            dealerReceiptInfo.put("deporemarks", ( resultSet.getString("DEPO_REMARKS") == null ) ? "" : resultSet.getString("DEPO_REMARKS").toString() );
            dealerReceiptInfo.put("dealerEmail", ( resultSet.getString("DEALER_EMAIL") == null ) ? "" : resultSet.getString("DEALER_EMAIL").toString() );
            dealerReceiptInfo.put("dealerList", this.getloadDealerInformation(resultSet.getString("DEALER_ID") != null ? resultSet.getString("DEALER_ID").toString() : ""));
            
            if(paymenttype.equals("Non Cash")) {
                List<Instrumentmodel> listInstruments = this.getLoadInstrumentdata( receiptId.toString() );
                dealerReceiptInfo.put("instruments", listInstruments);
            }
            JSONObject responseMap = new JSONObject();
            responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
            responseMap.put("data",dealerReceiptInfo);
            return responseMap;
            
        } catch (JSONException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return validate.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
        
    }
    
    /*
     *GET THE COUNTRY CODE BASED ON THE RECEIPT ID *
     */
    
    private ResultSet getByCountryCodeData(String receiptNumber) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			stmt = this.connection.createStatement();
			String querySelect = "SELECT L.* FROM " + SCHEMA_TABLE + " ." + COUNTRIES_MASTER + " L INNER JOIN "+SCHEMA_TABLE+"."+COLLECTION_RECEIPT_TABLE+" R ON R.COMPANY_CODE = L.COMPANY_CODE WHERE R.RECEIPT_NUMBER = '"+receiptNumber+"' LIMIT 1";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    /*
     * UPDATE THE DEPO ACCEPTENCE AND REJECTENCE
     */
 
    @Override
    public Object getUpdateReceiptDepoinfo(JsonObject data) {
        // TODO Auto-generated method stub
        try {
            String receiptnumber = data.get("receiptnumber").getAsString();
            String receiptstatus = data.get("receiptstatus").getAsString();
            String receiptremarks = data.get("receiptremarks").getAsString();
            String userid = data.get("userid").getAsString();
            
            JSONObject responseMap = new JSONObject();
            String updateQuery;
            JSONObject pushtokenObj = new JSONObject();
            String messageTokenPush;
            ResultSet tokensListQuery = this.getdepouserdeviceTokens(receiptnumber); 
            System.out.println(receiptnumber);
            String countrycode = tokensListQuery.getString("COMPANY_CODE");
//          Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            
            Timestamp createdAt = this.getCountryTimestamp(countrycode);
			ArrayList<String> tokensListArray = new ArrayList<String>(); //Creating arraylist
			if(!tokensListQuery.getString("USER_PUSH_TOKEN").isEmpty()) {
				tokensListArray.add(tokensListQuery.getString("USER_PUSH_TOKEN").toString());
			}
            if(receiptstatus.equals("approved")) {
                updateQuery = "UPDATE "
                		+ "" + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE +" SET "
                				+ "DEPO_RECEIPT_STATUS = 'Ready to Post', "
                				+ "DEPO_REMARKS = '" + receiptremarks + "', "
                						+ "APPROVED_BY = '" + userid + "', DEPO_UPDATED_AT = '" + createdAt +"' "
                								+ "WHERE RECEIPT_NUMBER = '" + receiptnumber + "' ";
                messageTokenPush = "Your receipt "+receiptnumber+" with amount of "+tokensListQuery.getString("RECEIPT_AMOUNT").toString()+" got approved successfully";
                responseMap.put("message", messageTokenPush);
            } else {
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
            System.out.println(pushtokenObj.toString());
            if(tokensListArray.size() > 0) {
            	pushrepo.send(pushtokenObj);
            }
            System.out.println(updateQuery);
            PreparedStatement updateQryPrepare = connection.prepareStatement(updateQuery);
            updateQryPrepare.execute();
            updateQryPrepare.close();
            if(updateQryPrepare != null) {
                responseMap.put(STATUS_CODE, SUCCESS_CODE);
                responseMap.put(STATUS_MESSAGE, SUCCESS);
            }
            return responseMap;
        } catch (SQLException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return validate.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
        
    }
    
    private Object getloadDealerInformation(String dealerId) {
    	Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT *,(SELECT STRING_AGG(M.DEALER_MOBILE,',') FROM "+SCHEMA_TABLE+"."+DEALER_MOBILE+" M WHERE M.DEALER_ID = DEALER_CODE) AS MOBILE FROM ( SELECT "
            		+ "L.DEALER_ID AS DEALER_CODE,"
            		+ "L.DEALER_NAME,"
            		+ "L.DEALER_EMAIL,"
            		+ "L.DEALER_BANK_BRANCH,"
            		+ "L.DEALER_BANK_NAME,"
            		+ "L.DEALER_CITY FROM " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " L "
            				+ "INNER JOIN " + SCHEMA_TABLE + "." + COUNTRIES_MASTER + " K "
            						+ "ON L.DEALER_COMPANY_CODE = K.COMPANY_CODE WHERE L.DEALER_ID = '"+dealerId+"'";
			querySelect += " ORDER BY L.ID DESC)  X ";
			List<Delarmodel> delarlist = new ArrayList<>();
			ResultSet countriesresultSet = stmt.executeQuery(querySelect);
			while(countriesresultSet.next()) {
            	String dealerMobile = countriesresultSet.getString("MOBILE");
            	String[] mobileList = new String[0];
            	if(dealerMobile != null) {
            		mobileList = dealerMobile.split(",");
            	}
            	
                Delarmodel deladerObj = new Delarmodel();
                String countid = countriesresultSet.getString("DEALER_CODE");
                String countryname = countriesresultSet.getString("DEALER_NAME");
                String dealerCity = countriesresultSet.getString("DEALER_CITY").toString();
                String dealerbankBranch = (countriesresultSet.getString("DEALER_BANK_BRANCH") != null ? countriesresultSet.getString("DEALER_BANK_BRANCH") : "");
                String dealerBankName = (countriesresultSet.getString("DEALER_BANK_NAME") != null ? countriesresultSet.getString("DEALER_BANK_NAME") : "");
                String dealerEmail = (countriesresultSet.getString("DEALER_EMAIL") != null ? countriesresultSet.getString("DEALER_EMAIL") : "");
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
			return delarlist;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    protected ResultSet getLoadDealerData(String receiptid) {
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String receiptSql = "SELECT *,(SELECT S.USERNAME FROM AP_GLOBAL.DL_APP_USER_INFO S WHERE S.USERID = CREATEDBY) AS SALESNAME FROM ( SELECT x.OCRTYPE,"
                    + "x.RECEIPT_NUMBER,"
                    + "d.DEALER_ID,"
                    + "d.DEALER_EMAIL,"
                    + "d.DEALER_NAME,"
                    + "x.UPDATED_AT AS CREATED_AT,"
                    + "x.DEALER_REPRESENTATION,"
                    + "x.DEALER_REMARKS,"
                    + "x.DATE_OF_RECEVING,"
                    + "x.MODE_OF_PAYMENT,"
                    + "x.DEALER_MOBILE,"
                    + "x.DEALER_COLLETION_STATUS,"
                    + "x.RECEIPT_AMOUNT,"
                    + "x.RECEIPT_FILENAME,"
                    + "x.DEPO_REMARKS,"
                    + "x.UPDATED_BY,"
                    + "x.DEPO_RECEIPT_STATUS, x.CREATED_BY AS CREATEDBY "
                    + " FROM " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " x"
                    + " LEFT JOIN " + SCHEMA_TABLE + "." + DL_DEALER_TABLE + " d ON d.DEALER_ID = x.DEALER_ID "
                    + " WHERE x.RECEIPT_NUMBER ='" + receiptid + "' ) X ";
            ResultSet resultSet = stmt.executeQuery(receiptSql);
            stmt.close();
            return resultSet;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    private ResultSet getdepouserdeviceTokens(String receiptNumber) {
		Statement stmt;
		Boolean deleteStatus = false;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.USER_PUSH_TOKEN,"
					+ "R.RECEIPT_AMOUNT,"
					+ "R.MODE_OF_PAYMENT,"
					+ "R.DEALER_COLLETION_STATUS,"
					+ "R.DEPO_RECEIPT_STATUS, "
					+ "R.COMPANY_CODE " //added this line by asif
					+ " FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "INNER JOIN " + SCHEMA_TABLE + "." + COLLECTION_RECEIPT_TABLE + " R ON R.UPDATED_BY = L.USERID "
							+ "WHERE "
							+ "L.USER_DELETE_FLAG = "+deleteStatus+" AND L.USER_LEVEL IN (1,2) AND R.RECEIPT_NUMBER = '"+receiptNumber+"' "
									+ "ORDER BY L.ID DESC";
			System.out.println(querySelect);
			ResultSet resultSet = stmt.executeQuery(querySelect);
			
			resultSet.next();
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    @SuppressWarnings("unused")
    public List<Instrumentmodel> getLoadInstrumentdata(String receiptNumber) {
        Statement stmt;
        try {
        	Boolean deleteFlagbool = false;
            stmt = connection.createStatement();
            String instrumentSql = "SELECT x.* FROM "+SCHEMA_TABLE+"."+INSTRUMENT_TABLE+" x "
                    + "WHERE "
                    + "RECEIPT_ID = '" + receiptNumber + "'"
                    + " AND DELETE_FLAG = "+deleteFlagbool+" ";
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
                String instrument_type = instrumentresultSet.getString("INSTRUMENT_TYPE").toString();
                String instrumentNumber = instrumentresultSet.getString("INSTRUMENT_NUMBER").toString();
                String instrumentDate = instrumentresultSet.getString("INSTRUMENT_DATE").toString();
                String instrumentDateofReceving = instrumentresultSet.getString("INSTRUMENT_DATE_OF_RECEVING").toString();
                String instrumentImage = instrumentresultSet.getString("INSTRUMENT_IMAGE_URL").toString();
                String instrumentmicrno = instrumentresultSet.getString("INSTRUMENT_MICR_NUMBER").toString();
                String instrumentpayeename = instrumentresultSet.getString("INSTRUMENT_PAYEE_NAME").toString();
                String instrumentid = instrumentresultSet.getString("INSTRUMENT_ID").toString();
                String instrumentStatus = instrumentresultSet.getString("INSTRUMNET_STATUS") != null ? instrumentresultSet.getString("INSTRUMNET_STATUS").toString() : "";
                String genId = instrumentresultSet.getString("INSTRUMENT_GENID") != null ? instrumentresultSet.getString("INSTRUMENT_GENID").toString() : "";
                instrumentlist.add(new Instrumentmodel(
                		ID,
                		sqNumber, 
                		receiptId, 
                		InstrumentAmount, 
                		instrumentBankCode, 
                		instrumentbankBranch, 
                		instrumentRemarks, 
                		instrument_type, 
                		instrumentNumber, 
                		instrumentDate, 
                		instrumentDateofReceving, 
                		instrumentImage,
                		instrumentAccountNumber,
                		instrumentmicrno,
                		instrumentpayeename,
                		instrumentid,
                		genId,instrumentStatus
                		));
                sqNumber++;
            }
            return instrumentlist;            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.storeLogsDataInformation("getLoadInstrumentdata:"+e.getMessage(),"","receipt","","error");
            return null;   
        }
    }
    
    public ResultSet getExistingUserDataById(String userId) {
		// TODO Auto-generated method stub
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.*"
					+ " FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "WHERE L.USERID = '"+userId+"' ORDER BY L.ID DESC";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			stmt.close();
			return resultSet;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
                    + "USERID"
                    + ") VALUES (?,?,?,?,?) ";
            System.out.println(createdAt.toString());
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, messageInfo.toString());
            pstUserlogin.setString(2, "");
            pstUserlogin.setString(3, createdAt.toString());
            pstUserlogin.setString(4, (category != null ? category.toString() : ""));
            pstUserlogin.setString(5, (userId != null ? userId.toString() : ""));
            System.out.println(pstUserlogin);
            pstUserlogin.executeUpdate();
            System.out.println(pstUserlogin.toString());
            pstUserlogin.close();
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    private Boolean getLoggeduserinfo(String username) {
		Statement stmt;
		Boolean userDeletestatus;
		try {
			stmt = connection.createStatement();
			String querySelect = "SELECT "
					+ "L.USER_DELETE_FLAG "
					+ "FROM " + SCHEMA_TABLE + " ." + DL_APP_USER_INFO + " L "
							+ "WHERE "
							+ "L.USERID = '"+username+"' "
									+ "ORDER BY L.ID DESC LIMIT 1";
			ResultSet resultSet = stmt.executeQuery(querySelect);
			resultSet.next();
			userDeletestatus = resultSet.getBoolean("USER_DELETE_FLAG");
			stmt.close();
			return userDeletestatus;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
}
