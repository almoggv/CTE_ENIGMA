package agentClientDmTest;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import service.MathService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentDmTestUtils {

    public static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ !?";
    public static final int NUMBER_OF_ROTORS_IN_USE = 3;
    public static final int MAX_NUM_OF_TASKS = 200;
    public static final int MAX_NUM_OF_THREADS = 3;
    public static final String ALLY_NAME = "boaty";
    public static final String INPUT_TO_DECRYPT = "SKY";

    public static MachineHandler loadAMachineHandler() throws Exception {
        MachineHandler resultHander = new MachineHandlerImpl();
        File resourcesDirectory = new File("src/test/resources");
        String schemaFileName = resourcesDirectory.getAbsolutePath() + "\\ex3-basic.xml";
        resultHander.buildMachinePartsInventory(schemaFileName);
        resultHander.assembleMachine();
        return resultHander;
    }

    public static List<MachineState> createWorkToDo(int amoutOfWork, MachineState initialState){
        //Creates work for an EASY level
        List<MachineState> workToDo = new ArrayList<>(amoutOfWork);
        MachineState currState = initialState.getDeepClone();
        for (int i = 0; i < amoutOfWork; i++) {
            workToDo.add(currState.getDeepClone());
            currState.setRotorsHeadsInitialValues(advanceRotorPositions(currState.getRotorsHeadsInitialValues()));
        }
        return workToDo;
    }

    public static List<String> advanceRotorPositions(List<String> startingPos){
        int positionAsDecimal = MathService.fromLettersToBase10(startingPos, AgentDmTestUtils.ABC.length(),AgentDmTestUtils.ABC);
        positionAsDecimal += 1;
        List<String> newAdvancedPos = MathService.fromBase10ToBaseN(positionAsDecimal, AgentDmTestUtils.ABC.length(), AgentDmTestUtils.ABC, AgentDmTestUtils.NUMBER_OF_ROTORS_IN_USE);
        return newAdvancedPos;
    }

    public static MachineState getInitialState(){
        MachineState initialState = new MachineState();
        initialState.setRotorIds(Arrays.asList(1,2,3));
        initialState.setRotorsHeadsInitialValues(Arrays.asList("A","A","A"));
        initialState.setReflectorId(ReflectorsId.I);
        initialState.setPlugMapping(new ArrayList<MappingPair<String, String>>());
        initialState.setNotchDistancesFromHead(new ArrayList<>());
        return initialState;
    }


}
