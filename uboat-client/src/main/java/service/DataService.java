package service;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import dto.EncryptionInfoHistory;
import dto.InventoryInfo;
import dto.MachineState;

import java.util.List;
import java.util.Map;


public class DataService {
    @Getter private static final SimpleObjectProperty<InventoryInfo> inventoryInfoProperty = new SimpleObjectProperty<>();
    @Getter private static final SimpleObjectProperty<MachineState> originalMachineStateProperty = new SimpleObjectProperty<>();
    @Getter private static final SimpleObjectProperty<MachineState> currentMachineStateProperty = new SimpleObjectProperty<>();

}
