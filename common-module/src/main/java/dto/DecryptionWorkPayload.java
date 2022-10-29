package dto;

import enums.DecryptionDifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionWorkPayload {
    String message;
    MachineState firstState;
    MachineState lastState;
    int amountOfStates;
    String inputToDecrypt;
    DecryptionDifficultyLevel difficultyLevel;
}
