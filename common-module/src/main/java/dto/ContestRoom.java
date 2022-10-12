package dto;

import enums.DecryptionDifficultyLevel;
import enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContestRoom {
    private String name;
    private String creatorName;
    private GameStatus gameStatus;
//    private DecryptionDifficultyLevel difficultyLevel;
    private String difficultyLevel;
    private Integer currNumOfTeams;
    private Integer requiredNumOfTeams;
}
