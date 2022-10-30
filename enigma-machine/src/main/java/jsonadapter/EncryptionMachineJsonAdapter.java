package jsonadapter;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import component.*;
import component.impl.EnigmaMachine;
import service.PropertiesService;

import java.lang.reflect.Type;
import java.sql.Ref;
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
        PlugBoard plugBoard = PlugBoardJsonAdapter.buildGsonLoginPayloadAdapter().fromJson(jsonObject.get(plugBoardName),PlugBoard.class);
        Reflector reflector = ReflectorJsonAdapter.buildGsonLoginPayloadAdapter().fromJson(jsonObject.get(reflectorName), Reflector.class);
        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();
        List<Rotor> rotorsList = RotorJsonAdapter.buildGsonLoginPayloadAdapter().fromJson(jsonObject.get(rotorsName),rotorListType);
        IOWheel wheel = IOWheelJsonAdapter.buildGsonLoginPayloadAdapter().fromJson(jsonObject.get(ioWheelName),IOWheel.class);
        deserializedMachine.buildMachine(plugBoard, reflector, rotorsList, wheel);
        return deserializedMachine;
    }

    @Override
    public JsonElement serialize(EncryptionMachine src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        String serializedPlugBoard = PlugBoardJsonAdapter.buildGsonLoginPayloadAdapter().toJson(src.getPlugBoard());
        String serializedReflector = ReflectorJsonAdapter.buildGsonLoginPayloadAdapter().toJson(src.getReflector());
        List<Rotor> rotors = src.getRotors();
        List<String> serializedRotorsList = new ArrayList<>();
        for (Rotor rotor : rotors ) {
            String serializedRotor = ReflectorJsonAdapter.buildGsonLoginPayloadAdapter().toJson(rotor);
            serializedRotorsList.add(serializedRotor);
        }
        String serializedRotors = new Gson().toJson(serializedRotorsList);
        String serializedIoWheel = IOWheelJsonAdapter.buildGsonLoginPayloadAdapter().toJson(src.getIoWheel());

        object.addProperty(plugBoardName,serializedPlugBoard);
        object.addProperty(reflectorName,serializedReflector);
        object.addProperty(rotorsName,serializedRotors);
        object.addProperty(ioWheelName,serializedIoWheel);
        return object;
    }

    public static Gson buildGsonLoginPayloadAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(EncryptionMachine.class, new EncryptionMachineJsonAdapter())
                .create();
        return gson;
    }
}
