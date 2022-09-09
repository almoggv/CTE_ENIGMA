package main.java.agent.impl;

import javafx.beans.property.*;
import javafx.util.Pair;
import lombok.Getter;
import main.java.agent.DecryptionAgent;
import main.java.component.EncryptionMachine;
import main.java.dto.AgentDecryptionInfo;
import main.java.dto.MachineState;
import main.java.generictype.MappingPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DecryptionAgentImpl implements DecryptionAgent {

    @Getter UUID id = UUID.randomUUID();
    private final EncryptionMachine encryptionMachine;
    private List<MachineState> startingConfigurations;
    private String inputToDecrypt;
    /**
     * Is updated through out the run, each decryption that yields a potentail candidate, the property is updated
     */
    @Getter private final ObjectProperty<AgentDecryptionInfo> decryptionInfoProperty = new SimpleObjectProperty<>();
    @Getter private final ObjectProperty<MappingPair<Integer,Integer>> progressProperty = new SimpleObjectProperty<>();
    @Getter private final BooleanProperty isFinishedProperty = new SimpleBooleanProperty();
    /**
     * Is updated once ALL the work is done. if no potential candidates were found, wont update.
     */
    @Getter private final ObjectProperty<List<AgentDecryptionInfo>> potentialCandidatesListProperty = new SimpleObjectProperty<>();

    public DecryptionAgentImpl(EncryptionMachine encryptionMachine) {
        this.encryptionMachine = encryptionMachine;
        this.isFinishedProperty.setValue(true);
    }

    public void assignWork(List<MachineState> startingConfigurations, String inputToDecrypt){
        if(startingConfigurations != null){
            startingConfigurations.clear();
        }
        this.startingConfigurations = startingConfigurations;
        this.inputToDecrypt = inputToDecrypt;
        potentialCandidatesListProperty.setValue(null);
        decryptionInfoProperty.setValue(null);
        progressProperty.setValue(new MappingPair<Integer,Integer>(0,startingConfigurations.size()));
        isFinishedProperty.setValue(false);
    }

    @Override
    public void run() {
        List<AgentDecryptionInfo> potentialCandidates = new ArrayList<>();
        Optional<String> decryptionCandidate;
        int index = 1;
        int PROGRESS_UPDATE_INTERVAL = (int)((0.1)*(startingConfigurations.size()));
        if(startingConfigurations == null){
            throw new RuntimeException("MachineStates given to agent is null");
        }
        for (MachineState initialState : startingConfigurations ) {
            long startEncryptionTime = System.nanoTime();
            decryptionCandidate = runSingleDecryption(initialState,this.inputToDecrypt);
            long endEncryptionTime = System.nanoTime();
            long encryptionTime = endEncryptionTime - startEncryptionTime;
            if(decryptionCandidate.isPresent()){
                AgentDecryptionInfo decryptionInfo = new AgentDecryptionInfo(this.id,initialState,inputToDecrypt,decryptionCandidate.get(),encryptionTime);
                decryptionInfoProperty.setValue(decryptionInfo);
                potentialCandidates.add(decryptionInfo);
            }
            //TODO: update progress
            index++;
            if(index % PROGRESS_UPDATE_INTERVAL == 0){
                progressProperty.setValue(new MappingPair<>(index,startingConfigurations.size()));
            }
        }
        if(potentialCandidates.size() > 0){
            potentialCandidatesListProperty.setValue(potentialCandidates);
        }
        isFinishedProperty.setValue(true);
    }

    private Optional<String> runSingleDecryption(MachineState machineInitialState, String inputToDecrypt){
        encryptionMachine.setMachineState(machineInitialState);
        String decryptionResult;

        decryptionResult = encryptionMachine.decrypt(inputToDecrypt);
        if(checkIfInDictionary(decryptionResult)){
            return Optional.of(decryptionResult);
        }
        return Optional.empty();
    }

    private boolean checkIfInDictionary(String decryptionResult) {
        //TODO: implement
        return false;
    }


}
