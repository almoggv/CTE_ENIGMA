package dto;

import enums.DecryptionDifficultyLevel;
import enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContestRoom {
    private String name;
    private String creatorName;
    private GameStatus gameStatus;
    //todo - fix
//    private DecryptionDifficultyLevel difficultyLevel;
    private String difficultyLevel;
    private Integer currNumOfTeams;
    private Integer requiredNumOfTeams;

    private List<User> alliesList;
    String wordToDecrypt;
}
