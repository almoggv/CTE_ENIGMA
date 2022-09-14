package main.java.agent;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.java.dto.AgentDecryptionInfo;
import main.java.dto.MachineState;
import main.java.generictype.MappingPair;

import java.util.List;
import java.util.UUID;

public interface DecryptionAgent extends Runnable {

    void assignWork(List<MachineState> startingConfigurations, String inputToDecrypt);

    UUID getId();

    ObjectProperty<AgentDecryptionInfo> getDecryptionInfoProperty();
    ObjectProperty<List<AgentDecryptionInfo>> getPotentialCandidatesListProperty();
    BooleanProperty getIsFinishedProperty();
    ObjectProperty<MappingPair<Integer,Integer>> getProgressProperty();



}
