package Apigeectrl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Scannerepo;
import Utilres.ValidationCtrl;

public class Scannerapigeectrl {
	
	private ValidationCtrl validate;
	private Scannerepo scannerep;
	public Scannerapigeectrl() {
		// TODO Auto-generated constructor stub
		validate = new ValidationCtrl();
		scannerep = new Scannerepo();
	}

	public ExecutionResult storeScannerData(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
		Object validatecheck = validate.validateStorePdfApidata(data);
    	if(validatecheck.equals(true)) {
            Object StorePdfResponse = scannerep.savePdfData(data);
            messageContext.getMessage().setContent(StorePdfResponse.toString());
    	} else {
    		messageContext.getMessage().setContent(validatecheck.toString());
    	}
    	
		return ExecutionResult.SUCCESS;
	}

	public ExecutionResult getScannerData(MessageContext messageContext, ExecutionContext executionContext) {
		// TODO Auto-generated method stub
		String userid = messageContext.getVariable("request.queryparam.userid").toString();
        String filter = messageContext.getVariable("request.queryparam.filter").toString();
        String search = messageContext.getVariable("request.queryparam.search").toString();
        String fromdate = messageContext.getVariable("request.queryparam.fromdate").toString();
        String todate = messageContext.getVariable("request.queryparam.todate").toString();
        Object responseMap = scannerep.getPdfData(userid, filter, search, fromdate,todate);
        messageContext.getMessage().setContent(responseMap.toString());
		return ExecutionResult.SUCCESS;
	}
}
