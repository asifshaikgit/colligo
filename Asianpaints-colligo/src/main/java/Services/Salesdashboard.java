package Services;

import com.google.gson.JsonObject;

public interface Salesdashboard {
    Object ListSalesdashboard(JsonObject data, String string);
    Object getSalesReceiptData(String receiptId, String userid);
    Object getUpdateReceiptDepoinfo(JsonObject data);
}
