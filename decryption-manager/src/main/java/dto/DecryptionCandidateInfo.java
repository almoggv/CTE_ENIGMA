package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionCandidateInfo {
    String input;
    String output;
    UUID responsibleAgentId;
    MachineState initialState;

}
