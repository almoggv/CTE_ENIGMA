package src.main.java.service;


import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;

import java.util.List;
import java.util.Map;


public class DataService {

    @Getter private static final SimpleObjectProperty<InventoryInfo> inventoryInfoProperty = new SimpleObjectProperty<>();
    @Getter private static final SimpleObjectProperty<MachineState> originalMachineStateProperty = new SimpleObjectProperty<>();
    @Getter private static final SimpleObjectProperty<MachineState> currentMachineStateProperty = new SimpleObjectProperty<>();
//    @Getter private static final SimpleObjectProperty<EncryptionInfoHistory> encryptionInfoHistory = new SimpleObjectProperty<>();
    @Getter private static final SimpleObjectProperty<Map<MachineState, List<EncryptionInfoHistory>>> encryptionInfoHistoryProperty = new SimpleObjectProperty<>();



//    @Getter @Setter private static InventoryInfo inventoryInfo;
//    @Getter @Setter private static EncryptionInfoHistory encryptionInfoHistory;
//    @Getter @Setter private static MachineState originalMachineState;
//    @Getter @Setter private static MachineState currentMachineState;
//    @Getter @Setter private static Boolean isCurrentMachineStateConfigured = new Boolean(false);
//    @Getter @Setter private static Boolean isOriginalMachineStateConfigured = new Boolean(false);
//    @Getter @Setter private static Boolean isMachineInventoryConfigured = new Boolean(false);
}
