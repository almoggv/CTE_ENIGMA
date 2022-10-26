package agentClientDmTest;

import component.MachineHandler;
import component.impl.MachineHandlerImpl;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import javafx.application.Platform;
import manager.AgentClientDM;
import manager.impl.AgentClientDMImpl;
import org.junit.Test;
import service.MathService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentClientDmRunAndPrintCandidates {


    @Test
    public void findDecryptionCandidatesTest() throws Exception {
        MachineHandler machineHandler = AgentDmTestUtils.loadAMachineHandler();
        AgentClientDM agentDm = new AgentClientDMImpl(machineHandler,AgentDmTestUtils.MAX_NUM_OF_TASKS,AgentDmTestUtils.MAX_NUM_OF_THREADS,AgentDmTestUtils.ALLY_NAME);
        List<MachineState> workToDo = AgentDmTestUtils.createWorkToDo(AgentDmTestUtils.MAX_NUM_OF_TASKS, machineHandler.getMachineState().get());
        //Connect Properties To Print:
        addListeners(agentDm);
        //Start Running
        Thread agentDmThread = new Thread(agentDm);
        agentDmThread.start();
        agentDm.assignWork(workToDo,AgentDmTestUtils.INPUT_TO_DECRYPT);
        agentDmThread.join();
    }


    /**
     * Connects the agentDm's candidates and progress properties to System.out
     * and each newAgent's candidates and progress properties
     */
    private void addListeners(AgentClientDM agentDm){
        agentDm.getDecryptionCandidatesProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println("AgentDM property: "+ System.lineSeparator() +"Decryption Candidates Property newValue=" + newValue);
                }
            });
        });
        agentDm.getProgressProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if ((newValue.getLeft() - oldValue.getLeft()) % 10 == 0){
                        System.out.println("AgentDM property: " + System.lineSeparator() + "Progress newValue=" + newValue);
                    }
                }
            });
        });
        agentDm.getNewestAgentProperty().addListener((observable, oldAgent, newAgent) -> {
            if(newAgent==null){
                return;
            }
            newAgent.getProgressProperty().addListener((observable1, oldProgressValue, newProgressValue) -> {
                if(newProgressValue.getLeft().equals(newProgressValue.getRight())){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("[ID="+ newAgent.getId() +"] - DecryptionAgent - Progress = 100%");
                        }
                    });
                }
            });
            newAgent.getPotentialCandidatesListProperty().addListener((observable1, oldPotentialCandidates, newPotentialCandidates) -> {
                if(newPotentialCandidates == null){
                    return;
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("[ID="+ newAgent.getId() +"] - DecryptionAgent - DecryptionCadidatesList newValue=" + newPotentialCandidates);
                    }
                });
            });

        });
    }

}
