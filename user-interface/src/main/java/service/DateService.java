package src.main.java.service;

import lombok.Getter;
import lombok.Setter;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;

public class DateService {
    @Setter @Getter
    private static InventoryInfo inventoryInfo;

    @Setter @Getter
    private static EncryptionInfoHistory encryptionInfoHistory;

    @Setter @Getter
    private static MachineState originalMachineState;

    @Setter @Getter
    private static MachineState currentMachineState;

}
