package jsonadapter;

import com.google.gson.*;
import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.LoginPayload;
import dto.MachineHandlerPayload;
import service.PropertiesService;

import java.lang.reflect.Type;

public class MachineHandlerPayloadJsonAdapter implements JsonSerializer<MachineHandlerPayload> ,JsonDeserializer<MachineHandlerPayload>{

    private static final String messageName = "message";
    private static final String machineHandlerName = "machineHandler";

    @Override
    public JsonElement serialize(MachineHandlerPayload src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty(messageName,src.getMessage());
        object.add(machineHandlerName,MachineHandlerJsonAdapter.buildGsonAdapter().toJsonTree(src.getMachineHandler(), MachineHandler.class));
        return object;
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineHandlerPayloadJsonAdapter.class, new MachineHandlerPayloadJsonAdapter())
                .create();
        return gson;
    }

    @Override
    public MachineHandlerPayload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        MachineHandlerPayload payload = new MachineHandlerPayload();
        payload.setMessage(jsonObject.get(messageName).getAsString());
        payload.setMachineHandler(MachineHandlerJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(machineHandlerName), MachineHandler.class));
        return payload;
    }
}
