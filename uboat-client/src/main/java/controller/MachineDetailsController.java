package controller;

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
import dto.EncryptionInfoHistory;
import dto.InventoryInfo;
import dto.MachineState;
import service.DataService;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MachineDetailsController implements Initializable {

    @Getter @Setter @FXML MachinePageController parentController;
    @FXML CurrMachineConfigController originalMachineConfigurationComponentController;

    @FXML GridPane rootGridPane;
    @FXML GridPane originalMachineConfigComponent;
    @FXML AnchorPane originalMachineConfigurationAnchor;
    @FXML Label titleLabel;
    @FXML GridPane innerGridPane;
    @FXML Label reflectorLabel;
    @FXML Label rotorsLabel;
    @FXML Label plugboardLabel;
    @FXML HBox rotorsHbox;
    @FXML Label availableRotorNumLabel;
    @FXML HBox rotorsHbox1;
    @FXML Label toUseRotorNumLabel;
    @FXML Label availableReflectorNumLabel;
    @FXML GridPane originalMachineConfigurationComponent;
    @FXML Label machineNotConfiguredLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(originalMachineConfigurationComponentController!=null){
            originalMachineConfigurationComponentController.setParentController(this);
            originalMachineConfigurationComponentController.changeHeaderToOriginal();
            originalMachineConfigurationComponentController.bindToData(DataService.getOriginalMachineStateProperty());
        }
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
            @Override
            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
                if(newValue != null){
                    showInventory(newValue);
                }
            }
        });
    }

    private void showInventory(InventoryInfo inventoryInfo){
        availableRotorNumLabel.setText((String.valueOf(inventoryInfo.getNumOfAvailableRotors())));
        availableReflectorNumLabel.setText((String.valueOf(inventoryInfo.getNumOfAvailableReflectors())));
        toUseRotorNumLabel.setText((String.valueOf(inventoryInfo.getNumOfRotorsInUse())));
    }

    private void fetchInventoryInfo(){

    }
}
