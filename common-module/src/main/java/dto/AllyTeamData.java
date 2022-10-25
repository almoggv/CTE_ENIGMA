package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import manager.AllyClientDM;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllyTeamData {
    private String teamName;
    private Integer taskSize = 0;
    private List<AgentData> agentsList;
    private Integer numOfAgents;
    private AllyClientDM decryptionManager;

}
