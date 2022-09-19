package main.java.agent;

import javafx.beans.property.*;
import main.java.common.Pausable;
import main.java.dto.AgentDecryptionInfo;
import main.java.dto.MachineState;
import main.java.generictype.MappingPair;

import java.util.List;
import java.util.UUID;

public interface DecryptionAgent extends Runnable, Pausable {

    void assignWork(List<MachineState> startingConfigurations, String inputToDecrypt);

    UUID getId();

    /**
     * Will Trigger after each found candidate
     */
    ObjectProperty<AgentDecryptionInfo> getDecryptionInfoProperty();
    /**
     * Will Trigger only onFinish, if any found
     */
    ObjectProperty<List<AgentDecryptionInfo>> getPotentialCandidatesListProperty();
    BooleanProperty getIsFinishedProperty();

    /**
     * the progress is in the form of a pair = (workCompletedSoFar,TotalWorkToDo)
     */
    ObjectProperty<MappingPair<Integer,Integer>> getProgressProperty();
    long getTimeTookToCompleteWork();


}
