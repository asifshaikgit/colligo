package Services;

import com.google.gson.JsonObject;

public interface forgotservice {
	Object checkUserId(JsonObject data);
	Object verifyEmailOtp(JsonObject data);
}
