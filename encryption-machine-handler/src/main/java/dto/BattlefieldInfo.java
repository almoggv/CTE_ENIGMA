package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattlefieldInfo {
    private String battlefieldName;
    private Integer requiredNumOfTeams;
    private String difficultyLevel;
}
