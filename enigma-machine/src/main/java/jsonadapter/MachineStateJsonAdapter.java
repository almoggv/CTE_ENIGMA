package jsonadapter;

import com.google.gson.*;

import dto.MachineState;
import service.PropertiesService;

import java.lang.reflect.Type;

public class MachineStateJsonAdapter implements JsonSerializer<MachineState>, JsonDeserializer<MachineState> {
    @Override
    public JsonElement serialize(MachineState src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
//        object.addProperty();

        return object;
    }

    @Override
    public MachineState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    public static Gson buildGsonLoginPayloadSerializer(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineState.class, new MachineStateJsonAdapter())
                .setPrettyPrinting()
                .create();
        return gson;
    }
}
