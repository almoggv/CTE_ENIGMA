package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import src.main.java.service.DateService;

import java.net.URL;
import java.util.ResourceBundle;

public class MachineDetailsController implements Initializable {

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
        setDetails();
        if(DateService.getIsOriginalMachineStateConfigured()){
            originalMachineConfigurationComponent.setVisible(true);
            machineNotConfiguredLabel.setVisible(false);
        }
        else{
            originalMachineConfigurationComponent.setVisible(false);
            machineNotConfiguredLabel.setVisible(true);
        }
    }


    private void setDetails() {
        availableRotorNumLabel.setText((String.valueOf(DateService.getInventoryInfo().getNumOfAvailableRotors())));
//        encryptedMsgLabel.setText(DateService.getInventoryInfo());
        toUseRotorNumLabel.setText((String.valueOf(DateService.getInventoryInfo().getNumOfRotorsInUse())));
        availableReflectorNumLabel.setText((String.valueOf(DateService.getInventoryInfo().getNumOfAvailableReflectors())));
    }
}
