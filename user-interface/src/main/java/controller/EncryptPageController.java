package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class EncryptPageController implements Initializable {

    @Getter @Setter private MachineHandler machineHandler;

    @Getter @Setter @FXML AppController parentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;

    @FXML public GridPane currMachineConfigComponent;
    @FXML private GridPane rootGrid;
    @FXML private ScrollPane currMachineConfigWrapperPane;
    @FXML private TextField testTextArea;
    @FXML private FlowPane keyboardFlowPane;

    public Parent getRootComponent(){
        return rootGrid;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
        }
    }

    /**
     * binds the root component of the controller to the designated pane.
     */
    public void bindComponent(CurrMachineConfigController currMachineConfigComponentController) {
        this.currMachineConfigComponentController = currMachineConfigComponentController;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
        currMachineConfigWrapperPane.setContent(currMachineConfigComponent);
    }
}
