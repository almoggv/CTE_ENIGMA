package main.java.manager;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import main.java.agent.DecryptionAgent;
import main.java.common.Pausable;
import main.java.dto.AgentDecryptionInfo;
import main.java.enums.DecryptionDifficultyLevel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AgentWorkManager extends Runnable, Pausable {

    BooleanProperty getIsWorkCompletedProperty();
    BooleanProperty getIsAllWorkAssignedProperty();
    ObjectProperty<List<AgentDecryptionInfo>> getDecryptionCandidatesProperty();

    Map<UUID,DecryptionAgent> getAgentIdToDecryptAgentMap();
    IntegerProperty getNumberOfAgentsProperty();
    ObjectProperty<DecryptionAgent> getNewestAgentProperty();
}
