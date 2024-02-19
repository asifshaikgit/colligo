package Apigeectrl;

import static Constants.AsianConstants.ERROR_CODE_500;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;

import Repository.SettingsRepo;

public class Settingsapigeectrl {
	
	private SettingsRepo settdata;
	private Apigeejsonresponse Josnutilres;
	 
	public Settingsapigeectrl() {
		// TODO Auto-generated constructor stub
		settdata = new SettingsRepo();
		Josnutilres = new Apigeejsonresponse();
	}
	 
	public ExecutionResult getSettingOptions(MessageContext messageContext, ExecutionContext executionContext, String queryType) {
		Object settingsInfo = settdata.getloadSettingsdata(queryType);
		if(settingsInfo == null) {
			Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Something went wrong!, please try again.");
		} else {
			messageContext.getMessage().setContent(settingsInfo.toString());
		}
		return ExecutionResult.SUCCESS;
	}

	public ExecutionResult getCountrywisebanks(MessageContext messageContext, ExecutionContext executionContext, String countryCode) {
		// TODO Auto-generated method stub
		Object settingsInfo = settdata.loadBanknameslist(countryCode);
		if(settingsInfo == null) {
			Josnutilres.errorResponse(messageContext, ERROR_CODE_500, "Something went wrong!, please try again.");
		} else {
			messageContext.getMessage().setContent(settingsInfo.toString());
		}
		return ExecutionResult.SUCCESS;
		
	}

}
