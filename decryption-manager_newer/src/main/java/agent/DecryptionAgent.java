package agent;

import javafx.beans.property.*;
import common.Pausable;
import dto.AgentDecryptionInfo;
import dto.MachineState;
import generictype.MappingPair;

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
