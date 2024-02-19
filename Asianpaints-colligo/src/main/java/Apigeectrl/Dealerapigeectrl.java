package Apigeectrl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Dealerrepository;
import Repository.RepositoryAccountVerification;
import Utilres.ValidationCtrl;

public class Dealerapigeectrl {
	
	private Dealerrepository DealerSer;
	private ValidationCtrl validate;
	private RepositoryAccountVerification accountverification;
	
	public Dealerapigeectrl() {
		// TODO Auto-generated constructor stub
		DealerSer = new Dealerrepository();
		validate = new ValidationCtrl();
		accountverification = new RepositoryAccountVerification();
	}
	
	public ExecutionResult getDealerList(MessageContext messageContext, ExecutionContext executionContext) {
	// TODO Auto-generated method stub
	String countryId = messageContext.getVariable("request.queryparam.countryId").toString();
	String filter = messageContext.getVariable("request.queryparam.filter").toString();
	String userid = messageContext.getVariable("request.queryparam.userid").toString();
	String isCountry = messageContext.getVariable("request.queryparam.isCountry").toString();
	messageContext.setVariable(countryId, filter);
	Object responseMap = DealerSer.getDealerInformation(userid,countryId,filter,isCountry);
	messageContext.getMessage().setContent(responseMap.toString());
	return ExecutionResult.SUCCESS;		
	}

	public ExecutionResult accountValidate(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validateaccountcheckAPIdata(data);
		if(validatecheck.equals(true)) {
	        Object accountInformation = accountverification.getCheckAccountbyDealerId(data);
	        messageContext.getMessage().setContent(accountInformation.toString());
		} else {
			messageContext.getMessage().setContent(validatecheck.toString());
		}
		return ExecutionResult.SUCCESS;	
	}
	
}
