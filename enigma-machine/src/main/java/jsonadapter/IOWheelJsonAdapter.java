package jsonadapter;

import com.google.gson.*;
import component.IOWheel;
import component.impl.IOWheelImpl;

import java.lang.reflect.Type;

public class IOWheelJsonAdapter implements JsonSerializer<IOWheel>, JsonDeserializer<IOWheel> {

    @Override
    public IOWheel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        IOWheelImpl newWheel = gson.fromJson(json,IOWheelImpl.class);
        return newWheel;
    }

    @Override
    public JsonElement serialize(IOWheel src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        return gson.toJsonTree(src,IOWheelImpl.class);
    }

    public static Gson buildGsonLoginPayloadAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IOWheel.class, new IOWheelJsonAdapter())
                .create();
        return gson;
    }
}
