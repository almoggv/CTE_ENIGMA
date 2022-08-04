package main.java;

import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.component.impl.MappingPair;
import main.java.enums.ReflectorsId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MachineHandler machineHandler =new MachineHandlerImpl();
        String abolutePath = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\ex1-sanity-small.xml";
        try {
            machineHandler.buildMachinePartsInventory(abolutePath);
        }
        catch (Exception e){
            System.out.printf(e.getMessage());
        }


    //    <F|A><I><CC><2,1>

//        sets reflector, plugBoardMapping, rotors and their starting position
        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugpair = new ArrayList<MappingPair<String,String>>();
        plugpair.add(new MappingPair<String,String>("F","A"));
        List<Integer> rotorsIds = Arrays.asList(1,2);
        List<Integer> rotorsstartingPos = Arrays.asList(2,2);

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugpair);

        String input = "AABBCCDDEEFF";
        String output = machineHandler.encrypt(input);
        System.out.println("the output is: " + output + " is correct?: "+ "CEEFBDFCDAAB".equals(output));

//        imp.loadMachineConfiguration("C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\ex1-error-3.xml");
//        imp.loadMachineConfiguration("C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\ex1-sanity-paper-enigma.xml");

//        machineHandler.buildMachine
//                        set
    }
}
