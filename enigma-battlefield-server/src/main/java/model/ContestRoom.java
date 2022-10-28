package model;

import component.MachineHandler;
import dto.AllyTeamData;
import dto.EncryptionCandidate;
import enums.DecryptionDifficultyLevel;
import enums.GameStatus;
import lombok.Setter;
import lombok.Getter;

import java.util.List;

public class ContestRoom {
    @Getter @Setter private String name;
    @Getter @Setter private String creatorName;
    @Getter @Setter private GameStatus gameStatus;
    @Getter @Setter private DecryptionDifficultyLevel difficultyLevel;
    @Getter @Setter private Integer currNumOfTeams;
    @Getter @Setter private Integer requiredNumOfTeams;
    @Getter @Setter String wordToDecrypt;
    @Getter @Setter private Integer numOfReady;
    @Getter @Setter private boolean isEveryoneReady;
    @Getter @Setter private String winnerName;
    @Getter @Setter private List<Ally> alliesList;
    @Getter @Setter private List<EncryptionCandidate> encryptionCandidateList;
    @Getter @Setter MachineHandler machineHandler;
    @Getter @Setter private Integer numOfGotWinCount;
}
