package Apigeectrl;

import static Constants.AsianConstants.ERROR_CODE_500;

import org.json.JSONException;
import org.json.JSONObject;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.VerifyingRepository;

public class Verifyapigeectrl {
	
	private VerifyingRepository verifyServ = new VerifyingRepository();
	private Apigeejsonresponse Josnutilres = new Apigeejsonresponse();
	
	public ExecutionResult getCheckOtpMessage(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		JSONObject verifyOtpObject = new JSONObject();
		try {
			verifyOtpObject.put("otp",  data.get("otp").getAsString());
			verifyOtpObject.put("otptoken",  data.get("otptoken").getAsString());
			verifyOtpObject.put("mobileNumber",  data.get("mobileNumber").getAsString());
			verifyOtpObject.put("userId",  data.get("userId").getAsString());
			Object OtpResponse = verifyServ.getCheckOtpMessage(verifyOtpObject);
			
			if(data.get("otp").getAsString() == null) {
				Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Please Enter OTP");
			} else if(data.get("otp").getAsString() != null && OtpResponse == null) {
				Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Something went wrong!, please try again.");
			} else {
				messageContext.getMessage().setContent(OtpResponse.toString());
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ExecutionResult.SUCCESS;
	}

}
