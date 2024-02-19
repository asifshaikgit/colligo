package Apigeectrl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.ChangePwdRepo;
import Repository.Forgotrepo;
import Repository.Logoutrepository;
import Utilres.ValidationCtrl;

public class Forgotpwdapigeectrl {
	
    private ValidationCtrl validate;
    
	public Forgotpwdapigeectrl() {
		// TODO Auto-generated constructor stub
		validate = new ValidationCtrl();
	}

	public ExecutionResult checkUserId(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		Forgotrepo forgotcall = new Forgotrepo();
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validateforgotApidata(data);
		if(validatecheck.equals(true)) {
			Object forgotcallObj = forgotcall.checkUserId(data);
			messageContext.getMessage().setContent(forgotcallObj.toString());
		} else {
			messageContext.getMessage().setContent(validatecheck.toString());
		}
		return ExecutionResult.SUCCESS;
	}

	public ExecutionResult updateThePwd(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		ChangePwdRepo pwdreq = new ChangePwdRepo();
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validatechnagepwdApidata(data);
		if(validatecheck.equals(true)) {
			Object updatePwdObj = pwdreq.updateThePwd(data);
			messageContext.getMessage().setContent(updatePwdObj.toString());
		} else {
			messageContext.getMessage().setContent(validatecheck.toString());
		}
		return ExecutionResult.SUCCESS;
	}

	public ExecutionResult verifyEmailOtp(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		Forgotrepo forgotcall = new Forgotrepo();
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validateforgotverifyotpApidata(data);
		if(validatecheck.equals(true)) {
			Object verifycallObj = forgotcall.verifyEmailOtp(data);
			messageContext.getMessage().setContent(verifycallObj.toString());
		} else {
			messageContext.getMessage().setContent(validatecheck.toString());
		}
		return ExecutionResult.SUCCESS;
	}

	public ExecutionResult userLogout(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		Logoutrepository logoutCall = new Logoutrepository();
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validatelogoutApidata(data);
		if(validatecheck.equals(true)) {
	        Object LogoutString = logoutCall.Userlogout(data);
	        messageContext.getMessage().setContent(LogoutString.toString());
		} else {
			messageContext.getMessage().setContent(validatecheck.toString());
		}
		return ExecutionResult.SUCCESS;
	}

}
