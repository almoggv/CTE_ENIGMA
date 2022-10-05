package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDecryptionInfo {
    private UUID agentID;
    private MachineState initialState;
    private String input;
    private String output;
    private long timeToDecrypt;
}
