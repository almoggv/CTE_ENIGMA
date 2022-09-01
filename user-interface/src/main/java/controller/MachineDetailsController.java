package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import src.main.java.service.DataService;

import java.net.URL;
import java.util.ResourceBundle;

public class MachineDetailsController implements Initializable {

    public AnchorPane originalMachineConfigurationAnchor;
    @Getter
    @Setter
    @FXML MachinePageController parentController;
    @FXML
    private Label titleLabel;

    @FXML
    private GridPane innerGridPane;

    @FXML
    private Label reflectorLabel;

    @FXML
    private Label rotorsLabel;

    @FXML
    private Label plugboardLabel;

    @FXML
    private HBox rotorsHbox;

    @FXML
    private Label availableRotorNumLabel;

    @FXML
    private Label encryptedMsgLabel;

    @FXML
    private HBox rotorsHbox1;

    @FXML
    private Label toUseRotorNumLabel;
    @FXML
    private Label availableReflectorNumLabel;
    @FXML
    private GridPane originalMachineConfigurationComponent;
    @FXML
    private Label machineNotConfiguredLabel;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    private void setDetails() {
        availableRotorNumLabel.setText((String.valueOf(DataService.getInventoryInfo().getNumOfAvailableRotors())));
//        encryptedMsgLabel.setText(DateService.getInventoryInfo());
        toUseRotorNumLabel.setText((String.valueOf(DataService.getInventoryInfo().getNumOfRotorsInUse())));
        availableReflectorNumLabel.setText((String.valueOf(DataService.getInventoryInfo().getNumOfAvailableReflectors())));
    }

    public void displayOriginalConfig() {
        if(DataService.getIsMachineInventoryConfigured()){
            setDetails();
        }
        if(DataService.getIsOriginalMachineStateConfigured()){
            originalMachineConfigurationAnchor.setVisible(true);
            machineNotConfiguredLabel.setVisible(false);
        }
        else{
            originalMachineConfigurationAnchor.setVisible(false);
            machineNotConfiguredLabel.setVisible(true);
        }
    }
}
