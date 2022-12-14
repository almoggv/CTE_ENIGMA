package dto;

import component.MachineHandler;
import enums.DecryptionDifficultyLevel;
import enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestRoomData {
    private String name;
    private String creatorName;
    private GameStatus gameStatus;
    private DecryptionDifficultyLevel difficultyLevel;
    private Integer currNumOfTeams;
    private Integer requiredNumOfTeams;
    private String wordToDecrypt;
    private String winnerName;
}
