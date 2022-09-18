package main.java.manager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import main.java.agent.DecryptionAgent;
import main.java.common.Pausable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CandidatesListener extends Runnable, Pausable {

    BooleanProperty getIsWorkCompletedProperty();

    void BindToWorkers(ObjectProperty<DecryptionAgent> newestAgentProperty);
    void BindToManager(AgentWorkManager agentWorkManager);
}
