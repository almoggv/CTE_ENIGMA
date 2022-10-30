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
        Type rotorListType = new TypeToken<ArrayList<Rotor>>(){}.getType();
        Type reflectorListType = new TypeToken<ArrayList<Reflector>>(){}.getType();
        String deserializedNumOfRotors = String.valueOf(inventoryComponents.getExpectedNumOfRotors());
        // BattleField & Machine:
        BattlefieldInfo battlefieldInfo = (src.getBattlefieldInfo() == null) ? new BattlefieldInfo() : src.getBattlefieldInfo().get();
        //Assembling Json Object
        object.add(plugBoardInventoryName,PlugBoardJsonAdapter.buildGsonAdapter().toJsonTree(inventoryComponents.getPlugBoardInventory(),PlugBoard.class));
        object.add(rotorsInventoryName,RotorJsonAdapter.buildGsonAdapter().toJsonTree(inventoryComponents.getRotorsInventory(),rotorListType));
        object.add(ioWheelInventoryName,IOWheelJsonAdapter.buildGsonAdapter().toJsonTree(inventoryComponents.getIoWheelInventory(),IOWheel.class));
        object.add(reflectorsInventoryName,ReflectorJsonAdapter.buildGsonAdapter().toJsonTree(inventoryComponents.getReflectorsInventory(),reflectorListType));
        object.addProperty(expectedNumOfRotorsName,deserializedNumOfRotors);
        object.add(battlefieldInfoName,gson.toJsonTree(battlefieldInfo,BattlefieldInfo.class));
        object.add(encryptionMachineName,EncryptionMachineJsonAdapter.buildGsonAdapter().toJsonTree(src.getEncryptionMachineClone(),EncryptionMachine.class));
        object.add(initialMachineStateName,gson.toJsonTree(src.getInitialMachineState().get(),MachineState.class));

        return object;
    }

    public static Gson buildGsonAdapter(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MachineHandler.class, new MachineHandlerJsonAdapter())
                .create();
        return gson;
    }
}
