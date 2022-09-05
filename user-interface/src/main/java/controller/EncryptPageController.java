package src.main.java.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
import src.main.java.service.PropertiesService;
import src.main.java.ui.CLIMenu;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    public ScrollPane scrollOfRightBottomAnchor;
    public AnchorPane rightAnchorOfBottom;
    public Accordion statisticsAccordion;
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

        statisticsAccordion.getPanes().clear();

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
            String title = "Machine state:" + CLIMenu.getMachineState(DataService.getCurrentMachineStateProperty().get(),
                    DataService.getInventoryInfoProperty().get());
            TitledPane a = new TitledPane();
            a.setText(title);
            statisticsAccordion.getPanes().add(a);
            List<EncryptionInfoHistory> encryptionInfoHistoryList = encryptionInfoHistory.get(machineStateHistory);
//            out.println("Machine encryption history:");
            for (EncryptionInfoHistory encryptionHistory : encryptionInfoHistoryList) {
//                out.println(i++ + ". <" + encryptionHistory.getInput() + "> --> <" + encryptionHistory.getOutput() + "> ( " + encryptionHistory.getTimeToEncrypt() + " nano-seconds)");
//                statisticsColOriginal.
//                encryptionHistory.getInput()

                //try
//                URL StatisticsTableResource = GuiApplication.class.getResource(PropertiesService.getStatisticsTableFxmlPath());
//                FXMLLoader fxmlLoader = new FXMLLoader();
//                fxmlLoader.setLocation(StatisticsTableResource);
//                Parent table = null;
//                try {
//                    table = fxmlLoader.load(StatisticsTableResource.openStream());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                StatisticsTableController controller = fxmlLoader.getController();
//                a.setContent(table);
//        statisticsColOriginal.setCellFactory();
//                statisticsAccordion.getPanes().add(a);
            }

        }
    }

    public void onEncryptButtonAction(ActionEvent actionEvent) throws IOException {
        try {
            String result = machineHandler.encrypt(encryptTextField.getText());
            resultTextField.setText(result);
            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
            DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
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
