package Services;

import com.google.gson.JsonObject;

public interface NonOcrServices {
    Object checkSavingtheNonOcrData(JsonObject data, String fullPathImage, String devlogopath);
}
