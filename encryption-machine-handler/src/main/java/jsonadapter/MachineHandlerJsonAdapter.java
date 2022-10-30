package jsonadapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import component.*;
import component.impl.EnigmaMachine;
import dto.BattlefieldInfo;
import dto.EncryptionInfoHistory;
import dto.MachineState;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineHandlerJsonAdapter implements JsonSerializer<MachineHandler>, JsonDeserializer<MachineHandler> {

    String plugBoardInventoryName = "plugBoardInventory";
    String rotorsInventoryName = "rotorsInventory";
    String ioWheelInventoryName = "ioWheelInventory";
    String reflectorsInventoryName = "reflectorsInventory";
    String expectedNumOfRotorsName = "expectedNumOfRotors";
    String battlefieldInfoName = "battlefieldInfo";
    String encryptionMachineName = "encryptionMachine";
    String initialMachineStateName = "initialMachineState";
    String machineStatisticsHistoryStateName = "machineStatisticsHistory";
    String xmlSchemaVerifierName = "xmlSchemaVerifier";

    @Override
    public MachineHandler deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {


//        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();
//        List<Rotor> rotorsList = RotorJsonAdapter.buildGsonLoginPayloadAdapter().fromJson(jsonObject.get(rotorsName),rotorListType);

    }

    @Override
    public JsonElement serialize(MachineHandler src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

//        object.addProperty(plugBoardName,serializedPlugBoard);

        return object;
    }

    public static Gson buildGsonLoginPayloadAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineHandler.class, new MachineHandlerJsonAdapter())
                .create();
        return gson;
    }
}
