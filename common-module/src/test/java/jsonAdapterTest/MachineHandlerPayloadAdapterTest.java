package jsonAdapterTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import component.MachineHandler;
import component.PlugBoard;
import component.Rotor;
import component.impl.MachineHandlerImpl;
import dto.MachineHandlerPayload;
import jsonadapter.MachineHandlerJsonAdapter;
import jsonadapter.MachineHandlerPayloadJsonAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MachineHandlerPayloadAdapterTest {

    public static final String SCHEMA_FILE = "\\ex3-basic.xml";


    @Test
    public void testSerializtion() throws Exception {
        File resourcesDirectory = new File("src/test/resources");
        String schemaFileName = resourcesDirectory.getAbsolutePath() + SCHEMA_FILE;

        MachineHandler machineHandler = new MachineHandlerImpl();
        machineHandler.buildMachinePartsInventory(schemaFileName);
        machineHandler.assembleMachine();
        String deserializedMachine = MachineHandlerJsonAdapter.buildGsonAdapter().toJson(machineHandler);
        MachineHandler newhandler = MachineHandlerJsonAdapter.buildGsonAdapter().fromJson(deserializedMachine,MachineHandler.class);



        MachineHandlerPayload payload = new MachineHandlerPayload("Blabla", machineHandler);
        Gson gson =  MachineHandlerPayloadJsonAdapter.buildGsonAdapter();
        String desrializedPayload = gson.toJson(payload,MachineHandlerPayload.class);
        MachineHandlerPayload newPayload = gson.fromJson(desrializedPayload,MachineHandlerPayload.class);
        MachineHandler newMachineHandler = newPayload.getMachineHandler();
        Assert.assertTrue(newMachineHandler != null && newMachineHandler.getInventoryInfo().isPresent() && newMachineHandler.getMachineState().isPresent());
    }

}
