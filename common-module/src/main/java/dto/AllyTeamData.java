package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllyTeamData {
    private String teamName;
    private Integer numOfAgents;
    private Integer taskSize;
}
