package agentClientDmTest;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import manager.AgentClientDM;
import manager.impl.AgentClientDMImpl;
import org.junit.Test;
import service.MathService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentClientDmRunAndPrintCandidates {

    private static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ !?";
    private static final int NUMBER_OF_ROTORS_IN_USE = 3;
    private static final int MAX_NUM_OF_TASKS = 200;
    private static final int MAX_NUM_OF_THREADS = 3;
    private static final String ALLY_NAME = "boaty";
    private static final String INPUT_TO_DECRYPT = "SKY";


    @Test
    public void findDecryptionCandidatesTest() throws Exception {
        MachineHandler machineHandler = loadAMachineHandler();
        AgentClientDM agentDm = new AgentClientDMImpl(machineHandler,MAX_NUM_OF_TASKS,MAX_NUM_OF_THREADS,ALLY_NAME);
        List<MachineState> workToDo = createWorkToDo(MAX_NUM_OF_TASKS);
        agentDm.assignWork(workToDo,INPUT_TO_DECRYPT);
        //Connect Properties:
        agentDm.getDecryptionCandidatesProperty().addListener((observable, oldValue, newValue) -> {

        });


    }

    private MachineHandler loadAMachineHandler() throws Exception {
        MachineHandler resultHander = new MachineHandlerImpl();
        String schemaFileName = "ex3-basic.xml";
        URL schemaUrl = AgentClientDmRunAndPrintCandidates.class.getResource(schemaFileName);
        resultHander.buildMachinePartsInventory(schemaUrl.getPath());
        resultHander.assembleMachine();
        return resultHander;
    }

    private List<MachineState> createWorkToDo(int amoutOfWork){
        //Creates work for an EASY level
        List<MachineState> workToDo = new ArrayList<>(amoutOfWork);
        MachineState firstState = getInitialState();
        MachineState currState = firstState.getDeepClone();
        for (int i = 0; i < amoutOfWork; i++) {
            workToDo.add(currState.getDeepClone());
            currState.setRotorsHeadsInitialValues(advanceRotorPositions(currState.getRotorsHeadsInitialValues()));
        }
        return workToDo;
    }

    private List<String> advanceRotorPositions(List<String> startingPos){
        int positionAsDecimal = MathService.fromLettersToBase10(startingPos, this.ABC.length(),this.ABC);
        positionAsDecimal += 1;
        List<String> newAdvancedPos = MathService.fromBase10ToBaseN(positionAsDecimal, this.ABC.length(), this.ABC, this.NUMBER_OF_ROTORS_IN_USE);
        return newAdvancedPos;
    }

    private MachineState getInitialState(){
        MachineState initialState = new MachineState();
        initialState.setRotorIds(Arrays.asList(1,2,3));
        initialState.setRotorsHeadsInitialValues(Arrays.asList("A","A","A"));
        initialState.setReflectorId(ReflectorsId.I);
        initialState.setPlugMapping(new ArrayList<MappingPair<String, String>>());
        return initialState;
    }

}
