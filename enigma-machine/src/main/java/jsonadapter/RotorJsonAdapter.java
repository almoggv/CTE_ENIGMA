package jsonadapter;

import com.google.gson.*;
import component.Rotor;
import component.impl.RotorImpl;

import java.lang.reflect.Type;

public class RotorJsonAdapter implements JsonSerializer<Rotor>, JsonDeserializer<Rotor> {

    @Override
    public Rotor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        RotorImpl newWheel = gson.fromJson(json,RotorImpl.class);
        return newWheel;
    }

    @Override
    public JsonElement serialize(Rotor src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        return gson.toJsonTree(src,RotorImpl.class);
    }

    public static Gson buildGsonLoginPayloadAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Rotor.class, new RotorJsonAdapter())
                .create();
        return gson;
    }
}
