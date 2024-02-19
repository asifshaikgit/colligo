package Apigeectrl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.LoginRepository;
import Utilres.ValidationCtrl;

public class Loginapigeectrl {
	
	private LoginRepository loginServ;
	private ValidationCtrl validate;
    public Loginapigeectrl() {
		// TODO Auto-generated constructor stub
		loginServ = new LoginRepository();
		validate = new ValidationCtrl();
	}
    
	public ExecutionResult checkLoginUpdate(MessageContext messageContext, ExecutionContext executionContext) {
	// TODO Auto-generated method stub
	System.out.println("LOGIN FIRST CAll");
	JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
	System.out.println("LOGIN AFTER GSON CAll");
	Object validatecheck = validate.validateloginApidata(data);
	System.out.println("APIGEE START LOGIN CAll");
	if(validatecheck.equals(true)) {
		System.out.println("APIGEE INSIDE LOGIN CAll");
	    String username = data.get("username").getAsString();
	    String password = data.get("password").getAsString();
	    String devicetoken = data.get("devicetoken").getAsString();
	    String userlevel = "1";
	    Object LoginString = loginServ.loginCheckrepository(username, password, userlevel, devicetoken);
	    messageContext.getMessage().setContent(LoginString.toString());
	} else {
		System.out.println("APIGEE OUTSIDE LOGIN CAll");
	    messageContext.getMessage().setContent(validatecheck.toString());
	}
	return ExecutionResult.SUCCESS;
}
	
}
