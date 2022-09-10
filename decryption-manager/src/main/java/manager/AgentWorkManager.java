package main.java.manager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import main.java.dto.AgentDecryptionInfo;

import java.util.List;

public interface AgentWorkManager extends Runnable{

    BooleanProperty getIsWorkCompletedProperty();
    BooleanProperty getIsAllWorkAssignedProperty();
    ObjectProperty<List<AgentDecryptionInfo>> getDecryptionCandidatesProperty();
}
