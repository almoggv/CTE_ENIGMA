package manager;

import agent.DecryptionAgent;
import dto.AgentDecryptionInfo;
import dto.MachineState;
import generictype.MappingPair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AgentClientDM extends Runnable {

    BooleanProperty getIsWorkCompletedProperty();
    /**
     * is displayed in the form of : (amountOfWorkCompleted, TotalWorkToDo)
     */
    ObjectProperty<MappingPair<Integer,Integer>> getProgressProperty();
    ObjectProperty<List<AgentDecryptionInfo>> getDecryptionCandidatesProperty();

    Map<UUID, DecryptionAgent> getAgentIdToDecryptAgentMap();
    ObjectProperty<DecryptionAgent> getNewestAgentProperty();

    int getMaxNumberOfTasks();

    void assignWork(List<MachineState> assignedWork, String inputToDecrypt);

    void setInputToDecrypt(String input);
    String getInputToDecrypt();

    void kill();
}
