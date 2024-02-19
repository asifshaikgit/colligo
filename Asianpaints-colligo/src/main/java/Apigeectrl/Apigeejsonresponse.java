package Apigeectrl;

import static Constants.AsianConstants.STATUS_CODE;
import static Constants.AsianConstants.STATUS_FLAG;
import static Constants.AsianConstants.STATUS_MESSAGE;

import org.json.JSONException;
import org.json.JSONObject;

import com.apigee.flow.message.MessageContext;

public class Apigeejsonresponse {
	
	public void errorResponse(MessageContext messageContext, String errCode, Object errResMsg) {
		JSONObject responseMap = new JSONObject();
		try {
			responseMap.put(STATUS_CODE, errCode);
			responseMap.put(STATUS_FLAG, 1);
			responseMap.put(STATUS_MESSAGE, errResMsg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		messageContext.getMessage().setContent(responseMap.toString());
	}

}
