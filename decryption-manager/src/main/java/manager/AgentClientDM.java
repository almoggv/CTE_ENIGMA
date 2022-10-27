package manager;

import adapter.ListenerAdapter;
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

    ListenerAdapter getListenerAdapter();

    ObjectProperty<DecryptionAgent> getNewestAgentProperty();

    String getInputToDecrypt();
    void setInputToDecrypt(String input);

    int getInternalAgentTaskSize();
    void setInternalAgentTaskSize(int taskSizePerAgent);

    int getMaxNumberOfTasks();

    void assignWork(List<MachineState> assignedWork, String inputToDecrypt);

    void kill();
}
