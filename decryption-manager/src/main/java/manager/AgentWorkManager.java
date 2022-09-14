package main.java.manager;

import javafx.beans.property.*;
import lombok.Getter;
import main.java.agent.DecryptionAgent;
import main.java.dto.AgentDecryptionInfo;

import java.util.List;

public interface AgentWorkManager extends Runnable{

    BooleanProperty getIsWorkCompletedProperty();
    BooleanProperty getIsAllWorkAssignedProperty();
    ObjectProperty<List<AgentDecryptionInfo>> getDecryptionCandidatesProperty();

    List<DecryptionAgent> getDecryptionAgentsList();
    IntegerProperty getNumberOfAgentsProperty();


}
