package src.main.java.controller;

import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.dto.EncryptionInfoHistory;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.manager.DecryptionManager;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
//import src.main.java.adapter.UIAdapter;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BruteForcePageController implements Initializable {

    public Label amountOfTasksNumLabel;
    public Label amountOfAgentsValueLabel;
    public ListView dictionaryList;
    public CustomTextField encryptTextField;
    public Button encryptButton;
    public CustomTextField resultTextField;
    public Button clearButton;
    MachineHandler machineHandler;
    @Setter private DecryptionManager decryptionManager;

//    @Setter @Getter private UIAdapter uiAdapter;

    private AppController parentController;
    @FXML
    private CurrMachineConfigController currMachineConfigComponentController;

    public GridPane rootGridPane;
    public Slider amountOfAgentsSlider;
    public ComboBox<DecryptionDifficultyLevel> difficultyComboBox;
    public TextField taskSizeTextField;
    public FlowPane dmResultsFlowPane;
    public Button startDecryptButton;

    private final Integer MIN_AGENT_AMOUNT = 2;
    @FXML
    public GridPane currMachineConfigComponent;

    private ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getMaxAgentNumProperty().addListener((observable, oldValue, newValue) -> {
            setDMDetails(newValue);
        });

        startDecryptButton.disableProperty().bind(validationSupport.invalidProperty());
//        startDecryptButton.disableProperty().bind(resultTextField.promptTextProperty().isNotNull());

        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }

        startDecryptButton.disableProperty().addListener((observable, oldValue, newValue) -> {
            showAmountOfTasks();
        });

        amountOfAgentsSlider.valueProperty().addListener((observable, oldValue, newValue) ->{
            decryptionManager.setNumberOfAgents((int) Math.floor((Double) newValue));
            amountOfAgentsValueLabel.setText(String.valueOf((int) Math.floor((Double) newValue)));
        });

        difficultyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            decryptionManager.setDifficultyLevel(newValue);
        });


    }

    private void showAmountOfTasks() {
        int taskSize = 0;
        taskSize = decryptionManager.getAmountOfTasks();
        amountOfTasksNumLabel.setText(String.valueOf(taskSize));
    }

    private void setDMDetails(Number newValue) {
        setDifficultyComboBox();
        setAgentAmountSlider(newValue);
        validationSupport.registerValidator(taskSizeTextField, Validator.createEmptyValidator("Value required"));
    }

    private void setAgentAmountSlider(Number newValue) {
        amountOfAgentsSlider.setMax((int) newValue);
        amountOfAgentsSlider.setMin(MIN_AGENT_AMOUNT);
    }

    private void setDifficultyComboBox() {
        difficultyComboBox.getItems().clear();
        for (DecryptionDifficultyLevel level : DecryptionDifficultyLevel.values()) {
            difficultyComboBox.getItems().add(level);
        }
        validationSupport.registerValidator(difficultyComboBox, Validator.createEmptyValidator("Selection required"));
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

    public void onTaskSizeTextFieldFilled(ActionEvent actionEvent) {
        int choice;
        try{
            choice = Integer.parseInt(taskSizeTextField.getText());
            if(choice < 1){
                parentController.showMessage("Please enter natural number over 1");
            }
            else {
                decryptionManager.setTaskSize(choice);
            }
        }
        catch (Exception ex){
            parentController.showMessage("Please enter natural number over 1");
        }
    }

    public void onEncryptButtonAction(ActionEvent actionEvent) {
        try {
            String result = machineHandler.encrypt(encryptTextField.getText());
            resultTextField.setText(result);
            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
            DataService.getEncryptionInfoHistoryProperty().setValue(null);
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

    public void onStartDecryptButtonAction(ActionEvent actionEvent) throws InterruptedException {
        if(resultTextField.getText().isEmpty()){
            parentController.showMessage("Please encrypt first.");
        }
        else{
//            UIAdapter uiAdapter = createUIAdapter();
//            decryptionManager.setUIAdapter(uiAdapter);
            decryptionManager.bruteForceDecryption(resultTextField.getText());
        }
    }

//    private UIAdapter createUIAdapter() {
//        UIAdapter adapter = new UIAdapter( candidate -> {
//            createTile(candidate.getOutput(), candidate.getTimeToDecrypt());
//        });
//        return adapter;
//    }

//    private void createTile(String output, long timeToDecrypt) {
//        try {
//            URL decodedCandidateURL = GuiApplication.class.getResource(ResourceLocationService.getDecodeCandidatePath());
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(decodedCandidateURL);
//            Parent decodedCandidate = fxmlLoader.load(decodedCandidateURL.openStream());
//            DecodedCandidateController decodedCandidateController = fxmlLoader.getController();
//            decodedCandidateController.setCandidateLabel(output);
//            decodedCandidateController.setTimeLabel(String.valueOf(Math.floor(timeToDecrypt)));
//            dmResultsFlowPane.getChildren().add(decodedCandidate);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}