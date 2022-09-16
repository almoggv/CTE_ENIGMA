package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

public class DecodedCandidateController {

    public Label timeLabel;
    @FXML
    private Label candidateLabel;

    @FXML
    private Label foundByConfigLabel;

    public void setCandidateLabel(String value){
        candidateLabel.setText(value);
    }
    public void setTimeLabel(String value){
        timeLabel.setText(value);
    }

    public void setFoundByConfigLabel(String value){
        foundByConfigLabel.setText(value);
    }
}
