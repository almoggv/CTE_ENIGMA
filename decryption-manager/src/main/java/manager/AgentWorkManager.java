package main.java.manager;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import main.java.agent.DecryptionAgent;
import main.java.common.Pausable;
import main.java.dto.AgentDecryptionInfo;
import main.java.enums.DecryptionDifficultyLevel;

import java.util.List;

public interface AgentWorkManager extends Runnable, Pausable {

    BooleanProperty getIsWorkCompletedProperty();
    BooleanProperty getIsAllWorkAssignedProperty();
    ObjectProperty<List<AgentDecryptionInfo>> getDecryptionCandidatesProperty();

    List<DecryptionAgent> getDecryptionAgentsList();
    IntegerProperty getNumberOfAgentsProperty();
}
