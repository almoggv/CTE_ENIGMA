package jsonadapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import component.*;
import component.impl.EnigmaMachine;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EncryptionMachineJsonAdapter implements JsonSerializer<EncryptionMachine>, JsonDeserializer<EncryptionMachine> {

    private String plugBoardName = "plugBoard";
    private String rotorsName = "rotors";
    private String ioWheelName = "ioWheel";
    private String reflectorName = "reflector";

    @Override
    public EncryptionMachine deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        EnigmaMachine deserializedMachine = new EnigmaMachine();
        JsonObject jsonObject = json.getAsJsonObject();
        PlugBoard plugBoard = PlugBoardJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(plugBoardName),PlugBoard.class);
        Reflector reflector = ReflectorJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(reflectorName), Reflector.class);
        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();
        List<Rotor> rotorsList = RotorJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(rotorsName),rotorListType);
        IOWheel wheel = IOWheelJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(ioWheelName),IOWheel.class);
        deserializedMachine.buildMachine(plugBoard, reflector, rotorsList, wheel);
        return deserializedMachine;
    }

    @Override
    public JsonElement serialize(EncryptionMachine src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();

        object.add(plugBoardName,PlugBoardJsonAdapter.buildGsonAdapter().toJsonTree(src.getPlugBoard(),PlugBoard.class));
        object.add(reflectorName,ReflectorJsonAdapter.buildGsonAdapter().toJsonTree(src.getReflector(),Reflector.class));
        object.add(rotorsName,RotorJsonAdapter.buildGsonAdapter().toJsonTree(src.getRotors(),rotorListType));
        object.add(ioWheelName,IOWheelJsonAdapter.buildGsonAdapter().toJsonTree(src.getIoWheel(),IOWheel.class));

        return object;
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(EncryptionMachine.class, new EncryptionMachineJsonAdapter())
                .create();
        return gson;
    }
}
