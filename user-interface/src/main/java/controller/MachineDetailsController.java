package src.main.java.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import main.java.dto.InventoryInfo;
import src.main.java.service.DataService;

import java.net.URL;
import java.util.ResourceBundle;

public class MachineDetailsController implements Initializable {


    @Getter @Setter @FXML MachinePageController parentController;

    @FXML private GridPane originalMachineConfigComponent;
    @FXML private CurrMachineConfigController originalMachineConfigComponentController;

    @FXML public AnchorPane originalMachineConfigurationAnchor;
    @FXML private Label titleLabel;
    @FXML private GridPane innerGridPane;
    @FXML private Label reflectorLabel;
    @FXML private Label rotorsLabel;
    @FXML private Label plugboardLabel;
    @FXML private HBox rotorsHbox;
    @FXML private Label availableRotorNumLabel;
    @FXML private Label encryptedMsgLabel;
    @FXML private HBox rotorsHbox1;
    @FXML private Label toUseRotorNumLabel;
    @FXML private Label availableReflectorNumLabel;
    @FXML private GridPane originalMachineConfigurationComponent;
    @FXML private Label machineNotConfiguredLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(originalMachineConfigComponentController!=null){
            originalMachineConfigComponentController.setParentController(this);
            originalMachineConfigComponentController.bindToData(DataService.getOriginalMachineStateProperty());
        }
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
           @Override
           public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
               if(newValue != null){
                   originalMachineConfigurationAnchor.setVisible(true);
                   machineNotConfiguredLabel.setVisible(false);
                   showInventory(newValue);
               }
               else{
                   originalMachineConfigurationAnchor.setVisible(false);
                   machineNotConfiguredLabel.setVisible(true);
               }
           }
        });
    }

    private void showInventory(InventoryInfo inventoryInfo){


    }

    // Use snippets for later
//    private void setDetails() {
//        availableRotorNumLabel.setText((String.valueOf(DataService.getInventoryInfo().getNumOfAvailableRotors())));
////        encryptedMsgLabel.setText(DateService.getInventoryInfo());
//        toUseRotorNumLabel.setText((String.valueOf(DataService.getInventoryInfo().getNumOfRotorsInUse())));
//        availableReflectorNumLabel.setText((String.valueOf(DataService.getInventoryInfo().getNumOfAvailableReflectors())));
//    }
}
