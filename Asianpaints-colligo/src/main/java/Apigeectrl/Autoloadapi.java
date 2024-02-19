package Apigeectrl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.UserAutoloadcheck;
import Utilres.ValidationCtrl;

public class Autoloadapi {
	
private ValidationCtrl validate;
private UserAutoloadcheck autoload;

	public Autoloadapi() {
		// TODO Auto-generated constructor stub
		validate = new ValidationCtrl();
		autoload = new UserAutoloadcheck();
	}
	
	public ExecutionResult getuserApploadinfo(MessageContext messageContext, ExecutionContext executionContext) {
        // TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validateautoloadApidata(data);
		if(validatecheck.equals(true)) {
		Object LoginString = autoload.CheckuserinfoAutoload(data);
		messageContext.getMessage().setContent(LoginString.toString());
		} else {
			messageContext.getMessage().setContent(validatecheck.toString());
		}
		return ExecutionResult.SUCCESS;
    }

}
