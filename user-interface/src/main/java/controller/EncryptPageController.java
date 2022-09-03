package src.main.java.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.dto.EncryptionInfoHistory;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import org.controlsfx.control.textfield.CustomTextField;
import src.main.java.service.DataService;
import src.main.java.ui.CLIMenu;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static java.lang.System.lineSeparator;
import static java.lang.System.out;

public class EncryptPageController implements Initializable {

    public CustomTextField encryptTextField;
    public Button encryptButton;
    public TableView statisticsTable;
    public TableColumn statisticsColOriginal;
    public TableColumn statisticsColEncrypted;
    public TableColumn statisticsColTime;
    public CustomTextField resultTextField;
    public FlowPane inputAbcFlowPane;
    public Button clearButton;
    public Button resetMachineStateButton;
    @Getter @Setter private MachineHandler machineHandler;

    @Getter @Setter @FXML AppController parentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;

    @FXML public GridPane currMachineConfigComponent;
    @FXML private GridPane rootGrid;
    @FXML private ScrollPane currMachineConfigWrapperPane;
    @FXML private TextField testTextArea;
    @FXML private FlowPane keyboardFlowPane;

    private SimpleBooleanProperty isEncryptionTextFieldEmpty = new SimpleBooleanProperty(true);
    public Parent getRootComponent(){
        return rootGrid;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        encryptButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        encryptTextField.getText().trim().isEmpty(),
                encryptTextField.textProperty()));
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
            @Override
            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
                showAbc(newValue);
            }
        });
    }

    private void showAbc(InventoryInfo inventoryInfo) {
        String abc = inventoryInfo.getABC();
        for (int i = 0; i < abc.length(); i++) {
            Button letter = new Button(abc.substring(i,i+1));
            inputAbcFlowPane.getChildren().add(letter);
        }
    }

    /**
     * binds the root component of the controller to the designated pane.
     */
    public void bindComponent(CurrMachineConfigController currMachineConfigComponentController) {
        this.currMachineConfigComponentController = currMachineConfigComponentController;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
//        currMachineConfigWrapperPane.setContent(currMachineConfigComponent);
    }

    public void bindToData(SimpleObjectProperty<Map<MachineState, List<EncryptionInfoHistory>>> dataProperty){
        dataProperty.addListener(new ChangeListener<Map<MachineState, List<EncryptionInfoHistory>>>() {
            @Override
            public void changed(ObservableValue<? extends Map<MachineState, List<EncryptionInfoHistory>>> observable, Map<MachineState, List<EncryptionInfoHistory>> oldValue, Map<MachineState, List<EncryptionInfoHistory>> newValue) {
                showStatistics(newValue);
            }
        });
    }

    private void showStatistics(Map<MachineState, List<EncryptionInfoHistory>> encryptionInfoHistory) {
        for (MachineState machineStateHistory : encryptionInfoHistory.keySet()) {
//            out.println(lineSeparator() + "Machine state:");
//            String rotorsInMachineMsg = CLIMenu.buildRotorsInMachineMsg(machineStateHistory,inventoryInfo.get());
//            String rotorsHeadLocationInMachineMsg = CLIMenu.buildRotorHeadLocationInMachineMsg(machineStateHistory);
//            String reflectorIdMsg = "<" + machineStateHistory.getReflectorId().getName() + ">";
//            String plugsMappedMsg = CLIMenu.buildPlugMappingInMachineMsg(machineStateHistory);

//            machineStateMsg = rotorsInMachineMsg + rotorsHeadLocationInMachineMsg + reflectorIdMsg + plugsMappedMsg;
//            out.println(machineStateMsg);
//            int i = 1;
//            List<EncryptionInfoHistory> encryptionInfoHistoryList = encryptionInfoHistory.get(machineStateHistory);
//            out.println("Machine encryption history:");
//            for (EncryptionInfoHistory encryptionHistory : encryptionInfoHistoryList) {
//                out.println(i++ + ". <" + encryptionHistory.getInput() + "> --> <" + encryptionHistory.getOutput() + "> ( " + encryptionHistory.getTimeToEncrypt() + " nano-seconds)");
//                statisticsColOriginal.
//            }
//
        }
    }

    public void onEncryptButtonAction(ActionEvent actionEvent) throws IOException {
        try {
            String result = machineHandler.encrypt(encryptTextField.getText());
            resultTextField.setText(result);
        }
        catch (Exception e){
            parentController.showMessage(e.getMessage());
        }
    }

    public void onClearButtonAction(ActionEvent actionEvent) {
        resultTextField.clear();
        encryptTextField.clear();
    }

    public void onResetMachineStateButtonAction(ActionEvent actionEvent) {
        machineHandler.resetToLastSetState();
        parentController.showMessage("Reset to last set machine state.");
    }
}
