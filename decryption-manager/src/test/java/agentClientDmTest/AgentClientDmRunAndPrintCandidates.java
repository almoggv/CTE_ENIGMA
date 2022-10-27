package agentClientDmTest;

import component.EncryptionMachine;
import component.MachineHandler;
import component.impl.EnigmaMachine;
import dto.MachineState;
import manager.AgentClientDM;
import manager.impl.AgentClientDMImpl;
import org.junit.Test;

import java.util.List;

public class AgentClientDmRunAndPrintCandidates {

    static int finishedAgentsCounter = 0;

    @Test
    public void findDecryptionCandidatesTest() throws Exception {
        MachineHandler machineHandler = AgentDmTestUtils.loadAMachineHandler();
        AgentClientDM agentDm = new AgentClientDMImpl(machineHandler,AgentDmTestUtils.MAX_NUM_OF_TASKS,AgentDmTestUtils.MAX_NUM_OF_THREADS,AgentDmTestUtils.ALLY_NAME);
        List<MachineState> workToDo = AgentDmTestUtils.createWorkToDo(AgentDmTestUtils.MAX_NUM_OF_TASKS, machineHandler.getMachineState().get());
        //Connect Properties To Print:
        addListeners(agentDm);
        //Set Decryption input
        EncryptionMachine machineCloneForEncryption = machineHandler.getEncryptionMachineClone();
        String inputToDecrypt = machineCloneForEncryption.encrypt(AgentDmTestUtils.INPUT_TO_ENCRYPT);
        //Start Running
        Thread agentDmThread = new Thread(agentDm);
        agentDmThread.start();
        agentDm.assignWork(workToDo,inputToDecrypt);
        agentDmThread.join();
    }

    /**
     * Connects the agentDm's candidates and progress properties to System.out
     * and each newAgent's candidates and progress properties
     */
    private void addListeners(AgentClientDM agentDm){
        agentDm.getListenerAdapter().getDecryptionCandidatesProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("AgentDM - ListenerAdapter's property: "+ System.lineSeparator() +"Decryption Candidates Property newValue=" + newValue);
        });
        agentDm.getListenerAdapter().getProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || oldValue == null){
                return;
            }
            if(newValue.getLeft() == null || oldValue.getLeft()==null){
                return;
            }
            if ((newValue.getLeft() - oldValue.getLeft()) % 10 == 0){
                System.out.println("AgentDM - ListenerAdapter's property: " + System.lineSeparator() + "Progress newValue=" + newValue);
            }
        });

//        agentDm.getNewestAgentProperty().addListener((observable, oldAgent, newAgent) -> {
//            if(newAgent==null){
//                return;
//            }
//            newAgent.getProgressProperty().addListener((observable1, oldProgressValue, newProgressValue) -> {
//                if(newProgressValue.getLeft().equals(newProgressValue.getRight())){
//                    System.out.println("ID=["+ newAgent.getId() +"] - DecryptionAgent number="+ finishedAgentsCounter +"- Progress = 100%");
//                    finishedAgentsCounter++;
//                }
//            });
//            newAgent.getPotentialCandidatesListProperty().addListener((observable1, oldPotentialCandidates, newPotentialCandidates) -> {
//                if(newPotentialCandidates == null){
//                    return;
//                }
//                System.out.println("[ID="+ newAgent.getId() +"] - DecryptionAgent - DecryptionCadidatesList newValue=" + newPotentialCandidates);
//            });
//        });
    }

}
