package jsonadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dto.LoginPayload;
import service.PropertiesService;

import java.lang.reflect.Type;

public class LoginPayloadSerializer implements JsonSerializer<LoginPayload>{
    @Override
    public JsonElement serialize(LoginPayload src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty(PropertiesService.getMessageAttributeName(),src.getMessage());
        object.addProperty(PropertiesService.getTokenAttributeName(),src.getAccessToken());
        return object;
    }
}
