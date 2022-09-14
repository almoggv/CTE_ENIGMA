package src.main.java.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import main.java.component.MachineHandler;
import main.java.dto.InventoryInfo;
import main.java.manager.DecryptionManager;
import org.controlsfx.validation.Validator;
import src.main.java.fxcomponent.ComboBoxCell;
import src.main.java.fxcomponent.ComboBoxItem;
import src.main.java.service.DataService;

import java.net.URL;
import java.util.ResourceBundle;

public class BruteForcePageController implements Initializable {

    MachineHandler machineHandler;
    DecryptionManager decryptionManager;

    private AppController parentController;
    @FXML
    private CurrMachineConfigController currMachineConfigComponentController;

    public GridPane rootGridPane;
    public Slider amountOfAgentsSlider;
    public ComboBox difficultyComboBox;
    public TextField taskSizeTextField;
    public FlowPane dmResultsFlowPane;
    public Button startDecryptButton;

    @FXML
    public GridPane currMachineConfigComponent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
//            @Override
//            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
//                setDMDetails(newValue);
//            }
//        });
//        startDecryptButton.disableProperty().bind();

        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
    }

    private void setDMDetails(InventoryInfo newValue) {
        setDifficultyComboBox();
    }

    private void setDifficultyComboBox() {
//        difficultyComboBox.

    }

    public GridPane getRootComponent() {
        return rootGridPane;
    }

    public void setParentController(AppController appController) {
        parentController = appController;
    }

    public void bindComponent(CurrMachineConfigController currMachineConfigController) {
        this.currMachineConfigComponentController = currMachineConfigController;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
    }

    public void setMachineHandler(MachineHandler machineHandler) {
        this.machineHandler = machineHandler;
    }
}
