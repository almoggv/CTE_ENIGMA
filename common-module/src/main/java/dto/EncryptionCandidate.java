package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionCandidate {
    private String candidate;
    private String allyTeamName;
    private MachineState foundByState;
}
