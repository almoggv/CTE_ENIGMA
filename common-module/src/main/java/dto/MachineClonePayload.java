package dto;

import component.EncryptionMachine;
import component.impl.EnigmaMachine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineClonePayload {
    String message;
    MachineState machineStaste;
    EncryptionMachine encryptionMachine;
}
