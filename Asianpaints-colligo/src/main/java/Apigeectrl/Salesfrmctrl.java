package Apigeectrl;

import static Constants.AsianConstants.ApigeeLogoPath;
import static Constants.AsianConstants.Apigeeimagepath;
import static Constants.AsianConstants.ERROR_CODE_500;

import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Salesrepository;
import Utilres.ValidationCtrl;

public class Salesfrmctrl {
	
	private Salesrepository saleserv;
	private ValidationCtrl validate;
	private Apigeejsonresponse Josnutilres;
	
	public Salesfrmctrl() {
		// TODO Auto-generated constructor stub
		saleserv = new Salesrepository();
		Josnutilres = new Apigeejsonresponse();
		validate = new ValidationCtrl();
	}
	
	public ExecutionResult getFormSave(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
	    try {
	        JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
	        Object validatecheck = validate.validateCollectionApidata(data);
	        if(validatecheck.equals(true)) {
	            JSONObject salesObject = new JSONObject();
	            try {
	                String imagepath = Apigeeimagepath;
	                String fullPathImage = URLDecoder.decode(imagepath, "UTF-8");
	                salesObject.put("ocr", data.get("ocr").getAsString());
	                salesObject.put("dealerId", data.get("dealerId").getAsString());
	                salesObject.put("finalRecepitAmount", data.get("finalRecepitAmount").getAsString());
	                salesObject.put("receivedAmount", data.get("receivedAmount").getAsString());
	                salesObject.put("dealerMobile", data.get("dealermobile").getAsString());
	                salesObject.put("paymentype", data.get("paymentype").getAsString());
	                salesObject.put("userid", data.get("userid").getAsString());
	                salesObject.put("otptoken", data.get("otptoken").getAsString());
	                salesObject.put("dealerRepresentative", data.get("dealerRepresentative").getAsString());
	                salesObject.put("dealerRemark", data.get("dealerRemark").getAsString());
	                salesObject.put("saveflag", data.get("saveflag").getAsInt());
	                salesObject.put("receiptid", data.get("receiptid").getAsString());
	                salesObject.put("dateofreceving", data.get("dateofreceving").getAsString());
	                salesObject.put("countrycode", data.has("countrycode") ? data.get("countrycode").getAsString() : "");
	                salesObject.put("receiptremarks", data.has("receiptremarks") ? data.get("receiptremarks").getAsString() : "");
	                salesObject.put("statuscollection", data.has("statuscollection") ? data.get("statuscollection").getAsString() : "");
	                salesObject.put("lang", data.has("lang") ? data.get("lang").getAsString() : "en");
	                Object SalesResponse = saleserv.cashreceiptflowmanage(salesObject, fullPathImage, ApigeeLogoPath);
	                messageContext.getMessage().setContent(SalesResponse.toString()); 
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	                Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Something went wrong!, Please try again.");
	            }
	        } else {
	            messageContext.getMessage().setContent(validatecheck.toString()); 
	        }
        } catch (Exception e) {
            // TODO: handle exception
            messageContext.setVariable("JAVA_ERROR", e.getMessage());
            Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Something went wrong!, Please try again.");
        }
		
		return ExecutionResult.SUCCESS;
	}

}
