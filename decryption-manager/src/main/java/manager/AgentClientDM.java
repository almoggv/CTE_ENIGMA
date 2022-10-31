package manager;

import adapter.ListenerAdapter;
import agent.DecryptionAgent;
import agent.DecryptionWorker;
import component.MachineHandler;
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

    ListenerAdapter getListenerAdapter();

    ObjectProperty<DecryptionWorker> getNewestAgentProperty();

    String getInputToDecrypt();
    void setInputToDecrypt(String input);

    int getInternalAgentTaskSize();
    void setInternalAgentTaskSize(int taskSizePerAgent);

    int getMaxNumberOfTasks();

    BooleanProperty getIsReadyForMoreWorkProperty();

    MachineHandler getMachineHandler();

    void assignWork(List<MachineState> assignedWork, String inputToDecrypt);

    void kill();
}
