package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lombok.Setter;
import main.java.dto.MachineState;
import src.main.java.service.DataService;
import src.main.java.ui.CLIMenu;

public class DecodedCandidateController {

//    public Label timeLabel;
    public Pane candidatePane;
    public Label agentIdLabel;
    @FXML
    private Label candidateLabel;

    @FXML
    private Label foundByConfigLabel;

    public void setCandidateLabel(String value){
        candidateLabel.setText(value);
    }
//    public void setTimeLabel(String value){
//        timeLabel.setText(value);
//    }

    public void setFoundByAgentIdLabel(String value){
        agentIdLabel.setText(value);
    }
    public void setFoundByConfigLabel(MachineState state){
        String machineState = CLIMenu.getMachineState(state, DataService.getInventoryInfoProperty().get());
        foundByConfigLabel.setText(machineState);
    }
}
