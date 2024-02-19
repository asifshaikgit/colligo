package com.dl;

import static Constants.AsianConstants.GET;
import static Constants.AsianConstants.POST;

import java.util.concurrent.Callable;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;

import Apigeectrl.Autoloadapi;
import Apigeectrl.Dealerapigeectrl;
import Apigeectrl.Forgotpwdapigeectrl;
import Apigeectrl.Loginapigeectrl;
import Apigeectrl.Otpapigeectrl;
import Apigeectrl.Receiptapigeectrl;
import Apigeectrl.Salesdashboardlist;
import Apigeectrl.Salesfrmctrl;
import Apigeectrl.Scannerapigeectrl;
import Apigeectrl.Verifyapigeectrl;
import Apigeectrl.Settingsapigeectrl;
import Apigeectrl.Ocrapigeectrl;

public class Router implements Callable<ExecutionResult> {
	
	MessageContext messageContext;
	ExecutionContext executionContext;
	
	public Router(MessageContext mc, ExecutionContext ec) {
		// TODO Auto-generated constructor stub
		this.messageContext = mc;
        this.executionContext = ec;
        System.out.println("ROUTER CONSTRUCTION");
	}

	@Override
	public ExecutionResult call() throws Exception {
		// TODO Auto-generated method stub
		String Callingmethod = messageContext.getVariable("request.queryparam.api");
		String methodCall = messageContext.getVariable("request.verb");
		String queryType = messageContext.getVariable("request.queryparam.type");
		System.out.println("ROUTER CALL FIRST CALL");
		if(methodCall.equals(POST)) {
			switch (Callingmethod) {
			case "login":
				System.out.println("ROUTER IN LOGIN SWITCH");
			    Loginapigeectrl LoginSer = new Loginapigeectrl();
			    System.out.println("ROUTER IN LOGIN AFTER APIGEE");
				LoginSer.checkLoginUpdate(messageContext,executionContext);
				System.out.println("ROUTER IN LOGIN END APIGEE");
				break;
			case "send-otp":
			    Otpapigeectrl otpSerCall = new Otpapigeectrl();
				otpSerCall.sendCountryOtp(messageContext,executionContext);
				break;
			case "collection-save":
			    Salesfrmctrl salesfrmCall = new Salesfrmctrl();
				salesfrmCall.getFormSave(messageContext,executionContext);
				break;
			case "verify-otp":
			    Verifyapigeectrl verifyOtpCall = new Verifyapigeectrl();
				verifyOtpCall.getCheckOtpMessage(messageContext,executionContext);
				break;
			case "get-ocr-data":
			    Ocrapigeectrl ocrctrlCall = new Ocrapigeectrl();
			    ocrctrlCall.getUploadocrUrlDataDealer(messageContext,executionContext);
                break;
			case "save-receipt-ocr-data":
			    Ocrapigeectrl ocrctrlsaveCall = new Ocrapigeectrl();
			    ocrctrlsaveCall.getSaveReceiptInformation(messageContext,executionContext);
			    break;
			case "get-load-glposting-data":
				Salesdashboardlist salesglposting = new Salesdashboardlist();
				salesglposting.getloadGlpostinginformation(messageContext,executionContext);
			    break;
			case "post-data-to-sap":
				Salesdashboardlist datatoSap = new Salesdashboardlist();
				datatoSap.postdatatoGlSap(messageContext,executionContext);
			    break;
			case "get-capture-image-azure-link":
			    Ocrapigeectrl ocrctrlgetCall = new Ocrapigeectrl();
			    ocrctrlgetCall.getCaptureAzurelink(messageContext,executionContext);
                break;
			case "get-sales-dashboard-list":
			    Salesdashboardlist saleslist = new Salesdashboardlist();
			    saleslist.getSalesDashboardList(messageContext,executionContext);
                break;
			case "get-depo-report-dashboard-list":
                Salesdashboardlist depolist = new Salesdashboardlist();
                depolist.getDepoReportDashboardList(messageContext,executionContext);
                break;
			case "receipt-approve-reject":
                Salesdashboardlist receiptStatus = new Salesdashboardlist();
                receiptStatus.getDepoApproveReject(messageContext,executionContext);
                break;
			case "dealer-account-verification":
				Dealerapigeectrl dealerAccount = new Dealerapigeectrl();
				dealerAccount.accountValidate(messageContext,executionContext);
                break;
			case "forgot-email-otp":
				Forgotpwdapigeectrl forgotemailotp = new Forgotpwdapigeectrl();
				forgotemailotp.checkUserId(messageContext,executionContext);
                break;
			case "forgot-otp-verification":
				Forgotpwdapigeectrl emailotpverification = new Forgotpwdapigeectrl();
				emailotpverification.verifyEmailOtp(messageContext,executionContext);
                break;
			case "forgot-pwd-change":
				Forgotpwdapigeectrl pwdchange = new Forgotpwdapigeectrl();
				pwdchange.updateThePwd(messageContext,executionContext);
                break;
			case "autoload":
				Autoloadapi autoload = new Autoloadapi();
				autoload.getuserApploadinfo(messageContext,executionContext);
                break;
             case "logout":
				Forgotpwdapigeectrl logout = new Forgotpwdapigeectrl();
				logout.userLogout(messageContext,executionContext);
                break;
                
             case "scanner-store-data":
            	 Scannerapigeectrl storeScanner = new Scannerapigeectrl();
            	 storeScanner.storeScannerData(messageContext,executionContext);
            	 break;
            	 
			}
		}
		
		if(methodCall.equals(GET)) {
			switch (Callingmethod) {
			case "dealer-list":
			    Dealerapigeectrl DealerServer = new Dealerapigeectrl();
				DealerServer.getDealerList(messageContext,executionContext);
				break;
			case "dropdown-options":
			    Settingsapigeectrl settingsOptionsCall = new Settingsapigeectrl();
				settingsOptionsCall.getSettingOptions(messageContext, executionContext, queryType);
				break;
			case "recept-data":
			    Receiptapigeectrl receiptCall = new Receiptapigeectrl();
			    receiptCall.getReceiptInformation(messageContext, executionContext);
                break;
			case "sales-receipt-data":
			    Receiptapigeectrl receiptsalesCall = new Receiptapigeectrl();
			    receiptsalesCall.getSingleReceiptInformation(messageContext, executionContext);
                break;
			case "bank-details":
			    Settingsapigeectrl bankSettings = new Settingsapigeectrl();
			    bankSettings.getCountrywisebanks(messageContext, executionContext, queryType);
				break;
			case "scanner-get-data":
				Scannerapigeectrl storeScanner = new Scannerapigeectrl();
           	 	storeScanner.getScannerData(messageContext,executionContext);
           	 break;
			}
		}
				
		return ExecutionResult.SUCCESS;
	}

}
