package controller;

import dto.AllyTeamData;
import javafx.scene.control.Label;

public class AllyTeamController {
    public Label teamNameLabel;
    public Label numOfAgentsLabel;
    public Label taskSizeLabel;

    public void setTeamNameLabel(String value) {
        this.teamNameLabel.setText(String.valueOf(value));
    }

    public void setNumOfAgentsLabel(String value) {
        this.numOfAgentsLabel.setText(String.valueOf(value));
    }

    public void setTaskSizeLabel(String value) {
        this.taskSizeLabel.setText(String.valueOf(value));
    }

    public void setData(AllyTeamData ally){
        setTeamNameLabel(ally.getTeamName());
        setNumOfAgentsLabel(String.valueOf(ally.getNumOfAgents()));
        setTaskSizeLabel(String.valueOf(ally.getTaskSize()));
    }
}
