package controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EncryptionController implements Initializable {

    @FXML
    public CustomTextField encryptTextField;
    @FXML public Button encryptButton;
    @FXML public CustomTextField resultTextField;
    @FXML public Button clearButton;

    @FXML public Button resetMachineStateButton;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        encryptButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        encryptTextField.getText().trim().isEmpty(),
                encryptTextField.textProperty()));
    }

    public void onEncryptButtonAction(ActionEvent actionEvent) throws IOException {
//        try {
//            String result = machineHandler.encrypt(encryptTextField.getText());
//            resultTextField.setText(result);
//            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
//            //need to set to null - because the machine state thinks it hasent changed - only internal structures changed and it dosent register
//            DataService.getEncryptionInfoHistoryProperty().setValue(null);
//            DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
//            log.debug("EncryptPageController - machine handler's statistics and history" + System.lineSeparator() + machineHandler.getMachineStatisticsHistory());
//        }
//        catch (Exception e){
//            parentController.showMessage(e.getMessage());
//        }
    }

    public void onClearButtonAction(ActionEvent actionEvent) {
        resultTextField.clear();
        encryptTextField.clear();
    }

    public void onResetMachineStateButtonAction(ActionEvent actionEvent) {
//        machineHandler.resetToLastSetState();
//        DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
//        encryptTextField.clear();
//        resultTextField.clear();
//        liveEncryptionInputProperty.setValue("");
//        liveEncryptionOutputProperty.setValue("");
//        parentController.showMessage("Reset to last set machine state.");
    }
}
