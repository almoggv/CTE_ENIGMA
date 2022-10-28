package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionWorkPayload {

    String serverMessage;
    MachineState firstState;
    MachineState lastState;
    int amountOfStates;
    String inputToDecrypt;
}
