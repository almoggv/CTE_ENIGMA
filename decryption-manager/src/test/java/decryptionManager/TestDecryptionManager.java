package test.java.decryptionManager;

import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.AgentDecryptionInfo;
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
    public void testEasyBrute() throws Exception {
        MachineHandler machineHandler = new MachineHandlerImpl();
        String path = "C:\\Users\\Eliya\\Documents\\java\\CTE\\CTE_ENIGMA\\enigma-machine\\src\\main\\resources\\machine-inventory-schema-Ex2\\ex2-basic-easy.xml";
        machineHandler.buildMachinePartsInventory(path);

        ReflectorsId refid = ReflectorsId.I;
        List<MappingPair<String,String>> plugBoard = new ArrayList<MappingPair<String,String>>();
        List<Integer> rotorsIds = Arrays.asList(1,2);

        String rotorsstartingPos = String.valueOf(machineHandler.verifyInputInAbcAndFix("AAF").get());

//        plugBoard.add(new MappingPair<String,String>("F","A"));
//        plugBoard.add(new MappingPair<String,String>("B","?"));
//        plugBoard.add(new MappingPair<String,String>("C"," "));

        machineHandler.assembleMachine(refid, rotorsIds,rotorsstartingPos,plugBoard);

        System.out.println("Initial Machine State:" + machineHandler.getMachineState());

//        System.out.println("Initial Machine State:" + machineHandler.getEncryptionMachineClone());

        int numberOfAgents = 10 ;
        int taskSize = 2;

//        String encrypted = machineHandler.encrypt("HELLO WORLD");
        String encrypted = "WMHDRHVBHQA";
        DecryptionManager manager = new DecryptionManagerImpl(machineHandler,  numberOfAgents, DecryptionDifficultyLevel.EASY, taskSize) ;
        manager.bruteForceDecryption(encrypted);

//        List<AgentDecryptionInfo>;
    }

}
