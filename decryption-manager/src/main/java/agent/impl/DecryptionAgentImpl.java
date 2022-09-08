package main.java.agent.impl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import main.java.agent.DecryptionAgent;
import main.java.component.EncryptionMachine;
import main.java.dto.MachineState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DecryptionAgentImpl implements DecryptionAgent {

    private final EncryptionMachine encryptionMachine;
    private List<MachineState> startingConfigurations;
    private String inputToDecrypt;
    @Getter private final List<String> potentialDecryptionCandidates = new ArrayList<>();
    @Getter private final StringProperty decryptedString = new SimpleStringProperty();
    @Getter private final BooleanProperty isFinishedProperty = new SimpleBooleanProperty();

    public DecryptionAgentImpl(EncryptionMachine encryptionMachine) {
        this.encryptionMachine = encryptionMachine;
        this.isFinishedProperty.setValue(true);
    }

    public void assignWork(List<MachineState> startingConfigurations, String inputToDecrypt){
        if(startingConfigurations != null){
            startingConfigurations.clear();
        }
        potentialDecryptionCandidates.clear();
        decryptedString.setValue(null);
        this.startingConfigurations = startingConfigurations;
        this.inputToDecrypt = inputToDecrypt;
    }

    @Override
    public void run() {
        Optional<String> decryptionCandidate;
        if(startingConfigurations == null){
            throw new RuntimeException("MachineStates given to agent is null");
        }
        for (MachineState initialState : startingConfigurations ) {
            decryptionCandidate = runSingleDecryption(initialState,this.inputToDecrypt);
            if(decryptionCandidate.isPresent()){
                decryptedString.setValue(decryptionCandidate.get());
                potentialDecryptionCandidates.add(decryptionCandidate.get());
            }
        }
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
        return false;
    }


}
