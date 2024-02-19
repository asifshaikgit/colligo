package Services;

import com.google.gson.JsonObject;

public interface scannerservice {
	Object savePdfData(JsonObject storePdfObject);
	Object getPdfData(String userid, String filter, String search, String fromdate, String todate);
}
