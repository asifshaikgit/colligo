package Apigeectrl;

import static Constants.AsianConstants.ApigeeLogoPath;
import static Constants.AsianConstants.Apigeeimagepath;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Repository.Base64decode;
import Repository.NonOcrRepository;
import Repository.Ocreopsitory;
import Services.Base64;
import Utilres.ValidationCtrl;

public class Ocrapigeectrl {
	
	private ValidationCtrl validate;
    private Ocreopsitory ocrCall;
    private NonOcrRepository nonOcrCall;
    private Base64 base64image;
	
	public Ocrapigeectrl() {
		// TODO Auto-generated constructor stub
		validate = new ValidationCtrl();
		ocrCall = new Ocreopsitory();
		nonOcrCall = new NonOcrRepository();
		base64image = new Base64decode();
	}
    
    public ExecutionResult getUploadocrUrlDataDealer(MessageContext messageContext, ExecutionContext executionContext) {
        JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
        Object validatecheck = validate.validateOcrRequest(data);
        if(validatecheck.equals(true)) {
            Object Ocresponse = ocrCall.getChecktheOcrData(data);
            messageContext.getMessage().setContent(Ocresponse.toString());
        } else {
            messageContext.getMessage().setContent(validatecheck.toString());
        }
        return ExecutionResult.SUCCESS;
    }

    public ExecutionResult getSaveReceiptInformation(MessageContext messageContext, ExecutionContext executionContext) {
        // TODO Auto-generated method stub
        JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
        String imagepath = "/tmp/hsperfdata_apigee/";
        String fullPathImage;
        try {
            fullPathImage = URLDecoder.decode(imagepath, "UTF-8");
            Object validatecheck = validate.validateNonOCRCollectionApidata(data);
            if(validatecheck.equals(true)) {
            	Object dataNonOcr = nonOcrCall.checkSavingtheNonOcrData(data, fullPathImage, ApigeeLogoPath);
//            	if(data.get("receiptid").getAsString().isEmpty()) {
//            		dataNonOcr = nonOcrCall.getSaveNonOcrData(data, fullPathImage, ApigeeLogoPath);
//            	} else {
//            		dataNonOcr = nonOcrCall.getUpdateNonOcrData(data, fullPathImage, ApigeeLogoPath);
//            	}
                messageContext.getMessage().setContent(dataNonOcr.toString());
            } else {
                messageContext.getMessage().setContent(validatecheck.toString());
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return ExecutionResult.SUCCESS;
    }

    public ExecutionResult getCaptureAzurelink(MessageContext messageContext, ExecutionContext executionContext) {
        // TODO Auto-generated method stub
//        Apigeeimagepath
        JsonObject data = new Gson().fromJson(messageContext.getVariable("request.content").toString(), JsonObject.class);
        Object validatecheck = validate.validateImageCapture(data);
        if(validatecheck.equals(true)) {
            Object imageCapturedata = base64image.getLoadDataBase64image(data,Apigeeimagepath);
            messageContext.getMessage().setContent(imageCapturedata.toString());
        } else {
            messageContext.getMessage().setContent(validatecheck.toString());
        }
        return ExecutionResult.SUCCESS;
    }

}
