package decryptionManager;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.AgentDecryptionInfo;
import enums.DecryptionDifficultyLevel;
import enums.ReflectorsId;
import generictype.MappingPair;
import manager.DecryptionManager;
import manager.DictionaryManagerStatic;
import manager.impl.DecryptionManagerImpl;
import org.junit.Test;
import testservice.PropertiesService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TestDecryptionManager {

    @Test
    public void testEasyBruteSmallABC() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = PropertiesService.getTestSchemaEx2BasicEasy();

        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2);

        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("AF").get());

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        int numberOfAgents = 10 ;
        int taskSize = 2;

        DictionaryManagerStatic.loadDictionary(path);


        String encrypted = machineHandler.encrypt("ABBA");
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.EASY, taskSize) ;
        Consumer<AgentDecryptionInfo> stopAndShowCandidate = candidate -> {
            manager.stopWork();
            System.out.println("Candidate Found = " + candidate);

        };
//        UIAdapter uiAdapter = new UIAdapter(stopAndShowCandidate);
//        manager.setUiAdapter(uiAdapter);
        manager.bruteForceDecryption(encrypted);
        manager.awaitWork();
    }

    @Test
    public void testEasyBruteEx2Basic() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = PropertiesService.getTestSchemaEx2Basic();
        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2,3);

        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("?FY").get());

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        int numberOfAgents = 20 ;
        int taskSize = 5;

        DictionaryManagerStatic.loadDictionary(path);

//        String encrypted = machineHandler.encrypt("water under their battle ");
        String encrypted = machineHandler.encrypt("if fire inferno blue than midnight ");
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.EASY, taskSize) ;
        manager.bruteForceDecryption(encrypted);
        System.out.println("JUnit Test - going to sleep - 1");
        Thread.sleep(3000);
        System.out.println("JUnit Test - Pausing work - 1");
        manager.pauseWork();
        Thread.sleep(4000);
        System.out.println("JUnit Test - Resuming work - 1");
        manager.resumeWork();
        System.out.println("JUnit Test - going to sleep - 2");
        Thread.sleep(4000);
        manager.pauseWork();
        Thread.sleep(4000);
        System.out.println("JUnit Test - Resuming work - 2");
        manager.resumeWork();

        System.out.println("JUnit Test - resumed and awaiting");
        manager.awaitWork();

    }

    @Test
    public void testIntermediateBruteSmallABCRefI() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = PropertiesService.getTestSchemaEx2BasicEasy();

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
        DictionaryManagerStatic.loadDictionary(path);
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
        DictionaryManagerStatic.loadDictionary(path);
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

        DictionaryManagerStatic.loadDictionary(path);
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

    @Test
    public void testHardBruteSmallABC() throws Exception {
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

        DictionaryManagerStatic.loadDictionary(path);


        String encrypted = machineHandler.encrypt("ABBA");
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.HARD, taskSize);

        manager.bruteForceDecryption(encrypted);

    }

    @Test
    public void testHardBruteEx2Basic() throws Exception {
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

        int numberOfAgents = 10 ;
        int taskSize = 5;

        DictionaryManagerStatic.loadDictionary(path);
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.HARD, taskSize) ;
        manager.bruteForceDecryption(encrypted);
    }

    @Test
    public void testImpossibleBruteSmallABC() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";

        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
//        List<Integer> rotorsIds = Arrays.asList(1,2);
        List<Integer> rotorsIds = Arrays.asList(2,3);

        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("AF").get());

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

        int numberOfAgents = 5 ;
        int taskSize = 2;

        DictionaryManagerStatic.loadDictionary(path);

        String encrypted = machineHandler.encrypt("ABBA");
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.IMPOSSIBLE, taskSize);

        manager.bruteForceDecryption(encrypted);

    }

}
