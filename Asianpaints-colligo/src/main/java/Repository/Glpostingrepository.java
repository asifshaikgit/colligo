package Repository;
import static Constants.AsianConstants.COLLECTION_RECEIPT_TABLE;
import static Constants.AsianConstants.DL_APP_USER_INFO;
import static Constants.AsianConstants.DL_GL_POSTING_INSTRUMENTS;
import static Constants.AsianConstants.COUNTRIES_MASTER;
import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.INSTRUMENT_TABLE;
import static Constants.AsianConstants.LOGS_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.SI_SO_PO_CREATE;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.Glpostinginterface;
import Utilres.Jsonresponse;
import Utilres.ValidationCtrl;
import models.Glpostingmodel;
import models.Glpostingrequest;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("static-access")
public class Glpostingrepository implements Glpostinginterface {
	
	private Connection connection;
	private ValidationCtrl validate;
	private Jsonresponse Jsonre;
	
	
	public Glpostingrepository() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
		validate = new ValidationCtrl();
		Jsonre = new Jsonresponse();
	}
	
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
            System.err.println(e.getMessage());
        }
        return noOfRecords;
    }
	
	private Object companywiseDropdowndata(String companyCode) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String settingQuery = "SELECT SNAME, (SELECT HOUSING_BANK AS HOUSEBANK FROM AP_GLOBAL.DL_SETTINGS_TABLE WHERE CATEGORY = 'gltype' AND COMPANY_MASTER_CODE = '"+companyCode+"' AND NAME = SNAME FOR JSON ) AS BANKINFO FROM ( SELECT S.NAME AS SNAME FROM AP_GLOBAL.DL_SETTINGS_TABLE S WHERE S.CATEGORY = 'gltype' AND S.COMPANY_MASTER_CODE = '"+companyCode+"') x";
			
			ResultSet queryDropdown = stmt.executeQuery(settingQuery);
			ArrayList<Object> dropdownArray = new ArrayList<>();
			while(queryDropdown.next()) {
				JSONObject dropDownObj = new JSONObject();
				dropDownObj.put("accountid", queryDropdown.getString("SNAME").toString());
				dropDownObj.put("bankinfo", new JSONArray(queryDropdown.getString("BANKINFO").toString()));
				dropdownArray.add(dropDownObj);
			}
			return dropdownArray;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage().toString());
			return null;
		}
	}
	
	private ResultSet getGlpostinformation(String instrumentNumber, String instrumentsNumber, String modeOfPayment) {
		Statement stmt;
        try {
        	stmt = connection.createStatement();
            String instrumentSql;
            	instrumentSql = "SELECT ACCOUNT_ID ,BUSSINESS_AREA ,HOUSE_BANK,FISCAL_YEAR ,SPECIAL_GL_IND ,BANK_CHARGES,POSTING_DATE,SAP_STATUS_CODE,SAP_STATUS_RESPONSE,DOCUMENT_DATE,INSTRUMENT_DATE,INSTRUMENT_NUMBER,SAP_DOCUMENT_NUMBER,SAP_STATUS_RESPONSE,SAP_STATUS_CODE,INSTRUMENT_TYPE,BANK_BRANCH,FISCAL_YEAR,DOCUMENT_NUMBER,BANK_CODE FROM AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS WHERE REMARKS = '"+instrumentNumber+"'";
            	if(!modeOfPayment.equals("Cash")) {
            		instrumentSql += " AND INSTRUMENT_NUMBER = '"+instrumentsNumber+"' ";	
            	}
            	instrumentSql += " ORDER BY CREATED_AT DESC LIMIT 1";
            System.err.println(instrumentSql);
            ResultSet instrumentResultSet = stmt.executeQuery(instrumentSql);
            return instrumentResultSet;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
            
	}
	
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
	
	
	private Object gettheinstrumenttypes() {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String settingQuery = "SELECT STRING_AGG(NAME,',') AS SNAME FROM AP_GLOBAL.DL_SETTINGS_TABLE WHERE  CATEGORY = 'instrument'";
			ResultSet queryDropdown = stmt.executeQuery(settingQuery);
			String[] dropdownArray = new String[0];
			queryDropdown.next();
			String snamelist = queryDropdown.getString("SNAME").toString();
				dropdownArray = snamelist.split(",");
			return dropdownArray;
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage().toString());
			return null;
		}
	}
	
	private String getcurrencyName(String countryCode) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			String settingQuery = "SELECT CM.CURRENCY FROM AP_GLOBAL.DL_COUNTRIES_MASTER CM WHERE CM.COMPANY_CODE = '"+countryCode+"' LIMIT 1";
			ResultSet queryDropdown = stmt.executeQuery(settingQuery);
			queryDropdown.next();
			return queryDropdown.getString("CURRENCY").toString();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage().toString());
			return null;
		}
	}

	@Override
	public Object getloadglPostingData(JsonObject data) {
		// TODO Auto-generated method stub
		Statement stmt;
        try {
        	String search = data.get("search").getAsString();
        	String statussearch = data.get("statussearch").getAsString();
        	String fromdate = data.get("fromdate").getAsString();
        	String todate = data.get("todate").getAsString();
        	String page = data.get("page").getAsString();
        	String userid = data.get("userid").getAsString();
        	String countryid = data.get("countryid").getAsString();
        	if(statussearch.equals("invalid")) {
        		statussearch = "Mark AS Invalid";
        	}
        	/*
        	 * LIMIT PER PAGE
        	 */
        	    
        	int perPageLimit = 10;
            int offset = (Integer.parseInt(page) - 1) * (perPageLimit);
            stmt = connection.createStatement();
            String listCountrieslist = this.UserMappedCountriesData(userid);
            String instrumentSql = "";
//            if(statussearch.isEmpty() || statussearch.equals("Ready to Post")) {
            	instrumentSql = "SELECT *, (SELECT NAME FROM AP_GLOBAL.DL_SETTINGS_TABLE WHERE CATEGORY ='bs' AND COMPANY_MASTER_CODE = COMPANY_CODE FOR JSON) AS bsdata, (SELECT st.SAP_BANK_CODE FROM AP_GLOBAL.DL_SETTINGS_TABLE st WHERE st.NAME =INSTRUMENT_BANK_CODE AND COMPANY_MASTER_CODE = COMPANY_CODE AND CATEGORY = 'bank') as bankcode, (SELECT IFNULL(NAME,'') AS bankName,IFNULL(SAP_BANK_CODE,'') AS bankCode  FROM AP_GLOBAL.DL_SETTINGS_TABLE WHERE  CATEGORY = 'bank' AND COMPANY_MASTER_CODE = COMPANY_CODE GROUP BY NAME,SAP_BANK_CODE FOR JSON ) AS bankDropdowns FROM (SELECT i.INSTRUMENT_BANK_ACCOUNT_NUMBER,"
                		+ "IFNULL(i.COMPANY_CODE,x.COMPANY_CODE) AS COMPANY_CODE,"
                		+ "x.DEALER_ID,"
                		+ "i.INSTRUMENT_AMOUNT,"
                		+ "x.APPROVED_BY,"
                		+ "i.INSTRUMENT_BANK_BRANCH,"
                		+ "i.INSTRUMENT_BANK_CODE,"
                		+ "i.INSTRUMENT_MICR_NUMBER,"
                		+ "i.CREATED_BY,"
                		+ "i.CREATED_AT,"
                		+ "i.RECEIPT_ID,"
                		+ "x.RECEIPT_NUMBER,"
                		+ "i.INSTRUMENT_DATE_OF_RECEVING,"
                		+ "i.INSTRUMENT_ID,"
                		+ "i.INSTRUMENT_NUMBER,"
                		+ "i.INSTRUMENT_DATE,"
                		+ " IFNULL(i.INSTRUMENT_TYPE,x.MODE_OF_PAYMENT) AS INSTRUMENT_TYPE,"
                		+ "x.DEPO_RECEIPT_STATUS,x.DEALER_REMARKS,x.UPDATED_AT,x.SAP_MESSAGE_LOG AS RSAP_MESSAGE_LOG,i.SAP_MESSAGE_LOG AS ISAP_MESSAGE_LOG,"
                		+ "i.INSTRUMENT_REMARKS, x.MODE_OF_PAYMENT, x.DATE_OF_RECEVING, x.RECEIPT_AMOUNT,i.SAP_DOC_NUM AS ISAP_DOC_NUM,x.SAP_DOC_NUM,x.DEPO_RECEIPT_STATUS,IFNULL(i.INSTRUMNET_STATUS, x.DEPO_RECEIPT_STATUS) AS INSTRUMNET_STATUS,x.CREATED_BY";
            	if(!statussearch.isEmpty() && !statussearch.equals("Ready to Post")) {
//            	instrumentSql += ",p.DOCUMENT_DATE DOCUMENTDATE,p.INSTRUMENT_TYPE INSTRUMENTTYPE,p.INSTRUMENT_DATE INSTRUMENTDATE ";
            	}
            	instrumentSql += " FROM "+SCHEMA_TABLE+"."+COLLECTION_RECEIPT_TABLE+" x LEFT JOIN "+SCHEMA_TABLE+"."+INSTRUMENT_TABLE+" i ON i.RECEIPT_ID = x.RECEIPT_NUMBER ";
            	if(!statussearch.isEmpty() && !statussearch.equals("Ready to Post")) {
//            		instrumentSql += "LEFT JOIN AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS p ON ( p.SAP_DOCUMENT_NUMBER = x.SAP_DOC_NUM OR p.SAP_DOCUMENT_NUMBER = i.SAP_DOC_NUM ) ";
            	}
            	instrumentSql += "WHERE ";
            	
            	
            	instrumentSql += " x.COMPANY_CODE = '"+countryid+"' AND x.COMPANY_CODE IN  ("+listCountrieslist+") ";
            	
                		if(statussearch.isEmpty() || statussearch.equals("Ready to Post")) {
                			instrumentSql += "AND x.DEALER_COLLETION_STATUS = 'issued' ";//AND x.COMPANY_CODE != '' AND i.INSTRUMENT_ID != ''Error from SAP
                		} else if(!statussearch.isEmpty() && statussearch.equals("Issued Receipts")) {
                    		instrumentSql += " AND (x.DEALER_COLLETION_STATUS = 'issued' AND x.DEPO_RECEIPT_STATUS = '' ) ";//OR i.INSTRUMNET_STATUS = '"+statussearch+"'
                    	} else if(!statussearch.isEmpty() && statussearch.equals("Error from SAP")) {
                			instrumentSql += "";
                			//instrumentSql += "AND CASE WHEN x.MODE_OF_PAYMENT IN ('Cash') THEN x.SAP_API_RESPONSE "
                			//		+ " WHEN x.MODE_OF_PAYMENT IN ('Non Cash') THEN i.SAP_API_RESPONSE END IN ('E') ";//AND x.COMPANY_CODE != '' AND i.INSTRUMENT_ID != ''
                		} else {
                			instrumentSql += "AND x.DEALER_COLLETION_STATUS = 'issued' ";
                		}
            	
                if(!statussearch.isEmpty()) {
                	if(statussearch.equals("Ready to Post")) {
                		instrumentSql += " AND (x.DEPO_RECEIPT_STATUS = '"+statussearch+"' AND i.INSTRUMNET_STATUS IS NULL ) ";//OR i.INSTRUMNET_STATUS = '"+statussearch+"'
                	} else if(!statussearch.isEmpty() && statussearch.equals("Issued Receipts")) {
                		instrumentSql += " AND (x.DEALER_COLLETION_STATUS = 'issued' AND x.DEPO_RECEIPT_STATUS = '' ) ";//OR i.INSTRUMNET_STATUS = '"+statussearch+"'
                	} else if(!statussearch.isEmpty() && statussearch.equals("Error from SAP")) {
            			instrumentSql += "AND CASE WHEN x.MODE_OF_PAYMENT IN ('Cash') THEN x.DEPO_RECEIPT_STATUS "
            					+ " WHEN x.MODE_OF_PAYMENT IN ('Non Cash') THEN i.INSTRUMNET_STATUS END IN ('"+statussearch+"') ";//AND x.COMPANY_CODE != '' AND i.INSTRUMENT_ID != ''
            		} else {
                		instrumentSql += " AND CASE "
                    			+ "WHEN x.MODE_OF_PAYMENT IN ('Cash') THEN x.DEPO_RECEIPT_STATUS "
                    			+ "WHEN x.MODE_OF_PAYMENT IN ('Non Cash') THEN i.INSTRUMNET_STATUS END IN ( '"+statussearch+"' ) ";
                		
                		if(statussearch.equals("Post to SAP")) {
                			instrumentSql += " AND CASE "
                        			+ " WHEN x.MODE_OF_PAYMENT IN ('Cash') THEN x.SAP_API_RESPONSE "
                        			+ " WHEN x.MODE_OF_PAYMENT IN ('Non Cash') THEN i.SAP_API_RESPONSE END IN ( 'S' ) ";
                		}
                	}
                	
                	
                } else {
                	instrumentSql += " AND x.DEPO_RECEIPT_STATUS = 'Ready to Post' AND i.INSTRUMNET_STATUS IS NULL ";
                }
                if(!search.isEmpty()) {
                	instrumentSql += " AND (i.INSTRUMENT_NUMBER LIKE '%"+search+"%' OR x.APPROVED_BY LIKE '%"+search+"%' OR x.RECEIPT_NUMBER LIKE '%"+search+"%' OR x.DEALER_ID LIKE '%"+search+"%' OR  x.CREATED_BY LIKE '%"+search+"%' )";
                }
                
//                if(!fromdate.isEmpty() && !todate.isEmpty()) {
//
//                	instrumentSql += " AND ( (x.MODE_OF_PAYMENT = 'Cash' AND (SUBSTRING(x.CREATED_AT,0,10) >= '"+fromdate+"' AND SUBSTRING(x.CREATED_AT,0,10) <= '"+todate+"' )) OR (x.MODE_OF_PAYMENT = 'Non Cash' AND (SUBSTRING(i.INSTRUMENT_DATE,0,10) >= '"+fromdate+"' AND SUBSTRING(i.INSTRUMENT_DATE,0,10) <= '"+todate+"' )) )";
//
//                	}
                
//                if(!fromdate.isEmpty() && !todate.isEmpty()) {
//                	
//                	List<Date> dates = new ArrayList<Date>();
//                	DateFormat formatter;
//                    formatter = new SimpleDateFormat("yyyy-MM-dd");
//                	Date startDate = (Date) formatter.parse(fromdate);
//                	Date endDate = (Date) formatter.parse(todate);
//                	
//                	 long interval = 24 * 1000 * 60 * 60;
//                	    long endTime = endDate.getTime();
//                	    long curTime = startDate.getTime();
//                	    while (curTime <= endTime) 
//                	    {
//                	      dates.add(new Date(curTime));
//                	      curTime += interval;
//                	    }
//                	    String selectedDates = "";
//                	    for (int i = 0; i < dates.size(); i++) {
//                	      Date lDate = (Date) dates.get(i);
//                	      String ds = formatter.format(lDate);
//                	      System.out.println(" Date is ..." + ds);
//                	      selectedDates += "'"+Jsonre.glpostingDotDateFormat(ds)+"'";
//                	      if(i < (dates.size()-1)) {
//                	    	  selectedDates += ",";
//                	      }
//                	      
//                	    }
//                	    System.out.println(selectedDates);
//                	//instrumentSql += " AND (SUBSTRING(x.DATE_OF_RECEVING,0,10) >= '"+fromdate+"' AND SUBSTRING(x.DATE_OF_RECEVING,0,10) <= '"+todate+"' ) OR (SUBSTRING(i.INSTRUMENT_DATE,0,10) >= '"+Jsonre.glpostingHippenDateFormat(fromdate)+"' AND SUBSTRING(i.INSTRUMENT_DATE,0,10) <= '"+Jsonre.glpostingHippenDateFormat(todate)+"' ) ";
//                	//instrumentSql += " AND ((TO_DATE(x.DATE_OF_RECEVING,'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(x.DATE_OF_RECEVING,'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') ) OR (TO_DATE(i.INSTRUMENT_DATE,'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(i.INSTRUMENT_DATE,'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') )) ";
//                	if(statussearch.isEmpty() || statussearch.equals("Ready to Post")) {
//                		instrumentSql += " AND ((TO_DATE(x.DATE_OF_RECEVING,'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(x.DATE_OF_RECEVING,'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') ) OR (TO_DATE(i.INSTRUMENT_DATE,'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(i.INSTRUMENT_DATE,'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') )) ";
//                	} else {
//                		instrumentSql += " AND (CASE WHEN p.INSTRUMENT_TYPE = 'Cash' THEN p.DOCUMENT_DATE WHEN p.INSTRUMENT_TYPE != 'Cash' THEN p.INSTRUMENT_DATE END IN ("+selectedDates+"))";
//                		//((TO_DATE(REPLACE(p.DOCUMENT_DATE,'.','/'),'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(REPLACE(p.DOCUMENT_DATE,'.','/'),'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') ) OR (TO_DATE(REPLACE(p.INSTRUMENT_DATE,'.','/'),'DD/MM/YYYY') > = TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(REPLACE(p.INSTRUMENT_DATE,'.','/'),'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') ))
//                		//END BETWEEN TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY')
//                		//instrumentSql += " AND CASE WHEN p.INSTRUMENT_TYPE = 'Cash' THEN TO_DATE(REPLACE(p.DOCUMENT_DATE,'.','/'),'DD/MM/YYYY') WHEN p.INSTRUMENT_TYPE != 'Cash' THEN TO_DATE(REPLACE(p.INSTRUMENT_DATE,'.','/'),'DD/MM/YYYY') END BETWEEN TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') ";
//                		//instrumentSql += " AND ((x.RECEIPT_NUMBER IN (SELECT REMARKS FROM "+SCHEMA_TABLE+"."+DL_GL_POSTING_INSTRUMENTS+" WHERE (TO_DATE(REPLACE(DOCUMENT_DATE,'.','/'),'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(REPLACE(DOCUMENT_DATE,'.','/'),'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY')) AND INSTRUMENT_NUMBER = '' AND INSTRUMENT_TYPE IN ('Cash') AND SAP_STATUS_CODE ='S')) OR (i.RECEIPT_ID IN (SELECT REMARKS FROM "+SCHEMA_TABLE+"."+DL_GL_POSTING_INSTRUMENTS+" WHERE (TO_DATE(REPLACE(INSTRUMENT_DATE,'.','/'),'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(REPLACE(INSTRUMENT_DATE,'.','/'),'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY'))  AND INSTRUMENT_NUMBER != '' AND INSTRUMENT_TYPE NOT IN ('Cash') AND SAP_STATUS_CODE ='S')))";
//                	}
//                }
                instrumentSql += ") X ";
//                if(statussearch.isEmpty() || statussearch.equals("Ready to Post")) {
                		instrumentSql += "WHERE X.COMPANY_CODE  != ''  ";
//                		}
                		
                		if(!fromdate.isEmpty() && !todate.isEmpty()) {
                        	
                        	List<Date> dates = new ArrayList<Date>();
                        	DateFormat formatter;
                            formatter = new SimpleDateFormat("yyyy-MM-dd");
                        	Date startDate = (Date) formatter.parse(fromdate);
                        	Date endDate = (Date) formatter.parse(todate);
                        	
                        	 long interval = 24 * 1000 * 60 * 60;
                        	    long endTime = endDate.getTime();
                        	    long curTime = startDate.getTime();
                        	    while (curTime <= endTime) 
                        	    {
                        	      dates.add(new Date(curTime));
                        	      curTime += interval;
                        	    }
                        	    String selectedDates = "";
                        	    for (int i = 0; i < dates.size(); i++) {
                        	      Date lDate = (Date) dates.get(i);
                        	      String ds = formatter.format(lDate);
                        	      System.out.println(" Date is ..." + ds);
                        	      selectedDates += "'"+Jsonre.glpostingDotDateFormat(ds)+"'";
                        	      if(i < (dates.size()-1)) {
                        	    	  selectedDates += ",";
                        	      } 
                        	    }
                        	if(statussearch.isEmpty() || statussearch.equals("Ready to Post") ) { 
                        		instrumentSql += " AND ((TO_DATE(X.DATE_OF_RECEVING,'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(X.DATE_OF_RECEVING,'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') ) OR (TO_DATE(X.INSTRUMENT_DATE,'DD/MM/YYYY') >= TO_DATE('"+Jsonre.glpostingHippenDateFormat(fromdate)+"','DD/MM/YYYY') AND TO_DATE(X.INSTRUMENT_DATE,'DD/MM/YYYY') <= TO_DATE('"+Jsonre.glpostingHippenDateFormat(todate)+"','DD/MM/YYYY') )) ";
                        	} else {
                        		//instrumentSql += " AND (CASE WHEN X.INSTRUMENTTYPE = 'Cash' THEN X.DOCUMENTDATE WHEN X.INSTRUMENTTYPE != 'Cash' THEN X.INSTRUMENTDATE END IN ("+selectedDates+"))";
//                        		instrumentSql += " AND X.RECEIPT_NUMBER IN ( SELECT y.REMARKS FROM AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS y "
//                        				+ "WHERE CASE WHEN y.INSTRUMENT_TYPE = 'Cash' THEN y.DOCUMENT_DATE WHEN y.INSTRUMENT_TYPE != 'Cash' THEN y.INSTRUMENT_DATE END IN ("+selectedDates+") AND ";
//                        		if(statussearch.equals("Post to SAP")) {
//                        			instrumentSql += " y.SAP_STATUS_CODE = 'S' AND ";
//                        		}
//                        		if(statussearch.equals("Mark AS Invalid")) {
//                        			instrumentSql += " y.SAP_STATUS_RESPONSE = 'Mark AS Invalid' AND ";
//                        		}
//                        		if(statussearch.equals("Error from SAP")) {
//                        			instrumentSql += " y.SAP_STATUS_CODE = 'E' AND ";
//                        		}
//                        		//SELECT CONCAT(y.REMARKS,y.INSTRUMENT_NUMBER) FROM (SELECT row_number() over (partition by REMARKS,INSTRUMENT_NUMBER order by CREATED_AT DESC) as row_num, f.* FROM AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS f) y WHERE y.row_num = 1 
//                        		instrumentSql += " y.COMPANY_CODE IN ('"+countryid+"') ORDER BY CREATED_AT DESC )";
                        		
                        		instrumentSql += "AND CONCAT(X.RECEIPT_NUMBER,IFNULL(X.INSTRUMENT_NUMBER,'')) IN ( SELECT CONCAT(y.REMARKS,IFNULL(y.INSTRUMENT_NUMBER,'')) FROM (SELECT row_number() over (partition by REMARKS,INSTRUMENT_NUMBER order by CREATED_AT DESC) as row_num, f.* FROM AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS f) y WHERE y.row_num = 1 AND CASE WHEN y.INSTRUMENT_TYPE = 'Cash' THEN y.DOCUMENT_DATE WHEN y.INSTRUMENT_TYPE != 'Cash' THEN y.INSTRUMENT_DATE END IN ("+selectedDates+") AND ";
                        		if(statussearch.equals("Post to SAP")) {
                        			instrumentSql += " y.SAP_STATUS_CODE = 'S' AND ";
                        		}
                        		if(statussearch.equals("Mark AS Invalid")) {
                        			instrumentSql += " y.SAP_STATUS_RESPONSE = 'Mark AS Invalid' AND ";
                        		}
                        		if(statussearch.equals("Error from SAP")) {
                        			instrumentSql += " y.SAP_STATUS_CODE = 'E' AND ";
                        		}
                        		instrumentSql += " y.COMPANY_CODE IN ('"+countryid+"') ORDER BY CREATED_AT DESC )";
                        	}
                        }
                		
                instrumentSql += " ORDER BY X.UPDATED_AT DESC ";
//            } else {
//            	 String selectedDates = "";
//            	if(!fromdate.isEmpty() && !todate.isEmpty()) {
//                	
//                	List<Date> dates = new ArrayList<Date>();
//                	DateFormat formatter;
//                    formatter = new SimpleDateFormat("yyyy-MM-dd");
//                	Date startDate = (Date) formatter.parse(fromdate);
//                	Date endDate = (Date) formatter.parse(todate);
//                	
//                	 long interval = 24 * 1000 * 60 * 60;
//                	    long endTime = endDate.getTime();
//                	    long curTime = startDate.getTime();
//                	    while (curTime <= endTime) 
//                	    {
//                	      dates.add(new Date(curTime));
//                	      curTime += interval;
//                	    }
//                	   
//                	    for (int i = 0; i < dates.size(); i++) {
//                	      Date lDate = (Date) dates.get(i);
//                	      String ds = formatter.format(lDate);
//                	      System.out.println(" Date is ..." + ds);
//                	      selectedDates += "'"+Jsonre.glpostingDotDateFormat(ds)+"'";
//                	      if(i < (dates.size()-1)) {
//                	    	  selectedDates += ",";
//                	      } 
//                	    }
//
//                }
//            	
//                instrumentSql += "SELECT *, (SELECT NAME FROM AP_GLOBAL.DL_SETTINGS_TABLE WHERE CATEGORY ='bs' AND COMPANY_MASTER_CODE = COMPANY_CODE FOR JSON) AS bsdata, (SELECT st.SAP_BANK_CODE FROM AP_GLOBAL.DL_SETTINGS_TABLE st WHERE st.NAME =INSTRUMENT_BANK_CODE AND COMPANY_MASTER_CODE = COMPANY_CODE AND CATEGORY = 'bank') as bankcode, (SELECT IFNULL(NAME,'') AS bankName,IFNULL(SAP_BANK_CODE,'') AS bankCode  FROM AP_GLOBAL.DL_SETTINGS_TABLE WHERE  CATEGORY = 'bank' AND COMPANY_MASTER_CODE = COMPANY_CODE GROUP BY NAME,SAP_BANK_CODE FOR JSON ) AS bankDropdowns FROM (SELECT x.DEALER_ID,"
//                		+ "x.DEPO_RECEIPT_STATUS,"
//                		+ "x.DEALER_REMARKS,"
//                		+ "i.SAP_MESSAGE_LOG AS RSAP_MESSAGE_LOG,i.SAP_MESSAGE_LOG AS ISAP_MESSAGE_LOG,"
//                		+ "x.MODE_OF_PAYMENT,"
//                		+ "x.RECEIPT_AMOUNT,"
//                		+ "x.SAP_DOC_NUM AS ISAP_DOC_NUM,x.SAP_DOC_NUM,"
//                		+ "x.DEPO_RECEIPT_STATUS,"
//                		+ "IFNULL(i.INSTRUMNET_STATUS, x.DEPO_RECEIPT_STATUS) AS INSTRUMNET_STATUS,i.INSTRUMENT_BANK_ACCOUNT_NUMBER,"
//                		+ "i.INSTRUMENT_AMOUNT,"
//                		+ "i.INSTRUMENT_BANK_BRANCH,"
//                		+ "i.INSTRUMENT_BANK_CODE,"
//                		+ "i.INSTRUMENT_MICR_NUMBER,"
//                		+ "i.RECEIPT_ID,"
//                		+ "i.INSTRUMENT_DATE_OF_RECEVING,"
//                		+ "i.INSTRUMENT_ID,"
//                		+ "i.INSTRUMENT_REMARKS,"
//                		+ "p.COMPANY_CODE,"
//                		+ "p.APPROVED_BY,"
//                		+ "p.CREATED_BY,"
//                		+ "p.CREATED_AT,"
//                		+ "p.REMARKS AS RECEIPT_NUMBER,"
//                		+ "p.INSTRUMENT_NUMBER,p.INSTRUMENT_DATE,"
//                		+ "p.INSTRUMENT_TYPE,"
//                		+ "p.UPDATED_AT,"
//                		+ "p.DOCUMENT_DATE AS DATE_OF_RECEVING,"
//                		+ "p.CREATED_BY FROM AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS p "
//                		+ "RIGHT JOIN AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS i ON i.SAP_DOC_NUM = p.SAP_DOCUMENT_NUMBER "
//                		+ "RIGHT JOIN AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS x ON x.SAP_DOC_NUM = p.SAP_DOCUMENT_NUMBER "
//                		+ "WHERE ";
//                instrumentSql += " x.COMPANY_CODE = '"+countryid+"' AND x.COMPANY_CODE IN  ("+listCountrieslist+") ";
//                if(!fromdate.isEmpty() && !todate.isEmpty()) {
//                instrumentSql += " AND (CASE WHEN p.INSTRUMENT_TYPE = 'Cash' THEN p.DOCUMENT_DATE WHEN p.INSTRUMENT_TYPE != 'Cash' THEN p.INSTRUMENT_DATE END IN ("+selectedDates+") )";
//                }
//                instrumentSql += " AND p.SAP_STATUS_CODE = 'S' ";
//                instrumentSql += " ORDER BY p.CREATED_AT DESC ) X";
//            }
               System.out.println(instrumentSql);
            int numberOfColumns = this.getRowsCount(instrumentSql);
            String originalSqlQuery;
            if(page.equals("0")) {
            	originalSqlQuery = instrumentSql;
            } else {
            	originalSqlQuery = instrumentSql + " limit " + perPageLimit + " offset " + offset;
            }
      System.out.println(originalSqlQuery);
            ResultSet instrumentResultSet = stmt.executeQuery(originalSqlQuery);
            ArrayList<Glpostingmodel> instrumentArraydata = new ArrayList<>();
            Integer number = 1;
            while(instrumentResultSet.next()) {
            	String documentDate;
            	String amount;
            	String bankCode;
            	String text;
            	String statusreceipt;
            	String remarksinformation;
       
            	String fiscalYear = "";
            	String SapdocumentNumber = "";
            	String Sapaccountid = "";
            	String Saphousebank = "";
            	String Sapbussinessarea = "";
            	String BankCharges = "";
            	String PostingDate = "";
            	String Specialglind = "";
            	String SapMessage = "";
            	String SapDocumentDate = "";
            	String SapInstrumentNumber = "";
            	String SapInstrumentDate = "";
            	String SapstatusCode = "";
            	String SapinstrumentType = "";
            	String SapBankbranch = "";
            	String setSapDocumentNumber = "";
            	String SetSapBankCode = "";
            	Object instrumentypenames = "";
            	
            	if(instrumentResultSet.getString("MODE_OF_PAYMENT").toString().equals("Non Cash")) {
            		documentDate = instrumentResultSet.getString("INSTRUMENT_DATE_OF_RECEVING") != null ? Jsonre.glpostingStartDateFormat(instrumentResultSet.getString("INSTRUMENT_DATE_OF_RECEVING").toString()) : "";
            		amount = instrumentResultSet.getString("INSTRUMENT_AMOUNT") != null ? instrumentResultSet.getString("INSTRUMENT_AMOUNT").toString() : "";
            		bankCode = instrumentResultSet.getString("bankcode") != null ? instrumentResultSet.getString("bankcode").toString() : "";
            		text = (instrumentResultSet.getString("RECEIPT_NUMBER") != null ? instrumentResultSet.getString("RECEIPT_NUMBER").toString() : "" )+ "-" +( instrumentResultSet.getString("INSTRUMENT_REMARKS") != null ? instrumentResultSet.getString("INSTRUMENT_REMARKS").toString() : "");
            		statusreceipt = instrumentResultSet.getString("INSTRUMNET_STATUS") != null ? instrumentResultSet.getString("INSTRUMNET_STATUS").toString() : "";
            		remarksinformation = instrumentResultSet.getString("RECEIPT_ID") != null ? instrumentResultSet.getString("RECEIPT_ID").toString() : "";
            		instrumentypenames = this.gettheinstrumenttypes();
            		ResultSet glpostingInformation = this.getGlpostinformation(instrumentResultSet.getString("RECEIPT_ID").toString(),instrumentResultSet.getString("INSTRUMENT_NUMBER").toString(),instrumentResultSet.getString("MODE_OF_PAYMENT").toString());
            		if(glpostingInformation.next()) {
//                		JSONArray GIPOSTDATARRAY = new JSONArray(instrumentResultSet.getString("GIPOSTDATA").toString());
//                		JSONObject GIPOSTDATA = new JSONObject(GIPOSTDATARRAY.get(0).toString());
                		fiscalYear = glpostingInformation.getString("FISCAL_YEAR").toString();
//                		SapinstrumentType = glpostingInformation.getString("INSTRUMENT_TYPE") != null ? glpostingInformation.getString("INSTRUMENT_TYPE").toString() : "";
                		Sapaccountid = glpostingInformation.getString("ACCOUNT_ID").toString();
                		Saphousebank = glpostingInformation.getString("HOUSE_BANK").toString();
                		Sapbussinessarea = glpostingInformation.getString("BUSSINESS_AREA").toString();
                		BankCharges = glpostingInformation.getString("BANK_CHARGES").toString();
                		PostingDate = glpostingInformation.getString("POSTING_DATE").toString();
                		Specialglind = glpostingInformation.getString("SPECIAL_GL_IND").toString();
                		SapstatusCode = glpostingInformation.getString("SAP_STATUS_CODE").toString();
                		SapBankbranch = glpostingInformation.getString("BANK_BRANCH").toString();
                		SapDocumentDate = glpostingInformation.getString("DOCUMENT_DATE") != null ? glpostingInformation.getString("DOCUMENT_DATE").toString() : "";
                		SapInstrumentNumber = glpostingInformation.getString("INSTRUMENT_NUMBER") != null ? glpostingInformation.getString("INSTRUMENT_NUMBER").toString() : "";
                		SapInstrumentDate = glpostingInformation.getString("INSTRUMENT_DATE") != null ? glpostingInformation.getString("INSTRUMENT_DATE").toString() : "";
                		SapdocumentNumber = glpostingInformation.getString("SAP_DOCUMENT_NUMBER")!= null ? glpostingInformation.getString("SAP_DOCUMENT_NUMBER").toString() : "";
                		setSapDocumentNumber = glpostingInformation.getString("DOCUMENT_NUMBER")!= null ? glpostingInformation.getString("DOCUMENT_NUMBER").toString() : "";
                		SetSapBankCode = glpostingInformation.getString("BANK_CODE")!= null ? glpostingInformation.getString("BANK_CODE").toString() : "";
                		SapinstrumentType = glpostingInformation.getString("INSTRUMENT_TYPE")!= null ? glpostingInformation.getString("INSTRUMENT_TYPE").toString() : "";
                	}
            		
            	} else {
            		documentDate = instrumentResultSet.getString("DATE_OF_RECEVING") != null ? Jsonre.glpostingStartDateFormat(instrumentResultSet.getString("DATE_OF_RECEVING").toString()) : "";
            		amount = instrumentResultSet.getString("RECEIPT_AMOUNT") != null ? instrumentResultSet.getString("RECEIPT_AMOUNT").toString() : "";
            		text = (instrumentResultSet.getString("RECEIPT_NUMBER") != null ? instrumentResultSet.getString("RECEIPT_NUMBER").toString() : "" )+ "-" + (instrumentResultSet.getString("DEALER_REMARKS") != null ? instrumentResultSet.getString("DEALER_REMARKS").toString() : "");
            		bankCode = "";
            		statusreceipt = instrumentResultSet.getString("DEPO_RECEIPT_STATUS") != null ? instrumentResultSet.getString("DEPO_RECEIPT_STATUS").toString() : "";
            		remarksinformation = instrumentResultSet.getString("RECEIPT_NUMBER") != null ? instrumentResultSet.getString("RECEIPT_NUMBER").toString() : "";
            		ResultSet glpostingInformation = this.getGlpostinformation(instrumentResultSet.getString("RECEIPT_NUMBER"),"",instrumentResultSet.getString("MODE_OF_PAYMENT").toString());
            		//SapinstrumentType = "Cash";
            		String[] cashinstrument = {"Cash"};
            		instrumentypenames = cashinstrument;
            		if(glpostingInformation.next()) {
//                		JSONArray GIPOSTDATARRAY = new JSONArray(instrumentResultSet.getString("GIPOSTDATA").toString());
//                		JSONObject GIPOSTDATA = new JSONObject(GIPOSTDATARRAY.get(0).toString());
                		fiscalYear = glpostingInformation.getString("FISCAL_YEAR").toString();
                		Sapaccountid = glpostingInformation.getString("ACCOUNT_ID").toString();
                		Saphousebank = glpostingInformation.getString("HOUSE_BANK").toString();
                		Sapbussinessarea = glpostingInformation.getString("BUSSINESS_AREA").toString();
                		BankCharges = glpostingInformation.getString("BANK_CHARGES").toString();
                		PostingDate = glpostingInformation.getString("POSTING_DATE").toString();
                		Specialglind = glpostingInformation.getString("SPECIAL_GL_IND").toString();
                		SapstatusCode = glpostingInformation.getString("SAP_STATUS_CODE").toString();
                		SapBankbranch = glpostingInformation.getString("BANK_BRANCH").toString();
                		SapDocumentDate = glpostingInformation.getString("DOCUMENT_DATE") != null ? glpostingInformation.getString("DOCUMENT_DATE").toString() : "";
                		SapInstrumentNumber = glpostingInformation.getString("INSTRUMENT_NUMBER") != null ? glpostingInformation.getString("INSTRUMENT_NUMBER").toString() : "";
                		SapInstrumentDate = glpostingInformation.getString("INSTRUMENT_DATE") != null ? glpostingInformation.getString("INSTRUMENT_DATE").toString() : "";
                		SapdocumentNumber = glpostingInformation.getString("SAP_DOCUMENT_NUMBER")!= null ? glpostingInformation.getString("SAP_DOCUMENT_NUMBER").toString() : "";
                		setSapDocumentNumber = glpostingInformation.getString("DOCUMENT_NUMBER")!= null ? glpostingInformation.getString("DOCUMENT_NUMBER").toString() : "";
                		SetSapBankCode = glpostingInformation.getString("BANK_CODE")!= null ? glpostingInformation.getString("BANK_CODE").toString() : "";
                		SapinstrumentType = glpostingInformation.getString("INSTRUMENT_TYPE")!= null ? glpostingInformation.getString("INSTRUMENT_TYPE").toString() : "";
                	}
            	}
            	
            	if(instrumentResultSet.getString("INSTRUMENT_ID") != null) {
            		SapMessage =  instrumentResultSet.getString("ISAP_MESSAGE_LOG") !=null ? instrumentResultSet.getString("ISAP_MESSAGE_LOG").toString() : "";
            	} else {
            		SapMessage =  instrumentResultSet.getString("RSAP_MESSAGE_LOG") !=null ? instrumentResultSet.getString("RSAP_MESSAGE_LOG").toString() : "";
            	}
            	
            	Glpostingmodel glpostmodel = new Glpostingmodel();
            	glpostmodel.setAccountnumber(instrumentResultSet.getString("INSTRUMENT_BANK_ACCOUNT_NUMBER") != null ? instrumentResultSet.getString("INSTRUMENT_BANK_ACCOUNT_NUMBER").toString() : "");
            	glpostmodel.setCompanycode(instrumentResultSet.getString("COMPANY_CODE") != null ? instrumentResultSet.getString("COMPANY_CODE").toString() : "");
            	glpostmodel.setAmount(amount);
            	glpostmodel.setApporvedBy(instrumentResultSet.getString("APPROVED_BY") != null ? instrumentResultSet.getString("APPROVED_BY").toString() : "");
            	glpostmodel.setBankBranch(instrumentResultSet.getString("INSTRUMENT_MICR_NUMBER") != null ? instrumentResultSet.getString("INSTRUMENT_MICR_NUMBER").toString() : "");
            	
            	glpostmodel.setBussinessarea(instrumentResultSet.getString("bsdata") != null ? new JSONArray(instrumentResultSet.getString("bsdata")) : "");
            	glpostmodel.setCurrency(instrumentResultSet.getString("COMPANY_CODE") != null ? this.getcurrencyName(instrumentResultSet.getString("COMPANY_CODE").toString()) : "");
            	glpostmodel.setCustomername(instrumentResultSet.getString("DEALER_ID") != null ? instrumentResultSet.getString("DEALER_ID").toString() : "");
            	
            	
            	glpostmodel.setFiscalYear(fiscalYear);
            	
            	glpostmodel.setInstrumentid(instrumentResultSet.getString("INSTRUMENT_ID") != null ? instrumentResultSet.getString("INSTRUMENT_ID").toString() : "");
            	
            	
            	glpostmodel.setLineitem(number);
            	glpostmodel.setPostingDate(PostingDate);
            	glpostmodel.setRemarks(remarksinformation);
            	/*if(statussearch.equals("Post to SAP")) {
                        			instrumentSql += " y.SAP_STATUS_CODE = 'S' AND ";
                        		}
                        		if(statussearch.equals("Mark AS Invalid")) {
                        			instrumentSql += " y.SAP_STATUS_RESPONSE = 'Mark AS Invalid' AND ";
                        		}
                        		if(statussearch.equals("Error from SAP")) {
                        			instrumentSql += " y.SAP_STATUS_CODE = 'E' AND ";
                        		}*/
            	 if(!statussearch.isEmpty() && statussearch.equals("Post to SAP")) {
            		glpostmodel.setDocumentNumber(setSapDocumentNumber);
            		glpostmodel.setBankCode(SetSapBankCode);
            		glpostmodel.setInstrumentnumber(SapInstrumentNumber);
            		glpostmodel.setIstumenttype(SapinstrumentType);
            		glpostmodel.setDocumentDate(SapDocumentDate);
            		glpostmodel.setSapInstrumentDate(SapInstrumentDate);
            		glpostmodel.setInstrumentdate(SapInstrumentDate);
            	} else if(!statussearch.isEmpty() && statussearch.equals("Error from SAP")) {
            		glpostmodel.setDocumentNumber(setSapDocumentNumber);
            		glpostmodel.setBankCode(SetSapBankCode);
            		glpostmodel.setInstrumentnumber(SapInstrumentNumber);
            		glpostmodel.setIstumenttype(SapinstrumentType);
            		glpostmodel.setDocumentDate(SapDocumentDate);
            		glpostmodel.setSapInstrumentDate(SapInstrumentDate);
            		glpostmodel.setInstrumentdate(SapInstrumentDate);
            	} else {
            		glpostmodel.setDocumentDate(documentDate);
            		glpostmodel.setDocumentNumber(instrumentResultSet.getString("INSTRUMENT_ID") != null ? instrumentResultSet.getString("INSTRUMENT_ID").toString() : "");
            		glpostmodel.setBankCode(bankCode);
            		glpostmodel.setInstrumentnumber(instrumentResultSet.getString("INSTRUMENT_NUMBER") != null ? instrumentResultSet.getString("INSTRUMENT_NUMBER").toString() : "");
            		if(instrumentResultSet.getString("MODE_OF_PAYMENT").toString().equals("Non Cash")) {
            		glpostmodel.setIstumenttype(instrumentResultSet.getString("INSTRUMENT_TYPE") != null ? instrumentResultSet.getString("INSTRUMENT_TYPE").toString() : "");
            		} else {
            			glpostmodel.setIstumenttype("Cash");
            		}
            		glpostmodel.setSapInstrumentDate(instrumentResultSet.getString("INSTRUMENT_DATE") != null ? Jsonre.glpostingStartDateFormat(instrumentResultSet.getString("INSTRUMENT_DATE").toString()) : "");
            		glpostmodel.setInstrumentdate(instrumentResultSet.getString("INSTRUMENT_DATE") != null ? Jsonre.glpostingStartDateFormat(instrumentResultSet.getString("INSTRUMENT_DATE").toString()) : "");
            	}
            	
            	glpostmodel.setSpecialglind(Specialglind);
            	glpostmodel.setBankCharges(BankCharges);
            	glpostmodel.setText(text);
            	
            	glpostmodel.setMicrnumber(instrumentResultSet.getString("INSTRUMENT_BANK_BRANCH") != null ? instrumentResultSet.getString("INSTRUMENT_BANK_BRANCH").toString() : "");
            	glpostmodel.setGldropdown(instrumentResultSet.getString("COMPANY_CODE") != null ? this.companywiseDropdowndata(instrumentResultSet.getString("COMPANY_CODE").toString()) : new String[0]);
            	glpostmodel.setSalesUserId(instrumentResultSet.getString("CREATED_BY") != null ? instrumentResultSet.getString("CREATED_BY").toString() : "");
            	glpostmodel.setSapaccountid(Sapaccountid);
            	glpostmodel.setSaphousebank(Saphousebank);
            	glpostmodel.setSapbussinessarea(Sapbussinessarea);
            	glpostmodel.setStatus(statusreceipt);
            	glpostmodel.setSapMessage(SapMessage);
            	glpostmodel.setSapDocumentDate(SapDocumentDate);
            	glpostmodel.setSapInstrumentNumber(SapInstrumentNumber);
            	
            	glpostmodel.setSapBankbranch(SapBankbranch);
            	glpostmodel.setSapdocumentNumber(SapdocumentNumber);
            	glpostmodel.setSapStatusCode(SapstatusCode);
            	glpostmodel.setInstrumentypenames(instrumentypenames);
            	glpostmodel.setBankNamesList(new JSONArray(instrumentResultSet.getString("bankDropdowns").toString()));
            	instrumentArraydata.add(glpostmodel);
            	number++;
            }
            double numofpages = (double)numberOfColumns / perPageLimit;
            JSONObject responseMap  = new JSONObject();
            responseMap.put(STATUS_CODE, SUCCESS_CODE);
            responseMap.put(STATUS_MESSAGE, SUCCESS);
            responseMap.put("numofpages",Math.ceil(numofpages));
            responseMap.put("totalrows",numberOfColumns);
            responseMap.put("data", instrumentArraydata);
            return responseMap;
        } catch (SQLException | JSONException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return validate.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
        }
	}
	private void posttoglpostingtable(String aCCOUNTID, String cOMPANYCODE, String bUSSINESSAREA, String hOUSEBANK, String dOCUMENTDATE, String pOSTINGDATE, String cUSTOMER, String sPECIALDLIND, String aMOUNT, String cURRENCY, String bANK_CHARGES, String bANK_CODE, String bANK_BRANCH, String iNSTEUMENT_TYPE, String aRROVED_BY, String iNSTRUMENT_DATE, String iNSTRUMENT_NUMBER, String rEMARKS, String tEXT, String lINE_ITEM, String dOCUMENT_NUMBER, String fISCAL_YEAR, String userid, String actiontype, String instrumentnumber) {
		try {
			Timestamp createdAt = new Timestamp(System.currentTimeMillis());
			String insertQueryGl = "INSERT INTO "+SCHEMA_TABLE+"."+DL_GL_POSTING_INSTRUMENTS+" ("
					+ "ACCOUNT_ID,"
					+ "COMPANY_CODE,"
					+ "BUSSINESS_AREA,"
					+ "HOUSE_BANK,"
					+ "DOCUMENT_DATE,"
					+ "POSTING_DATE,"
					+ "CUSTOMER,"
					+ "SPECIAL_GL_IND,"
					+ "AMOUNT,"
					+ "CURRENCY,"
					+ "BANK_CHARGES,"
					+ "BANK_CODE,"
					+ "BANK_BRANCH,"
					+ "INSTRUMENT_TYPE,"
					+ "APPROVED_BY,"
					+ "INSTRUMENT_DATE,"
					+ "INSTRUMENT_NUMBER,"
					+ "REMARKS,"
					+ "TEXT,"
					+ "LINE_ITEM,"
					+ "DOCUMENT_NUMBER,"
					+ "FISCAL_YEAR,"
					+ "SAP_DOCUMENT_NUMBER,"
					+ "CREATED_AT,"
					+ "CREATED_BY,"
					+ "UPDATED_AT,"
					+ "UPDATED_BY,"
					+ "SAP_STATUS_CODE,"
					+ "SAP_STATUS_RESPONSE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement AREAINSERTQUERYEXE = connection.prepareStatement(insertQueryGl);
			AREAINSERTQUERYEXE.setString(1, aCCOUNTID);
			AREAINSERTQUERYEXE.setString(2, cOMPANYCODE);
			AREAINSERTQUERYEXE.setString(3, bUSSINESSAREA);
			AREAINSERTQUERYEXE.setString(4, hOUSEBANK);
			AREAINSERTQUERYEXE.setString(5, dOCUMENTDATE);
			AREAINSERTQUERYEXE.setString(6, pOSTINGDATE);
			AREAINSERTQUERYEXE.setString(7, cUSTOMER);
			AREAINSERTQUERYEXE.setString(8, sPECIALDLIND);
			AREAINSERTQUERYEXE.setString(9, aMOUNT);
			AREAINSERTQUERYEXE.setString(10, cURRENCY);
			AREAINSERTQUERYEXE.setString(11, bANK_CHARGES);
			AREAINSERTQUERYEXE.setString(12, bANK_CODE);
			AREAINSERTQUERYEXE.setString(13, bANK_BRANCH);
			AREAINSERTQUERYEXE.setString(14, iNSTEUMENT_TYPE);
			AREAINSERTQUERYEXE.setString(15, aRROVED_BY);
			AREAINSERTQUERYEXE.setString(16, iNSTRUMENT_DATE);
			AREAINSERTQUERYEXE.setString(17, iNSTRUMENT_NUMBER);
			AREAINSERTQUERYEXE.setString(18, rEMARKS);
			AREAINSERTQUERYEXE.setString(19, tEXT);
			AREAINSERTQUERYEXE.setString(20, lINE_ITEM);
			AREAINSERTQUERYEXE.setString(21, dOCUMENT_NUMBER);
			AREAINSERTQUERYEXE.setString(22, fISCAL_YEAR);
			AREAINSERTQUERYEXE.setString(23, "");
			AREAINSERTQUERYEXE.setString(24, createdAt.toString());
			AREAINSERTQUERYEXE.setString(25, userid);
			AREAINSERTQUERYEXE.setString(26, createdAt.toString());
			AREAINSERTQUERYEXE.setString(27, userid);
			AREAINSERTQUERYEXE.setString(28, "");
			AREAINSERTQUERYEXE.setString(29, actiontype.equals("sap") ? "" : "Mark AS Invalid");
			AREAINSERTQUERYEXE.executeUpdate();
			AREAINSERTQUERYEXE.close();
			
			
			if(!rEMARKS.isEmpty() && !iNSTRUMENT_NUMBER.isEmpty() && !instrumentnumber.isEmpty()) {
				PreparedStatement updateQryPrepare;
				String updateQry = " UPDATE AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS I SET I.INSTRUMENT_NUMBER = '"+iNSTRUMENT_NUMBER+"' WHERE I.RECEIPT_ID = '"+rEMARKS.toString()+"' AND I.INSTRUMENT_NUMBER = '"+instrumentnumber.toString()+"' ";
                	System.out.println(updateQry);
                updateQryPrepare = connection.prepareStatement(updateQry);
                updateQryPrepare.execute();
                updateQryPrepare.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage().toString());
		}
	}

	@SuppressWarnings("unused")
	public Object posttoSapglData(JsonObject data) {
		System.out.println("/////");
		// TODO Auto-generated method stub
		ArrayList<String> errorIdsList =new ArrayList<String>();
		JSONObject ECOLLECTION = new JSONObject();
		String userid = data.get("userid").getAsString();
		try {
			ArrayList<Glpostingrequest> glpostingData = new ArrayList<>();
			JsonArray gldata = data.get("gldata").getAsJsonArray();
			
			System.err.println(01);
			String actiontype = data.get("actiontype").getAsString();
			Integer g = 0;
			System.err.println(1);
		do {
			JSONObject gljsonData = new JSONObject(gldata.get(g).toString());
			String instrumentnumber = gljsonData.getString("instrumentnumber").toString();
			String ACCOUNTID = gljsonData.getString("ACCOUNT_ID").toString();
			String COMPANYCODE = gljsonData.getString("COMPANY_CODE").toString();
			String BUSSINESSAREA = gljsonData.getString("BUSSINESS_AREA").toString();
			String HOUSEBANK = gljsonData.getString("HOUSE_BANK").toString();
			String DOCUMENTDATE = gljsonData.getString("DOCUMENT_DATE").toString();
			String POSTINGDATE = gljsonData.getString("POSTING_DATE").toString();
			String CUSTOMER = gljsonData.getString("CUSTOMER").toString();
			String SPECIALDLIND = gljsonData.getString("SPECIAL_D_L_IND").toString();
			String AMOUNT = gljsonData.getString("AMOUNT").toString();
			String CURRENCY = gljsonData.getString("CURRENCY").toString();
			String BANK_CHARGES = gljsonData.getString("BANK_CHARGES").toString();
			String BANK_CODE = gljsonData.getString("BANK_CODE").toString();
			String BANK_BRANCH = gljsonData.getString("BANK_BRANCH").toString();
			String INSTEUMENT_TYPE = gljsonData.getString("INSTEUMENT_TYPE").toString();
			String ARROVED_BY = gljsonData.getString("ARROVED_BY").toString();
			String INSTRUMENT_DATE = gljsonData.getString("INSTRUMENT_DATE").toString();
			String INSTRUMENT_NUMBER = gljsonData.getString("INSTRUMENT_NUMBER").toString();
			String REMARKS = gljsonData.getString("REMARKS").toString();
			String TEXT = gljsonData.getString("TEXT").toString();
			String LINE_ITEM = gljsonData.getString("LINE_ITEM").toString();
			String DOCUMENT_NUMBER = gljsonData.getString("DOCUMENT_NUMBER").toString();
			String FISCAL_YEAR = gljsonData.getString("FISCAL_YEAR").toString();
			
			String[] AmountArray = AMOUNT.split(".0");
			
			String amountwithoutcommas = AmountArray[0].replace(",", "");
			String finalAmount = AMOUNT.replace(",", "");//amountwithoutcommas+".0"+AmountArray[1];
			
			Glpostingrequest sapJsonData = new Glpostingrequest();
			sapJsonData.setMANDT("");
			sapJsonData.setUTR_NO(INSTRUMENT_NUMBER);
			sapJsonData.setTRANS_ID(REMARKS);
			sapJsonData.setTRANS_TYPE("");
			sapJsonData.setDR_CR("C");
			sapJsonData.setENTRY_AMOUNT(finalAmount);
			sapJsonData.setVALUE_DATE(POSTINGDATE);
			sapJsonData.setCUST_CODE(CUSTOMER);
			sapJsonData.setCUST_NAME(CURRENCY);
			sapJsonData.setZONECODE(BANK_CODE);
			sapJsonData.setCOLLECTION_CENTER(BUSSINESSAREA);
			sapJsonData.setCUST_ACC_NO("");
			sapJsonData.setLOCATION(BANK_BRANCH);
			sapJsonData.setREMITTING_BANK("");
			sapJsonData.setSTART_DATE(DOCUMENTDATE);
			sapJsonData.setEND_DATE(INSTRUMENT_DATE);
			sapJsonData.setCLIENT_CODE(ACCOUNTID);
			sapJsonData.setCLIENT_NAME(HOUSEBANK);
			//sapJsonData.setCLIENT_NAME(ACCOUNTID);
			sapJsonData.setCLIENT_COMPANY_CODE(COMPANYCODE);
			sapJsonData.setCLIENT_ACC_NO(ACCOUNTID);
			sapJsonData.setSTATUS(INSTEUMENT_TYPE);
			sapJsonData.setSTATUS_MESSAGE(TEXT);
			sapJsonData.setCREATED_BY("");
			sapJsonData.setEXEDATE("");
			sapJsonData.setEXETIME("");
			sapJsonData.setCR_DOC_NO("");
			glpostingData.add(sapJsonData);
			
			this.posttoglpostingtable(ACCOUNTID,COMPANYCODE,BUSSINESSAREA,HOUSEBANK,DOCUMENTDATE,POSTINGDATE,CUSTOMER,SPECIALDLIND,AMOUNT,CURRENCY,BANK_CHARGES,BANK_CODE,BANK_BRANCH,INSTEUMENT_TYPE,ARROVED_BY,INSTRUMENT_DATE,INSTRUMENT_NUMBER,REMARKS,TEXT,LINE_ITEM,DOCUMENT_NUMBER,FISCAL_YEAR,userid,actiontype,instrumentnumber);
			if(!actiontype.equals("sap")) { this.updatethestatustoMarkAsInvalid(REMARKS, COMPANYCODE); }
			
			g++;
		} while (g < gldata.size());
		
		if(actiontype.equals("sap")) {
			
			ECOLLECTION.put("MT_ECOLLECTION_REQ", new JSONObject().put("ZRFC_APG_ECOLLECTION_POSTING1", new JSONObject().put("IT_ZECOLLECTION_APG", new JSONObject().put("item", glpostingData ))));	
			System.out.println(ECOLLECTION);
			OkHttpClient client = new OkHttpClient().newBuilder()
					  .build();
					MediaType mediaType = MediaType.parse("application/json");
					RequestBody body = RequestBody.create(mediaType, ECOLLECTION.toString());
					Request request = new Request.Builder()
					  .url(SI_SO_PO_CREATE)
					  .method("POST", body)
					  .addHeader("Content-Type", "application/json")
					  .build();
					Response response = client.newCall(request).execute();
					String responseData = response.body().string();
					JSONObject jsonObject = new JSONObject(responseData);
					System.out.println(jsonObject.toString());
					JSONObject MTECOLLECTIONRES = new JSONObject(jsonObject.get("MT_ECOLLECTION_RES").toString());
					Object ITRETURN = MTECOLLECTIONRES.getJSONObject("ZRFC_APG_ECOLLECTION_POSTING1.Response").getJSONObject("IT_RETURN").get("item");
					
					if(ITRETURN.getClass().getName().equals("org.json.JSONObject")) {
						JSONObject itemsObjectData = new JSONObject(ITRETURN.toString());
//						if(itemsObjectData.get("RECEIPT_NUMBER"))
						this.updatethereceiptglpostingdata(itemsObjectData);
						if(itemsObjectData.get("TYPE").toString().equals("E")) {
							errorIdsList.add(itemsObjectData.get("RECEIPT_NUMBER").toString());
						}
					} else {
						JSONArray itemsArraydata = new JSONArray(ITRETURN.toString());
						for(int m=0;m<itemsArraydata.length();m++) {
							JSONObject itemsObjectData = new JSONObject(itemsArraydata.get(m).toString());
							this.updatethereceiptglpostingdata(itemsObjectData);
							if(itemsObjectData.get("TYPE").toString().equals("E")) {
								errorIdsList.add(itemsObjectData.get("RECEIPT_NUMBER").toString());
							}
						}
					}
		}
		
					JSONObject responseMap  = new JSONObject();
		            
		            if(errorIdsList.size() == 0) {
		            	responseMap.put(STATUS_CODE, SUCCESS_CODE);
		            	responseMap.put(STATUS_MESSAGE, SUCCESS);
		            } else {
		            	responseMap.put(STATUS_CODE, ERROR_CODE_500);
		            	responseMap.put("errorCodes", errorIdsList);
		            	responseMap.put(STATUS_MESSAGE, "");
		            }
		            
					return responseMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.storeLogsDataInformation("0", e.getMessage().toString(), "GL POST", userid, "error");
			return validate.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
		}
	}
	
	private void updatethestatustoMarkAsInvalid(String instruemntNumber,String companyCode) {
		Statement stmt;
		PreparedStatement updateQryPrepare;
	    try {
	    	stmt = connection.createStatement();
            String querytoCheckReceipt = "SELECT * FROM AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS WHERE RECEIPT_NUMBER = '"+instruemntNumber+"'";// AND COMPANY_CODE = '"+companyCode+"'
            System.err.println(querytoCheckReceipt);
            ResultSet receiptResultSet = stmt.executeQuery(querytoCheckReceipt);
            if(receiptResultSet.next()) {
                String updateQry = " UPDATE AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS C SET C.DEPO_RECEIPT_STATUS = 'Mark AS Invalid' WHERE C.RECEIPT_NUMBER = '"+instruemntNumber+"' "; //AND C.COMPANY_CODE = '"+companyCode+"'
                System.out.println(updateQry);
                updateQryPrepare = connection.prepareStatement(updateQry);
                updateQryPrepare.execute();
                updateQryPrepare.close();
            } else {
            	String querytoCheckInstrument = "SELECT * FROM AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS WHERE INSTRUMENT_ID = '"+instruemntNumber+"'";// AND COMPANY_CODE = '"+companyCode+"'
            	ResultSet instrumentResultSet = stmt.executeQuery(querytoCheckInstrument);
            	if(instrumentResultSet.next()) {
            		// AND I.COMPANY_CODE = '"+companyCode+"'
            		String updateQry = " UPDATE AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS I SET I.INSTRUMNET_STATUS = 'Mark AS Invalid' WHERE I.INSTRUMENT_ID = '"+instruemntNumber+"'";
            		System.out.println(updateQry);
                    updateQryPrepare = connection.prepareStatement(updateQry);
                    updateQryPrepare.execute();
                    updateQryPrepare.close();
                    System.err.println(instruemntNumber);
                    this.voidupdateInstrumentReceiptStatus(instruemntNumber,companyCode);
            	}
            }
            
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage().toString());
		}    
	}
	
	private void voidupdateInstrumentReceiptStatus(String receiptNumbertoCheck, String companycode) {
		Statement stmt;
		PreparedStatement updateQryPrepare;
		try {
//			Timestamp createdAt = new Timestamp(System.currentTimeMillis());
			Timestamp createdAt = this.getCountryTimestamp(companycode);
			stmt = connection.createStatement();
			
			String readytopostnotInstruments = "SELECT COUNT('*') RCOUNT FROM AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS WHERE (INSTRUMNET_STATUS IS NULL OR INSTRUMNET_STATUS = '') AND RECEIPT_ID = '"+receiptNumbertoCheck+"'";
			System.err.println(readytopostnotInstruments);
			ResultSet readytopostnotInstrumentsResultSet = stmt.executeQuery(readytopostnotInstruments);
			readytopostnotInstrumentsResultSet.next();
			int rcounValue = Integer.parseInt(readytopostnotInstrumentsResultSet.getString("RCOUNT").toString());
			if(rcounValue == 0) {
				String updateQryReceipt = " UPDATE AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS C SET C.DEPO_RECEIPT_STATUS = 'Post to SAP',C.UPDATED_AT = '"+createdAt.toString()+"' WHERE C.RECEIPT_NUMBER = '"+receiptNumbertoCheck+"' ";
				System.err.println("asif"+updateQryReceipt);
                updateQryPrepare = connection.prepareStatement(updateQryReceipt);
                updateQryPrepare.execute();
                updateQryPrepare.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void updatethereceiptglpostingdata(JSONObject itemsObjectData) {
		Statement stmt;
		PreparedStatement updateQryPrepare;
	    try {
	    	stmt = connection.createStatement();
	    	 String companycode = itemsObjectData.get("COMPANY_CODE").toString();
//	    	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
	    	 Timestamp createdAt = this.getCountryTimestamp(companycode);
	    	String receiptNumbertoCheck = itemsObjectData.get("RECEIPT_NUMBER").toString();
	    	String INSTRUMENTNUMBER = itemsObjectData.get("INSTRUMENT_NUMBER").toString();
	    	
	    	String glpostQueryInfo = "SELECT * FROM AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS WHERE RECEIPT_NUMBER = '"+receiptNumbertoCheck+"' LIMIT 1";
	    	System.err.println(glpostQueryInfo);
	    	ResultSet receiptGlResultSet = stmt.executeQuery(glpostQueryInfo);
	    	receiptGlResultSet.next();
	    	String modeOfPayment = receiptGlResultSet.getString("MODE_OF_PAYMENT").toString();
	    	if(modeOfPayment.equals("Cash")) { //if(INSTRUMENTNUMBER.isEmpty()) {
	    		String querytoCheckReceipt = "SELECT * FROM AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS WHERE RECEIPT_NUMBER = '"+receiptNumbertoCheck+"'";
	            ResultSet receiptResultSet = stmt.executeQuery(querytoCheckReceipt);
	            if(receiptResultSet.next()) {
	                String updateQry = " UPDATE AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS C "
	                        + "SET ";
	                		if(itemsObjectData.get("TYPE").toString().equals("S")) {
	                			updateQry += " C.DEPO_RECEIPT_STATUS = 'Post to SAP', ";
	                		} else {
	                			updateQry += "C.DEPO_RECEIPT_STATUS = 'Error from SAP', ";
	                		}
	                		updateQry += "C.UPDATED_AT = '"+createdAt.toString()+"', C.SAP_DOC_NUM = '"+itemsObjectData.get("SAP_DOC_NUMBER").toString()+"', C.SAP_MESSAGE_LOG = '"+itemsObjectData.get("MESSAGE").toString()+"',SAP_API_RESPONSE = '"+itemsObjectData.get("TYPE").toString()+"' "
	                        + "WHERE C.RECEIPT_NUMBER = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"'";
	                		if(itemsObjectData.get("TYPE").toString().equals("S")) {
//	                			updateQry += " AND C.COMPANY_CODE = '"+itemsObjectData.get("COMPANY_CODE").toString()+"' ";
	                		}
	                		System.out.println("asif"+updateQry);
	                updateQryPrepare = connection.prepareStatement(updateQry);
	                updateQryPrepare.execute();
	                updateQryPrepare.close();
	            }
	    	} else {
	    		
	    		String querytoCheckInstrument = "SELECT * FROM AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS WHERE RECEIPT_ID = '"+receiptNumbertoCheck+"' AND INSTRUMENT_NUMBER = '"+INSTRUMENTNUMBER+"'";
            	ResultSet instrumentResultSet = stmt.executeQuery(querytoCheckInstrument);
            	if(instrumentResultSet.next()) {
            		String updateQry = " UPDATE AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS I "
                            + "SET ";
                    		if(itemsObjectData.get("TYPE").toString().equals("S")) {
                    			updateQry += " I.INSTRUMNET_STATUS = 'Post to SAP', ";
                    		} else {
                    			updateQry += "I.INSTRUMNET_STATUS = 'Error from SAP', ";
                    		}
                    		updateQry += " I.SAP_DOC_NUM = '"+itemsObjectData.get("SAP_DOC_NUMBER").toString()+"', I.SAP_MESSAGE_LOG = '"+itemsObjectData.get("MESSAGE").toString()+"',I.SAP_API_RESPONSE = '"+itemsObjectData.get("TYPE").toString()+"' "
                            + "WHERE I.RECEIPT_ID = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"' AND I.INSTRUMENT_NUMBER = '"+INSTRUMENTNUMBER+"' ";
                    		if(itemsObjectData.get("TYPE").toString().equals("S")) {
//                    			updateQry += " AND I.COMPANY_CODE = '"+itemsObjectData.get("COMPANY_CODE").toString()+"' ";
                    		}
                    	System.out.println(updateQry);
                    updateQryPrepare = connection.prepareStatement(updateQry);
                    updateQryPrepare.execute();
                    updateQryPrepare.close();
                    /*
                     * Update receipt updatedat column
                     */
                    String updateQryReceipt = " UPDATE AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS C SET C.UPDATED_AT = '"+createdAt.toString()+"' WHERE C.RECEIPT_NUMBER = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"'";
                    updateQryPrepare = connection.prepareStatement(updateQryReceipt);
                    updateQryPrepare.execute();
                    updateQryPrepare.close();
                    /*
                     * 
                     */
                   
                    this.voidupdateInstrumentReceiptStatus(receiptNumbertoCheck,companycode);
                    
	    	}
            	
	    	}
	    	
	    	String updateQryS = " UPDATE AP_GLOBAL.DL_GL_POSTING_INSTRUMENTS G "
	                + "SET G.SAP_DOCUMENT_NUMBER = '"+itemsObjectData.get("SAP_DOC_NUMBER").toString()+"', G.SAP_STATUS_RESPONSE = '"+itemsObjectData.get("MESSAGE").toString()+"',G.SAP_STATUS_CODE = '"+itemsObjectData.get("TYPE").toString()+"',FISCAL_YEAR = '"+itemsObjectData.get("FISCAL_YEAR").toString()+"' WHERE G.REMARKS = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"' AND G.INSTRUMENT_NUMBER = '"+itemsObjectData.get("INSTRUMENT_NUMBER").toString()+"'";
	        
	        if(itemsObjectData.get("TYPE").toString().equals("S")) {
	        	updateQryS += " AND G.COMPANY_CODE = '"+itemsObjectData.get("COMPANY_CODE").toString()+"' ";
	        }
	        System.out.println(updateQryS);
	        updateQryPrepare = connection.prepareStatement(updateQryS);
	        updateQryPrepare.execute();
	        updateQryPrepare.close();
	    	
//            String querytoCheckReceipt = "SELECT * FROM AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS WHERE RECEIPT_NUMBER = '"+receiptNumbertoCheck+"'";
//            ResultSet receiptResultSet = stmt.executeQuery(querytoCheckReceipt);
//            if(receiptResultSet.next()) {
//                String updateQry = " UPDATE AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS C "
//                        + "SET ";
//                		if(itemsObjectData.get("TYPE").toString().equals("S")) {
//                			updateQry += "C.DEPO_RECEIPT_STATUS = 'Post to SAP', ";
//                		} else {
//                			updateQry += "C.DEPO_RECEIPT_STATUS = 'Mark AS Invalid', ";
//                		}
//                		updateQry += "C.UPDATED_AT = '"+createdAt.toString()+"', C.SAP_DOC_NUM = '"+itemsObjectData.get("SAP_DOC_NUMBER").toString()+"', C.SAP_MESSAGE_LOG = '"+itemsObjectData.get("MESSAGE").toString()+"',SAP_API_RESPONSE = '"+itemsObjectData.get("TYPE").toString()+"' "
//                        + "WHERE C.RECEIPT_NUMBER = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"'";
//                		if(itemsObjectData.get("TYPE").toString().equals("S")) {
//                			updateQry += " AND C.COMPANY_CODE = '"+itemsObjectData.get("COMPANY_CODE").toString()+"' ";
//                		}
//                		System.out.println(updateQry);
//                updateQryPrepare = connection.prepareStatement(updateQry);
//                updateQryPrepare.execute();
//                updateQryPrepare.close();
//            } else {
//            	// AND COMPANY_CODE = '"+itemsObjectData.get("COMPANY_CODE").toString()+"'
//            	String querytoCheckInstrument = "SELECT * FROM AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS WHERE INSTRUMENT_ID = '"+receiptNumbertoCheck+"'";
//            	
//            	ResultSet instrumentResultSet = stmt.executeQuery(querytoCheckInstrument);
//            	if(instrumentResultSet.next()) {
//            		String updateQry = " UPDATE AP_GLOBAL.DL_RECEIPT_INSTRUMENT_DETAILS I "
//                            + "SET ";
//                    		if(itemsObjectData.get("TYPE").toString().equals("S")) {
//                    			updateQry += "I.INSTRUMNET_STATUS = 'Post to SAP', ";
//                    		} else {
//                    			updateQry += "I.INSTRUMNET_STATUS = 'Mark AS Invalid', ";
//                    		}
//                    		updateQry += "I.SAP_DOC_NUM = '"+itemsObjectData.get("SAP_DOC_NUMBER").toString()+"', I.SAP_MESSAGE_LOG = '"+itemsObjectData.get("MESSAGE").toString()+"',I.SAP_API_RESPONSE = '"+itemsObjectData.get("TYPE").toString()+"' "
//                            + "WHERE I.INSTRUMENT_ID = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"'";
//                    		if(itemsObjectData.get("TYPE").toString().equals("S")) {
//                    			updateQry += " AND I.COMPANY_CODE = '"+itemsObjectData.get("COMPANY_CODE").toString()+"' ";
//                    		}
//                    	
//                    updateQryPrepare = connection.prepareStatement(updateQry);
//                    updateQryPrepare.execute();
//                    updateQryPrepare.close();
//                    
//                    String updateQryReceipt = " UPDATE AP_GLOBAL.DL_COLLECTION_RECEIPT_DETAILS C SET C.UPDATED_AT = '"+createdAt.toString()+"' WHERE C.RECEIPT_NUMBER = '"+itemsObjectData.get("RECEIPT_NUMBER").toString()+"'";
//                    updateQryPrepare = connection.prepareStatement(updateQryReceipt);
//                    updateQryPrepare.execute();
//                    updateQryPrepare.close();
//                    
//                    this.voidupdateInstrumentReceiptStatus(receiptNumbertoCheck);
//            	}
//            }
            
		} catch (Exception e) {
			// TODO: handle exception
			
		}    
	}
	
	private void storeLogsDataInformation(String excutionTime, String messageType, String category, String userId, String errorType) {
        try {
        	Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            String insertCommand = "INSERT INTO " + SCHEMA_TABLE + "." + LOGS_TABLE + " "
                    + "("
                    + "MESSAGE,"
                    + "EXEXUTION_TIME,"
                    + "CREATED_AT,"
                    + "CATEGORY,"
                    + "USERID,"
                    + "LOG_TYPE"
                    + ") VALUES (?,?,?,?,?,?) ";
            PreparedStatement pstUserlogin = connection.prepareStatement(insertCommand);
            pstUserlogin.setString(1, messageType.toString());
            pstUserlogin.setString(2, excutionTime.toString());
            pstUserlogin.setString(3, createdAt.toString());
            pstUserlogin.setString(4, category.toString());
            pstUserlogin.setString(5, userId.toString());
            pstUserlogin.setString(6, errorType.toString());
            pstUserlogin.executeUpdate();
            pstUserlogin.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

	public Object getFilterDropdown() {
		JSONObject responseMap = new JSONObject();
		Statement stmt ;
		try {
			stmt = connection.createStatement();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseMap;
	}
	
	private Timestamp getCountryTimestamp(String cOMPANYCODE) {
		Timestamp createAt =null;
		try {
			String getTimestamp="SELECT dcm.TIMEZONE FROM "+ SCHEMA_TABLE +" . "+ COUNTRIES_MASTER +" dcm WHERE dcm.COMPANY_CODE = ?";
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

