package main.java;

import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.generictype.MappingPair;
import main.java.enums.ReflectorsId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        mainToCheckSanitySmall();
//        MachineHandler machineHandler =new MachineHandlerImpl();
//        String relativePath = "./enigma-machine/src/main/resources/ex1-sanity-small.xml";
//        try {
//            machineHandler.buildMachinePartsInventory(relativePath);
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//
//        machineHandler.assembleMachine();

    }

    private static void mainToCheckSanitySmall() {
        MachineHandler machineHandler =new MachineHandlerImpl();
        String relativePath = "./enigma-machine/src/main/resources/ex1-sanity-small.xml";
        try {
            machineHandler.buildMachinePartsInventory(relativePath);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

//      <F|A><I><CC><2,1>
//      sets reflector, plugBoardMapping, rotors and their starting position
        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        plugBoard.add(new MappingPair<String,String>("F","A"));
        //plugpair.add(new MappingPair<String,String>("B","C"));
        List<Integer> rotorsIds = Arrays.asList(1,2);
        List<Integer> rotorsstartingPos = Arrays.asList(2,2);

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
//        String input = "zzz";
//        String output = machineHandler.encrypt(input);

        List<String> inputs = Arrays.asList(new String[]{"AABBCCDDEEFF" , "FEDCBADDEF", "FEDCBAABCDEF" , "AFBFCFDFEFFF" , "AAAEEEBBBDDDCCCFFF"});
        List<String> outputs = Arrays.asList(new String[]{"CEEFBDFCDAAB" , "BACACFFCDD" , "BACACFEEAFBB", "CDEBBAFADDAB" , "CEFDABCEFFCEDABABD"});

        for (int i = 0; i < inputs.size(); i++) {
            String input = inputs.get(i);
            String wantedOutput = outputs.get(i);
            machineHandler.resetToLastSetState();
            String output = machineHandler.encrypt(input);
            System.out.println("the output is: " + output + " is correct?: "+ wantedOutput.equals(output));
        }
    }
}
