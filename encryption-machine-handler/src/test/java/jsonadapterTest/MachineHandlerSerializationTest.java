package jsonadapterTest;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import jsonadapter.MachineHandlerJsonAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class MachineHandlerSerializationTest {

    public static final String SCHEMA_FILE = "\\ex3-basic.xml";


    @Test
    public void testMachineHandlerSerialization() throws Exception {
        File resourcesDirectory = new File("src/test/resources");
        String schemaFileName = resourcesDirectory.getAbsolutePath() + SCHEMA_FILE;

        MachineHandler machineHandler = new MachineHandlerImpl();
        machineHandler.buildMachinePartsInventory(schemaFileName);
        machineHandler.assembleMachine();
        String deserializedMachineHandler = MachineHandlerJsonAdapter.buildGsonAdapter().toJson(machineHandler,MachineHandler.class);
        MachineHandler newMachineHandler = MachineHandlerJsonAdapter.buildGsonAdapter().fromJson(deserializedMachineHandler,MachineHandler.class);
        Assert.assertTrue(newMachineHandler != null && newMachineHandler.getInventoryInfo().isPresent() && newMachineHandler.getMachineState().isPresent());

    }
}
