package controller;

import dto.EncryptionCandidate;
import dto.MachineState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class CandidateController {
    public Pane candidatePane;
    public Label allyTeamLabel;
    @FXML
    private Label candidateLabel;

    @FXML
    private Label foundByConfigLabel;

    public void setCandidateLabel(String value){
        candidateLabel.setText(value);
    }

    public void setAllyTeamLabel(String value){
        allyTeamLabel.setText(value);
    }
    public void setFoundByConfigLabel(MachineState state){
        //todo:
//        String machineState = CLIMenu.getMachineState(state, DataService.getInventoryInfoProperty().get());
        foundByConfigLabel.setText(state.toString());
    }

    public void setData(EncryptionCandidate candidate){
        setCandidateLabel(candidate.getCandidate());
        setAllyTeamLabel(candidate.getAllyTeamName());
//        setFoundByConfigLabel(candidate.getFoundByState());
    }
}
