package jsonadapter;

import com.google.gson.*;
import component.Reflector;
import component.impl.ReflectorImpl;

import java.lang.reflect.Type;

public class ReflectorJsonAdapter implements JsonSerializer<Reflector>, JsonDeserializer<Reflector> {

    @Override
    public Reflector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        ReflectorImpl newWheel = gson.fromJson(json,ReflectorImpl.class);
        return newWheel;
    }

    @Override
    public JsonElement serialize(Reflector src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        return gson.toJsonTree(src,ReflectorImpl.class);
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Reflector.class, new ReflectorJsonAdapter())
                .create();
        return gson;
    }
}
