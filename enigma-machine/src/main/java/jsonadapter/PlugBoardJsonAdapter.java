package jsonadapter;

import com.google.gson.*;
import component.PlugBoard;
import component.impl.PlugBoardImpl;

import java.lang.reflect.Type;

public class PlugBoardJsonAdapter implements JsonSerializer<PlugBoard>, JsonDeserializer<PlugBoard> {

    @Override
    public PlugBoard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        PlugBoardImpl newWheel = gson.fromJson(json,PlugBoardImpl.class);
        return newWheel;
    }

    @Override
    public JsonElement serialize(PlugBoard src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();
        return gson.toJsonTree(src,PlugBoardImpl.class);
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PlugBoard.class, new PlugBoardJsonAdapter())
                .create();
        return gson;
    }
}
