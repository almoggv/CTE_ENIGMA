package test.java.component;

import main.java.component.MachineHandler;
import main.java.component.SerializationHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.component.impl.SerializationHandlerImpl;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestSerializationHandlerImpl {

    private SerializationHandler serializationHandler= new SerializationHandlerImpl();
    private MachineHandler machineHandler = new MachineHandlerImpl();

    private void buildMachineSmall(){
        String schemaPath = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\encryption-machine-handler\\src\\main\\resources\\machine-inventory-schema\\ex1-sanity-small.xml";
        //<F|A><I><CC><2,1>
        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        plugBoard.add(new MappingPair<String,String>("F","A"));
        List<Integer> rotorsIds = Arrays.asList(1,2);
        String rotorsstartingPos = "CC";
        try{
            machineHandler.buildMachinePartsInventory(schemaPath);
            machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
        }
        catch(Exception e){
            System.out.println("Failed to build machine");
        }
    }

    @Test
    public void testSerialization(){
        String outfilePath = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\encryption-machine-handler\\src\\main\\resources\\savedstates\\testTarget.dat";
        buildMachineSmall();
        try{
            serializationHandler.saveMachineHandlerToFile(outfilePath,machineHandler);
        }
        catch (Exception e){
            System.out.println("Error:" + e.getMessage());
            Assert.assertEquals(true,false);
        }

        Paths.get(outfilePath);
    }

    @Test
    public void testDeserialization(){
        String infilePath = "C:\\Dev\\GitProjects\\CTE_JAVA_ENIGMA\\encryption-machine-handler\\src\\main\\resources\\savedstates\\testTarget.dat";
        try{
            machineHandler = serializationHandler.loadMachineHandlerFromFile(infilePath);
        }
        catch (Exception e){
            System.out.println("Error:" + e.getMessage());
            Assert.assertEquals(true,false);
        }

        Assert.assertTrue(machineHandler.getInventoryInfo().isPresent());
        Assert.assertTrue(machineHandler.getMachineState().isPresent());
    }
}
