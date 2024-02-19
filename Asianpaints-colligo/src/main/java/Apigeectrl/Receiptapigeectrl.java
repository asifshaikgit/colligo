package Apigeectrl;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;

import Repository.SalesDashboardlistrepo;
import Services.Salesdashboard;

public class Receiptapigeectrl {
	
    private Salesdashboard salesDash;
    
    public Receiptapigeectrl() {
		// TODO Auto-generated constructor stub
    	salesDash = new SalesDashboardlistrepo();
	}
    
    public ExecutionResult getReceiptInformation(MessageContext messageContext, ExecutionContext executionContext) {
        // TODO Auto-generated method stub
        return ExecutionResult.SUCCESS;
    }

    public ExecutionResult getSingleReceiptInformation(MessageContext messageContext, ExecutionContext executionContext) {
        // TODO Auto-generated method stub
        String receiptId = messageContext.getVariable("request.queryparam.receiptId").toString();
        String userid = messageContext.getVariable("request.queryparam.userid").toString();
        Object salesListdashboard = salesDash.getSalesReceiptData(receiptId, userid);
        messageContext.getMessage().setContent(salesListdashboard.toString());
        return ExecutionResult.SUCCESS;
    }

}
