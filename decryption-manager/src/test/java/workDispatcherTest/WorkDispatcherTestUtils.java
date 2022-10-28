package workDispatcherTest;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkDispatcherTestUtils {

    public static final String SCHEMA_FILE = "\\ex3-basic.xml";
    public static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ !?'";
    public static final int NUMBER_OF_ROTORS_IN_USE = 3;
    public static final int REQUESTED_EASY_BATCH_SIZE = 200;
    public static final int REQUESTED_MEDIUM_BATCH_SIZE = 2000;
    public static final int REQUESTED_HARD_BATCH_SIZE = 4000;
    public static final int REQUESTED_IMPOSSIBLE_BATCH_SIZE = 10000;



    public static MachineHandler loadAMachineHandlerRandomly() throws Exception {
        MachineHandler resultHander = new MachineHandlerImpl();
        File resourcesDirectory = new File("src/test/resources");
        String schemaFileName = resourcesDirectory.getAbsolutePath() + SCHEMA_FILE;
        resultHander.buildMachinePartsInventory(schemaFileName);
        resultHander.assembleMachine();
        return resultHander;
    }

    public static MachineHandler loadAMachineHandlerManually(MachineState initialState) throws Exception {
        MachineHandler resultHander = new MachineHandlerImpl();
        File resourcesDirectory = new File("src/test/resources");
        String schemaFileName = resourcesDirectory.getAbsolutePath() + SCHEMA_FILE;
        resultHander.buildMachinePartsInventory(schemaFileName);
        resultHander.assembleMachine(initialState);
        return resultHander;
    }

    /**
     * @return a State that uses rotors: (1,2,3) -> with positions (A,B,C) , Empty PlugBoard , Reflector: I
     */
    public static MachineState getInitialState(){
        MachineState initialState = new MachineState();
        initialState.setRotorIds(Arrays.asList(1,2,3));
        initialState.setRotorsHeadsInitialValues(Arrays.asList("A","B","C"));
        initialState.setReflectorId(ReflectorsId.I);
        initialState.setPlugMapping(new ArrayList<MappingPair<String, String>>());
        initialState.setNotchDistancesFromHead(new ArrayList<>());
        return initialState;
    }

    /**
     * @return a State that uses rotors: (1,2,3) -> with positions (A,A,A) , Empty PlugBoard , Reflector: I
     */
    public static MachineState getStartingState(){
        MachineState initialState = new MachineState();
        initialState.setRotorIds(Arrays.asList(1,2,3));
        initialState.setRotorsHeadsInitialValues(Arrays.asList("A","A","A"));
        initialState.setReflectorId(ReflectorsId.I);
        initialState.setPlugMapping(new ArrayList<MappingPair<String, String>>());
        initialState.setNotchDistancesFromHead(new ArrayList<>());
        return initialState;
    }

    /**
     * @return a State that uses rotors: (1,2,3) -> with positions (',?,A) , Empty PlugBoard , Reflector: I
     */
    public static MachineState getProgressedState(){
        MachineState initialState = new MachineState();
        initialState.setRotorIds(Arrays.asList(1,2,3));
        initialState.setRotorsHeadsInitialValues(Arrays.asList("'","?","A"));
        initialState.setReflectorId(ReflectorsId.I);
        initialState.setPlugMapping(new ArrayList<MappingPair<String, String>>());
        initialState.setNotchDistancesFromHead(new ArrayList<>());
        return initialState;
    }
}
