package Apigeectrl;

import static Constants.AsianConstants.ERROR_CODE_500;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Otprepository;

public class Otpapigeectrl {
	
	private Otprepository otpservices;
	private Apigeejsonresponse Josnutilres;
	
	public Otpapigeectrl() {
		// TODO Auto-generated constructor stub
		otpservices = new Otprepository();
		Josnutilres = new Apigeejsonresponse();
	}
	
	public ExecutionResult sendCountryOtp(MessageContext messageContext, ExecutionContext executionContext) {
	// TODO Auto-generated method stub
	JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
	String countrycode = data.get("countrycode").getAsString();
	String mobileNumber = data.get("mobileNumber").getAsString();
	String userId = data.get("userId").getAsString();
	String amount = data.get("amount").getAsString();
	String dealerid = data.get("dealerid").getAsString();
	Object OtpResponse = otpservices.SendOtpMessage(countrycode,mobileNumber,userId,amount,dealerid);
	if(OtpResponse == null) {
		Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Something went wrong!, 1 please try again.");
	} else if(countrycode == null) {
	    Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Invalid Country");
	} else if(mobileNumber == null) {
        Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Invalid Mobile Number");
    } else if(userId == null) {
        Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Invalid User");
    } else if(amount == null) {
        Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Invalid Amount");
    } else {
		messageContext.getMessage().setContent(OtpResponse.toString());
	}
	return ExecutionResult.SUCCESS;
}
	
}
