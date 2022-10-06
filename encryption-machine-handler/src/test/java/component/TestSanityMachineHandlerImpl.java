package component;

import component.EncryptionMachine;
import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import enums.ReflectorsId;
import generictype.MappingPair;
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
            String sanitySmallXmlPath = "./machine-inventory-schema/ex1-sanity-small.xml";
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
            String sanityBigXmlPath = "./machine-inventory-schema/ex1-sanity-paper-enigma.xml";
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
    public void testWierdABC(){
        try{
            machineHandler.buildMachinePartsInventory("C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema\\ex1-extra-test.xml");

            ReflectorsId refid = ReflectorsId.I;
            List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
            List<Integer> rotorsIds = Arrays.asList(1,5,4,3,2);

            String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("f *bd").get());

            plugBoard.add(new MappingPair<String,String>("F","A"));
            plugBoard.add(new MappingPair<String,String>("B","*"));
            plugBoard.add(new MappingPair<String,String>("C"," "));

            machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
            System.out.println("Initial Machine State:" + machineHandler.getMachineState());
            List<String> inputs = Arrays.asList(new String[]{"aaaaabbaaaababababbababababbabab***********************************bababababbaaaaaaaadddddddddddddddddddd            ****************************FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbccccccccccccccccccccccccccccccccccccccccccDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDaaaaaaaaaaaaaaaaaaaaaabbababab", "defeca"/*, "ENIGMAMACHINEROCKS" , "WOWCANTBELIEVEITACTUALLYWORKS" , "JAVARULES"*/});

            for (int i = 0; i < inputs.size(); i++) {
                String input = inputs.get(i);
                machineHandler.resetToLastSetState();
                String output = machineHandler.encrypt(input);
                System.out.println(i + ". the output is: " + output  /* + wantedOutput.equals(output)*/);
            }
        }
        catch(Exception e){
                System.out.println(e.getMessage());
        }
    }

    @Test
    public void testMachineClone(){
        try{
            String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic.xml";
            machineHandler.buildMachinePartsInventory(path);

            ReflectorsId refid = ReflectorsId.I;
            List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
            List<Integer> rotorsIds = Arrays.asList(1,2,3);

            String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("AB ").get());

            plugBoard.add(new MappingPair<String,String>("F","A"));
            plugBoard.add(new MappingPair<String,String>("B","?"));
            plugBoard.add(new MappingPair<String,String>("C"," "));

            machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

            EncryptionMachine clonedMachine = machineHandler.getEncryptionMachineClone();
            System.out.println("cloned");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void testBadReflectorsIdSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error - reflector-id.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
        Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
   @Test
    public void testBadRotorCountsIdSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error - bad-rotor-count.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
       Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
    @Test
    public void testBadRotorCountsId2Schema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error - bad-rotor-count2.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
       Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
   @Test
    public void testBadReflectionMappingIdSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error - reflector-mapping.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
       Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
   @Test
    public void testBadRotorMappingSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error - rotor-mapping.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
           Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
    @Test
    public void testBadRotorIdSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error - rotor id.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
        Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
    @Test
    public void testBadNotchSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error -notch.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
        Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
    @Test
    public void testABCSchema(){
        try{
            String badReflectorIdXml = "./machine-inventory-schema/extra-errors/ex1-error-odd-abc.xml";
            machineHandler.buildMachinePartsInventory(badReflectorIdXml);
        }
        catch(Exception e){
            System.out.println(e.getMessage());

        }
        Assert.assertFalse(machineHandler.getInventoryInfo().isPresent());
    }
}
