package agent;

import dto.DecryptionCandidateInfo;
import dto.MachineState;
import generictype.MappingPair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;

import java.util.List;
import java.util.UUID;

public interface DecryptionWorker extends Runnable{

    UUID getId();
    ObjectProperty<DecryptionCandidateInfo> getLastFoundCandidateProperty();
    ObjectProperty<List<DecryptionCandidateInfo>> getAllFoundDecryptionCandidatesProperty();
    ObjectProperty<MappingPair<Integer,Integer>> getProgressProperty();
    BooleanProperty getIsWorkerFinishedWorkProperty();

    void assignWork(List<MachineState> workToDo, String inputToDecrypt);

}
