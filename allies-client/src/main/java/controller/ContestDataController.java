package controller;

import dto.ContestRoomData;
import enums.GameStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class ContestDataController {
    public GridPane contestGrid;
    @FXML
    private Label battlefieldNameLabel;
    @FXML private Label uboatCreatorName;
    @FXML private Label gameStatusLabel;
    @FXML private Label difficultyLevelLabel;
    @FXML private Label allyTeamsLabel;

    DashboardPageController dashboardPageController;

    ContestPageController contestPageController;

    public void setBattlefieldNameLabel(String value) {
        this.battlefieldNameLabel.setText(String.valueOf(value));
    }

    public void setAllyTeamsLabel(Integer currNumOfTeams, Integer requiredNumOfTeams ) {
        String allyTeams = currNumOfTeams + "/" + requiredNumOfTeams;
        this.allyTeamsLabel.setText(allyTeams);
    }

    public void setDifficultyLevelLabel(String value) {
        this.difficultyLevelLabel.setText(String.valueOf(value));
    }

    public void setGameStatusLabel(GameStatus value) {
        this.gameStatusLabel.setText(String.valueOf(value));
    }

    public void setUboatCreatorName(String value) {
        this.uboatCreatorName.setText(String.valueOf(value));
    }

//

    public void setParentController(DashboardPageController dashboardPageController) {
        this.dashboardPageController = dashboardPageController;
    }
    public void setParentController(ContestPageController contestPageController) {
        this.contestPageController = contestPageController;
    }

    public void setData(ContestRoomData contestRoom){
        setAllyTeamsLabel(contestRoom.getCurrNumOfTeams(), contestRoom.getRequiredNumOfTeams());
        setBattlefieldNameLabel(contestRoom.getName());
        setDifficultyLevelLabel(contestRoom.getDifficultyLevel().name());
        setUboatCreatorName(contestRoom.getCreatorName());
        setGameStatusLabel(contestRoom.getGameStatus());
    }

}
