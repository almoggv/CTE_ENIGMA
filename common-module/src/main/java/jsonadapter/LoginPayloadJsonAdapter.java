package jsonadapter;

import com.google.gson.*;
import dto.LoginPayload;
import service.PropertiesService;

import java.lang.reflect.Type;

public class LoginPayloadJsonAdapter implements JsonSerializer<LoginPayload> ,JsonDeserializer<LoginPayload>{
    @Override
    public JsonElement serialize(LoginPayload src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty(PropertiesService.getMessageAttributeName(),src.getMessage());
        object.addProperty(PropertiesService.getTokenAttributeName(),src.getAccessToken());
        return object;
    }

    public static Gson buildGsonLoginPayloadAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LoginPayload.class, new LoginPayloadJsonAdapter())
                .setPrettyPrinting()
                .create();
        return gson;
    }

    @Override
    public LoginPayload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
