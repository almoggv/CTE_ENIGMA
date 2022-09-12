package test.java.decryptionManager;

import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import main.java.manager.DecryptionManager;
import main.java.manager.DictionaryManager;
import main.java.manager.impl.DecryptionManagerImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDecryptionManager {

    @Test
    public void testEasyBruteSmallABC() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2);

        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("AF").get());

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        int numberOfAgents = 10 ;
        int taskSize = 2;

        DictionaryManager.loadDictionary(path);


        String encrypted = machineHandler.encrypt("ABBA");
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.EASY, taskSize) ;
        manager.bruteForceDecryption(encrypted);

    }
 @Test
    public void testEasyBruteEx2Basic() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic.xml";

        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2,3);

        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("?FY").get());

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        int numberOfAgents = 10 ;
        int taskSize = 5;

        DictionaryManager.loadDictionary(path);

//        String encrypted = machineHandler.encrypt("water under their battle ");
        String encrypted = machineHandler.encrypt("if fire inferno blue than midnight ");
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.EASY, taskSize) ;
        manager.bruteForceDecryption(encrypted);
    }

    @Test
    public void testIntermediateBruteSmallABCRefI() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);

//        ReflectorsId refid = ReflectorsId.II;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2);
        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("BF").get());

//BF , I , encrypt
        machineHandler.assembleMachine(ReflectorsId.I, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
        String encrypted = machineHandler.encrypt("ABBA");
        System.out.println("input: ABBA encrypted to: " + encrypted );
        //decrypt

        rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("BF").get());
        machineHandler.assembleMachine(ReflectorsId.I, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
        encrypted = machineHandler.encrypt("DACF");
        System.out.println("input: DACF encrypted to: " + encrypted );

      //BC, I
        rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("BC").get());
        machineHandler.assembleMachine(ReflectorsId.I, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
        encrypted = machineHandler.encrypt("DACF");
        System.out.println("input: DACF encrypted to: " + encrypted );


        int numberOfAgents = 10 ;
        int taskSize = 2;
        DictionaryManager.loadDictionary(path);
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.INTERMEDIATE, taskSize) ;
        manager.bruteForceDecryption("DACF");
    }
  @Test
    public void testIntermediateBruteSmallABCRefII() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.II;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2);
        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("BF").get());

//BF , I , encrypt
        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
        String encrypted = machineHandler.encrypt("ABBA");
        System.out.println("input: ABBA encrypted to: " + encrypted );
        //decrypt

//        rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("BF").get());
//        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
//        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
//        encrypted = machineHandler.encrypt("DACF");
//        System.out.println("input: DACF encrypted to: " + encrypted );
//
//      //BC, I
//        rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("BC").get());
//        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
//        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
//        encrypted = machineHandler.encrypt("DACF");
//        System.out.println("input: DACF encrypted to: " + encrypted );

        int numberOfAgents = 10 ;
        int taskSize = 2;
        DictionaryManager.loadDictionary(path);
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.INTERMEDIATE, taskSize) ;
        manager.bruteForceDecryption("CEDD");
    }

    @Test
    public void testIntermediateBruteEx2Basic() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic.xml";

        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.III;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2,3);
        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("A? ").get());

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        String input = "water";
//        String input = "water under their battle";
        String encrypted = machineHandler.encrypt(input);
        System.out.println("input: " +input + " encrypted to: " + encrypted);

        //to check if the same in both reflectors
//        machineHandler.assembleMachine(ReflectorsId.II, rotorsIds,rotorsstartingPos,plugBoard);
//        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
//        encrypted = machineHandler.encrypt(input);
//        System.out.println("input: " +input + " encrypted to: " + encrypted);


        int numberOfAgents = 10 ;
        int taskSize = 5;

        DictionaryManager.loadDictionary(path);
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.INTERMEDIATE, taskSize) ;
        manager.bruteForceDecryption(encrypted);
    }
    @Test
    public void testReflectors() throws Exception {
        //works only if ref III
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic.xml";
        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.III;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2,3);
        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix(" ?A").get());
        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        String input = "EP!RF";
        String encrypted = machineHandler.encrypt(input);
        System.out.println("input: " +input + " encrypted to: " + encrypted + " need to be : WATER");

        rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("X?L").get());
        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);
        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

         input = "EP!RF";
         encrypted = machineHandler.encrypt(input);
        System.out.println("input: " +input + " encrypted to: " + encrypted + " need to be: GREEN ");

        //to check if the same in both reflectors
//        machineHandler.assembleMachine(ReflectorsId.II, rotorsIds,rotorsstartingPos,plugBoard);
//        System.out.println("Initial Machine State:" + machineHandler.getMachineState());
//        encrypted = machineHandler.encrypt(input);
//        System.out.println("input: " +input + " encrypted to: " + encrypted);


/*
res of test: testIntermediateBruteEx2Basic

Initial Machine State:Optional[MachineState(reflectorId=III, rotorIds=[1, 2, 3], rotorsHeadsInitialValues=[ , ?, A], plugMapping=[], notchDistancesFromHead=[13, 26, 25])]
input: water encrypted to: EP!RF
IN agentManager run!
found a candidate: EP!RF-->GREEN ---- ref: I ,init pos: [X, ?, L]
found a candidate: EP!RF-->WATER ---- ref: I ,init pos: [ , ?, A]
found a candidate: EP!RF-->GREEN ---- ref: II ,init pos: [X, ?, L]
found a candidate: EP!RF-->WATER ---- ref: II ,init pos: [ , ?, A]
found a candidate: EP!RF-->GREEN ---- ref: III ,init pos: [X, ?, L]
found a candidate: EP!RF-->WATER ---- ref: III ,init pos: [ , ?, A]
 */

    }
}
