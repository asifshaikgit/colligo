package Repository;

import static Constants.AsianConstants.ERROR_CODE_500;
import static Constants.AsianConstants.SCANNER_TABLE;
import static Constants.AsianConstants.SCHEMA_TABLE;
import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;
import static Constants.AsianConstants.SUCCESS;
import static Constants.AsianConstants.SUCCESS_CODE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.JsonObject;

import Databaseconnection.Database;
import Services.scannerservice;
import Utilres.ValidationCtrl;

public class Scannerepo implements scannerservice {
	
	private Connection connection;
	private ValidationCtrl validationlog;
	public Scannerepo() {
		// TODO Auto-generated constructor stub
		connection = new Database().Connection();
		validationlog = new ValidationCtrl();
	}
	
	public Object savePdfData(JsonObject data) {
		Timestamp createdAt = new Timestamp(System.currentTimeMillis());
		try {
		    	connection.setAutoCommit(false);
		    	String billingDocumentNumber = data.get("billingDocumentNumber").getAsString();
		    	String billingDate = data.get("billingDate").getAsString();
		    	String odnNumber = data.get("odnNumber").getAsString();
		    	String countryCode = data.get("countryCode").getAsString();
		    	String pdfAzureLink = data.get("pdfAzureLink").getAsString();
		    	String createdBy = data.get("userid").getAsString();
		    	
	            String insertQry = "INSERT INTO " + SCHEMA_TABLE + "." + SCANNER_TABLE + " "
	            		+ "("
	            		+ "BILLING_DOCUMENT_NUMBER,"
	            		+ "BILLING_DATE,"
	            		+ "ODN_NUMBER,"
	            		+ "COUNTRY_CODE,"
	            		+ "PDF_AZURE_LINK,"
	            		+ "CREATED_AT,"
	            		+ "CREATED_BY,"
	            		+ "UPDATED_AT,"
	            		+ "UPDATED_BY"
	            		+ ") VALUES(?,?,?,?,?,?,?,?,?)";
	            
	            PreparedStatement storeQuery = connection.prepareStatement(insertQry);
	            storeQuery.setString(1, billingDocumentNumber);
	            storeQuery.setString(2, billingDate);
	            storeQuery.setString(3, odnNumber);
	            storeQuery.setString(4, countryCode);
	            storeQuery.setString(5, pdfAzureLink);
	            storeQuery.setString(6, createdAt.toString());
	            storeQuery.setString(7, createdBy);
	            storeQuery.setString(8, createdAt.toString());
	            storeQuery.setString(9, createdBy);
	            storeQuery.executeUpdate();
	            storeQuery.close();
	            connection.commit();
	            JSONObject responseMap = new JSONObject();
                responseMap.put(STATUS_MESSAGE, SUCCESS);
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
			return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
		}
	}

	@Override
	public Object getPdfData(String userid,String filter, String search, String fromdate,String todate) {
		// TODO Auto-generated method stub
		Statement stmt;
		JSONObject responseMap = new JSONObject();
		try {
			Boolean isTrash = false;
			stmt = connection.createStatement();
			String querySelect = " SELECT S.*"
					+ " FROM "+SCHEMA_TABLE+"."+SCANNER_TABLE+" S WHERE CREATED_BY = '"+userid+"' ";
			
			if(!filter.isEmpty()) {
				if(filter.equals("billingDocumentNumber")) {
					querySelect += " AND S.BILLING_DOCUMENT_NUMBER LIKE '%"+search+"%' ";
				}
				if(filter.equals("odnNumber")) {
					querySelect += " AND S.ODN_NUMBER LIKE '%"+search+"%' ";
				}
				if(filter.equals("billingDate")) {
					querySelect += " AND S.BILLING_DATE >= '%"+fromdate+"%' AND S.BILLING_DATE <= '%"+todate+"%' ";
				}
				if(filter.equals("billingDate")) {
					querySelect += " AND SUBSTRING(S.CREATED_AT,0,10) >= '%"+fromdate+"%' AND SUBSTRING(S.CREATED_AT,0,10) <= '%"+todate+"%' ";
				}
			}
			querySelect += " AND IS_TRASH = "+isTrash+"";
			
			ResultSet storePdfInfo = stmt.executeQuery(querySelect);
			
			ArrayList<Object> StorepdfArray = new ArrayList<>();
			while(storePdfInfo.next()) {
				JSONObject scannerObj = new JSONObject();
				scannerObj.put("billingDocumentNumber", storePdfInfo.getString("BILLING_DOCUMENT_NUMBER"));
				scannerObj.put("billingDate", storePdfInfo.getString("BILLING_DATE"));
				scannerObj.put("odnNumber", storePdfInfo.getString("ODN_NUMBER"));
				scannerObj.put("countryCode", storePdfInfo.getString("COUNTRY_CODE"));
				scannerObj.put("pdfAzureLink", storePdfInfo.getString("PDF_AZURE_LINK"));
				scannerObj.put("createdBy", storePdfInfo.getString("CREATED_BY"));
				StorepdfArray.add(scannerObj);
			}
			stmt.close();
			responseMap.put(STATUS_CODE, SUCCESS_CODE);
			responseMap.put(STATUS_MESSAGE, SUCCESS);
			responseMap.put("data",StorepdfArray);
			return responseMap;
	}catch (Exception e) {
		// TODO: handle exception
		return validationlog.errorValidationResponse(ERROR_CODE_500, "Something went wrong!, please try again..");
	}
	
	}
	
}
