package test.java.component;

import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSanityMachineHandlerImpl {

    private MachineHandler machineHandler = new MachineHandlerImpl();

    @Test
    public void testSanitySmall(){
        try{
            String sanitySmallXmlPath = "./src/main/resources/machine-inventory-schema/ex1-sanity-small.xml";
            machineHandler.buildMachinePartsInventory(sanitySmallXmlPath);
            //<F|A><I><CC><2,1>
            ReflectorsId refid = ReflectorsId.I;
            List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
            plugBoard.add(new MappingPair<String,String>("F","A"));
            List<Integer> rotorsIds = Arrays.asList(1,2);
            String rotorsstartingPos = "CC";

            machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
            List<String> inputs = Arrays.asList(new String[]{"AABBCCDDEEFF" , "FEDCBADDEF", "FEDCBAABCDEF" , "AFBFCFDFEFFF" , "AAAEEEBBBDDDCCCFFF"});
            List<String> outputs = Arrays.asList(new String[]{"CEEFBDFCDAAB" , "BACACFFCDD" , "BACACFEEAFBB", "CDEBBAFADDAB" , "CEFDABCEFFCEDABABD"});
            for (int i = 0; i < inputs.size(); i++) {
                String input = inputs.get(i);
                String wantedOutput = outputs.get(i);
                machineHandler.resetToLastSetState();
                String output = machineHandler.encrypt(input);
                System.out.println( i+". the output is: " + output + "\nis correct?: "+ wantedOutput.equals(output));
                Assert.assertEquals(wantedOutput,output);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testSanityBig(){
        try{
            String sanityBigXmlPath = "./src/main/resources/machine-inventory-schema/ex1-sanity-paper-enigma.xml";
            machineHandler.buildMachinePartsInventory(sanityBigXmlPath);
            //<I><ODX><1,2,3>
            ReflectorsId refid = ReflectorsId.I;
            List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
            List<Integer> rotorsIds = Arrays.asList(3,2,1);
            String rotorsstartingPos = "XDO";
            machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
            System.out.println("Initial Machine State:" + machineHandler.getMachineState());
            List<String> inputs = Arrays.asList(new String[]{"THERAINISDROPPING", "HELLOWORLD", "ENIGMAMACHINEROCKS" , "WOWCANTBELIEVEITACTUALLYWORKS" , "JAVARULES"});
            List<String> outputs = Arrays.asList(new String[]{"APZTICDXRVMWQHBHU" , "DLTBBQVPQV" , "QMJIDORMMYQBJDVSBR", "CVRDIZWDAWQKUKBVHJILPKRNDXWIY" , "MRUHFRZZR"});
            for (int i = 0; i < inputs.size(); i++) {
                String input = inputs.get(i);
                String wantedOutput = outputs.get(i);
                machineHandler.resetToLastSetState();
                System.out.println("Machine State Before Encryption number:" + i + ":\n" + machineHandler.getMachineState());
                String output = machineHandler.encrypt(input);
                System.out.println(i + ". the output is: " + output + "\nis correct?: " + wantedOutput.equals(output));
                Assert.assertEquals(wantedOutput, output);
            }
        }
        catch(Exception e){
                System.out.println(e.getMessage());
        }


    }


    @Test
    public void testBadReflectorsIdSchema(){
        try{
            String badReflectorIdXml = "./src/main/resources/machine-inventory-schema/extra-errors/ex1-error - reflector-id.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }

        Assert.assertNull(machineHandler.getInventoryInfo());
    }

}
