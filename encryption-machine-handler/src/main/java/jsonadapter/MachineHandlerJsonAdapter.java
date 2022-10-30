package jsonadapter;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import component.*;
import component.impl.MachineHandlerImpl;
import dto.BattlefieldInfo;
import dto.InventoryComponents;
import dto.MachineState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MachineHandlerJsonAdapter implements JsonSerializer<MachineHandler>, JsonDeserializer<MachineHandler> {

    String plugBoardInventoryName = "plugBoardInventory";
    String rotorsInventoryName = "rotorsInventory";
    String ioWheelInventoryName = "ioWheelInventory";
    String reflectorsInventoryName = "reflectorsInventory";
    String expectedNumOfRotorsName = "expectedNumOfRotors";
    String battlefieldInfoName = "battlefieldInfo";
    String encryptionMachineName = "encryptionMachine";
    String initialMachineStateName = "initialMachineState";
    String machineStatisticsHistoryStateName = "machineStatisticsHistory";  //created on initialization
    String xmlSchemaVerifierName = "xmlSchemaVerifier";                     //created on initialization

    @Override
    public MachineHandler deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        MachineHandler machineHandler = new MachineHandlerImpl();
        JsonElement test1 = jsonObject.get(plugBoardInventoryName);
        JsonObject test2 = jsonObject.get(plugBoardInventoryName).getAsJsonObject();
        String test3 = jsonObject.get(plugBoardInventoryName).getAsString();

        PlugBoard plugboardInventory = PlugBoardJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(plugBoardInventoryName),PlugBoard.class);
        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();
        List<Rotor> rotorsListInventory = RotorJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(rotorsInventoryName),rotorListType);
        IOWheel wheelInventory = IOWheelJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(ioWheelInventoryName),IOWheel.class);
        Type reflectorListType = new TypeToken<ArrayList<Reflector>>(){}.getType();
        List<Reflector> reflectorInventory = ReflectorJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(reflectorsInventoryName), reflectorListType);
        int expectedNumOfRotors = Integer.valueOf(jsonObject.get(expectedNumOfRotorsName).getAsString());
        InventoryComponents inventoryComponents = new InventoryComponents(plugboardInventory,rotorsListInventory,wheelInventory,reflectorInventory,expectedNumOfRotors);
        machineHandler.buildMachinePartsInventory(inventoryComponents);
        Gson gson = new Gson();
        machineHandler.setBattlefieldInfo(gson.fromJson(jsonObject.get(battlefieldInfoName),BattlefieldInfo.class));
        EncryptionMachine encryptionMachine = EncryptionMachineJsonAdapter.buildGsonAdapter().fromJson(jsonObject.get(encryptionMachineName),EncryptionMachine.class);
        machineHandler.setEncryptionMachine(encryptionMachine);
        machineHandler.setInitialMachineState(gson.fromJson(jsonObject.get(initialMachineStateName),MachineState.class));
        return machineHandler;
    }

    @Override
    public JsonElement serialize(MachineHandler src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        Gson gson = new Gson();
        //Inventory parts
        InventoryComponents inventoryComponents = src.getInventoryComponents().get();
        String deserializedPlugboardInventory = PlugBoardJsonAdapter.buildGsonAdapter().toJson(inventoryComponents.getPlugBoardInventory());
        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();
        String deserializedRotorsListInventory = RotorJsonAdapter.buildGsonAdapter().toJson(inventoryComponents.getRotorsInventory());
        String deserializedWheelInvnetory = IOWheelJsonAdapter.buildGsonAdapter().toJson(inventoryComponents.getIoWheelInventory());
        Type reflectorListType = new TypeToken<ArrayList<Reflector>>(){}.getType();
        String deserializedReflectorsInventory = ReflectorJsonAdapter.buildGsonAdapter().toJson(inventoryComponents.getReflectorsInventory());
        String deserializedNumOfRotors = String.valueOf(inventoryComponents.getExpectedNumOfRotors());
        // BattleField & Machine:
        BattlefieldInfo battlefieldInfo = (src.getBattlefieldInfo() == null) ? new BattlefieldInfo() : src.getBattlefieldInfo().get();
        String deserializedBattleField = gson.toJson(battlefieldInfo);
        String deserializedEncryptionMachine = EncryptionMachineJsonAdapter.buildGsonAdapter().toJson(src.getEncryptionMachineClone());
        String deserialzedInitialMachineState = gson.toJson(src.getInitialMachineState().get());
        //Assembling Json Object
        object.addProperty(plugBoardInventoryName,deserializedPlugboardInventory);
        object.addProperty(rotorsInventoryName,deserializedRotorsListInventory);
        object.addProperty(ioWheelInventoryName,deserializedWheelInvnetory);
        object.addProperty(reflectorsInventoryName,deserializedReflectorsInventory);
        object.addProperty(expectedNumOfRotorsName,deserializedNumOfRotors);
        object.addProperty(battlefieldInfoName,deserializedBattleField);
        object.addProperty(encryptionMachineName,deserializedEncryptionMachine);
        object.addProperty(initialMachineStateName,deserialzedInitialMachineState);

        return object;
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineHandler.class, new MachineHandlerJsonAdapter())
                .create();
        return gson;
    }
}
