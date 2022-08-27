package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class MachinePageController implements Initializable {

    @Setter @Getter
    @FXML private AppController parentController;
    @Setter @Getter
    @FXML private SetMachineConfigController setMachineConfigController;
    @Setter @Getter
    @FXML private CurrMachineConfigController currMachineConfigController;

    @FXML private GridPane rootGridPane;
    @FXML private SplitPane bottomSplitPane;
    @FXML private AnchorPane leftAnchorOfBottom;
    @FXML private ScrollPane scrollOfLeftBottomAnchor;
    @FXML private AnchorPane rightAnchorOfBottom;
    @FXML private ScrollPane scrollOfRightBottomAnchor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(setMachineConfigController != null){
            setMachineConfigController.setParentController(this);
        }
        if(currMachineConfigController != null){
            currMachineConfigController.setParentController(this);
        }
    }
}
