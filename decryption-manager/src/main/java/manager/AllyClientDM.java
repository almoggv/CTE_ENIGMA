package manager;

import dto.InventoryInfo;
import dto.MachineState;
import enums.DecryptionDifficultyLevel;
import generictype.MappingPair;
import javafx.beans.property.ObjectProperty;

import java.util.List;
import java.util.Queue;

public interface AllyClientDM extends Runnable{

//    Queue<List<MachineState>> getWorkBatchesQueue();

    /**
     * @return a list of MachineStates or null of no new batch was found
     */
    List<MachineState> getNextBatch();
    List<List<MachineState>> getNextBatch(int numberOfBatches);

    InventoryInfo getInventoryInfo();
    void setInventoryInfo(InventoryInfo inventoryInfo);

    DecryptionDifficultyLevel getDifficultyLevel();
    void setDifficultyLevel(DecryptionDifficultyLevel level);

    int getTaskSize();
    void setTaskSize(int taskSize);

    ObjectProperty<MappingPair<Long,Long>> getProgressProperty();

    /**
     * used to infer, based on the DifficultyLevel, the state of the other machine components for the work distribution
     * @return
     */
    MachineState getInitialMachineConfig();
    /**
     * used to infer, based on the DifficultyLevel, the state of the other machine components for the work distribution
     */
    void setInitialMachineConfig(MachineState initialMachineConfig);

    void kill();


}
