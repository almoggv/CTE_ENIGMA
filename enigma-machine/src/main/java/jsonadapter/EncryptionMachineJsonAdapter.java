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
        String serializedPlugBoard = PlugBoardJsonAdapter.buildGsonAdapter().toJson(src.getPlugBoard());
        String serializedReflector = ReflectorJsonAdapter.buildGsonAdapter().toJson(src.getReflector());
        List<Rotor> rotors = src.getRotors();
        List<String> serializedRotorsList = new ArrayList<>();
        for (Rotor rotor : rotors ) {
            String serializedRotor = ReflectorJsonAdapter.buildGsonAdapter().toJson(rotor);
            serializedRotorsList.add(serializedRotor);
        }
        String serializedRotors = new Gson().toJson(serializedRotorsList);
        String serializedIoWheel = IOWheelJsonAdapter.buildGsonAdapter().toJson(src.getIoWheel());

        object.addProperty(plugBoardName,serializedPlugBoard);
        object.addProperty(reflectorName,serializedReflector);
        object.addProperty(rotorsName,serializedRotors);
        object.addProperty(ioWheelName,serializedIoWheel);
        return object;
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(EncryptionMachine.class, new EncryptionMachineJsonAdapter())
                .create();
        return gson;
    }
}
