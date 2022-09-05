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
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import src.main.java.service.DataService;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MachineDetailsController implements Initializable {


    @Getter @Setter @FXML MachinePageController parentController;

    @FXML private GridPane originalMachineConfigComponent;


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
    @FXML private CurrMachineConfigController originalMachineConfigurationComponentController;
    @FXML private Label machineNotConfiguredLabel;

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

        DataService.getEncryptionInfoHistoryProperty().addListener(new ChangeListener<Map<MachineState, List<EncryptionInfoHistory>>>() {
            @Override
            public void changed(ObservableValue<? extends Map<MachineState, List<EncryptionInfoHistory>>> observable, Map<MachineState, List<EncryptionInfoHistory>> oldValue, Map<MachineState, List<EncryptionInfoHistory>> newValue) {
                if(newValue != null){
                    encryptedMsgLabel.setText(String.valueOf(countNumberOfMessagesInHistory()));
                }
            }
        });
    }

    private void showInventory(InventoryInfo inventoryInfo){
        availableRotorNumLabel.setText((String.valueOf(inventoryInfo.getNumOfAvailableRotors())));
        availableReflectorNumLabel.setText((String.valueOf(inventoryInfo.getNumOfAvailableReflectors())));
        toUseRotorNumLabel.setText((String.valueOf(inventoryInfo.getNumOfRotorsInUse())));
    }

    private int countNumberOfMessagesInHistory() {
        if(DataService.getEncryptionInfoHistoryProperty().getValue() == null){
            return 0;
        }
        int resultCount = 0;
        for (MachineState key : DataService.getEncryptionInfoHistoryProperty().get().keySet()) {
            resultCount += DataService.getEncryptionInfoHistoryProperty().get().get(key).size();
        }
        return resultCount;
    }

}
