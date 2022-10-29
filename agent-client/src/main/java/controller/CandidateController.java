package controller;

import dto.EncryptionCandidate;
import dto.MachineState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import manager.AgentClientDM;

public class CandidateController {
    @FXML private Pane candidatePane;
    @FXML private Label allyTeamLabel;
    @FXML private Label candidateLabel;
    @FXML private Label foundByConfigLabel;

    public void setCandidateLabel(String value){
        candidateLabel.setText(value);
    }

    public void setAllyTeamLabel(String value){
        allyTeamLabel.setText(value);
    }

    public void setFoundByConfigLabel(MachineState state){
        foundByConfigLabel.setText(state.toString());
    }

    public void setData(EncryptionCandidate candidate){
        setCandidateLabel(candidate.getCandidate());
        setAllyTeamLabel(candidate.getAllyTeamName());
        setFoundByConfigLabel(candidate.getFoundByState());
    }
}
