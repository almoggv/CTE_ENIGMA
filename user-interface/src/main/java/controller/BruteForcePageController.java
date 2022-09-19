package src.main.java.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import main.java.component.MachineHandler;
import main.java.dto.AgentDecryptionInfo;
import main.java.dto.MachineState;
import main.java.enums.DecryptionDifficultyLevel;
import main.java.generictype.MappingPair;
import main.java.manager.DecryptionManager;
import main.java.manager.DictionaryManager;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import main.java.adapter.UIAdapter;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;
import src.main.java.service.Trie;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;

public class BruteForcePageController implements Initializable {

    public Label amountOfTasksNumLabel;
    public Label amountOfAgentsValueLabel;
    public ListView dictionaryList;
    public CustomTextField encryptTextField;
    public Button encryptButton;
    public CustomTextField resultTextField;
    public Button clearButton;
    public TextField dictionaryTextField;
    public Button pauseDecryptButton;
    public Label progressPrecentNumberLabel;
    public ProgressBar progressBar;
    public Button stopDecryptButton;
    MachineHandler machineHandler;
    private DecryptionManager decryptionManager;

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

    private BooleanProperty isDecryptionRunningProperty = new SimpleBooleanProperty(false);


    private Trie dictionaryTrie;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getMaxAgentNumProperty().addListener((observable, oldValue, newValue) -> {
            setDMDetails(newValue);
        });

        DictionaryManager.getDictionaryProperty().addListener(((observable, oldValue, newValue) ->{
            dictionaryTrie = new Trie(DictionaryManager.getDictionaryProperty().get());
            dictionaryList.getItems().addAll(dictionaryTrie.suggest(""));
        }));

        dictionaryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            dictionaryList.getItems().clear();
            dictionaryList.getItems().addAll(dictionaryTrie.suggest(newValue.toUpperCase()));
        });

        startDecryptButton.disableProperty().bind(validationSupport.invalidProperty());
        stopDecryptButton.disableProperty().bind(isDecryptionRunningProperty.not());
        pauseDecryptButton.disableProperty().bind(isDecryptionRunningProperty.not());

        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        startDecryptButton.disableProperty().addListener((observable, oldValue, newValue) -> {
            showAmountOfTasks();
        });
        amountOfAgentsSlider.valueProperty().addListener((observable, oldValue, newValue) ->{
            if(decryptionManager!=null){
                decryptionManager.setNumberOfAgents((int) Math.floor((Double) newValue));
            }
            amountOfAgentsValueLabel.setText(String.valueOf((int) Math.floor((Double) newValue)));
        });
        amountOfAgentsSlider.valueProperty().bindBidirectional(DataService.getCurrNumberOfAgentsProperty());
        //Patch to trigger an on value change for the DataService property
        //so that we will set it in the creating of the decryption manager
        amountOfAgentsSlider.valueProperty().setValue(amountOfAgentsSlider.getMax());
        amountOfAgentsSlider.valueProperty().setValue(amountOfAgentsSlider.getMin());
        taskSizeTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            try{
                int value = Integer.parseInt(newValue);
                if(value > 0){
                    decryptionManager.setTaskSize(value);
                }
                else{
                    parentController.showMessage("Please enter natural number over 1");
                }
            }
            catch(Exception ignore){
                parentController.showMessage("Please enter natural number over 1");
            }
        }));

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
        difficultyComboBox.getSelectionModel().select(0);
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
            return;
        }
        if(decryptionManager.getIsBruteForceInitiatedProperty().get()){
            decryptionManager.stopWork();
        }
        clearDecryptionResults();
        decryptionManager.bruteForceDecryption(resultTextField.getText());
    }

    public void onPauseDecryptButtonAction(ActionEvent actionEvent) {
        if(decryptionManager.getIsBruteForcePausedProperty().get() == false){
            decryptionManager.pauseWork();
            pauseDecryptButton.setText("Resume decrypting");
        }
        else{
            decryptionManager.resumeWork();
            pauseDecryptButton.setText("Pause decrypting");
        }

    }

    public void onStopDecryptButtonAction(ActionEvent actionEvent) {
        if(decryptionManager!=null){
            decryptionManager.stopWork();
        }
        clearDecryptionResults();
    }

    private void clearDecryptionResults() {
        this.dmResultsFlowPane.getChildren().clear();
        this.progressBar.setProgress(0.0);
        this.progressPrecentNumberLabel.setText("%");
    }

    public void onDictionaryListClicked(MouseEvent mouseEvent) {
        try {
            String word = dictionaryList.getSelectionModel().getSelectedItem().toString();
            String result = machineHandler.encryptWithoutHistory(word);
            resultTextField.setText(result);
            encryptTextField.setText(word);
            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
            DataService.getEncryptionInfoHistoryProperty().setValue(null);
            DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
        }
        catch (Exception e){
            parentController.showMessage(e.getMessage());
        }
    }

    public void onResetMachineStateButtonAction(ActionEvent actionEvent) {
        machineHandler.resetToLastSetState();
        DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
        parentController.showMessage("Reset to last set machine state.");
    }

    public UIAdapter createUIAdapter() {
        Consumer<AgentDecryptionInfo>  updateCandidates = candidate -> {
            createTile(candidate.getOutput(), candidate.getInitialState(),candidate.getAgentID() /*candidate.getTimeToDecrypt()*/);
        };
        Consumer<MappingPair<Integer,Integer>> updateProgress = (progress) ->{
            double progressValue = (double)progress.getLeft() / (double)progress.getRight();
            int progressPrecentage = (int)Math.floor(progressValue*100);
            progressPrecentNumberLabel.setText(progressPrecentage+"%");
            progressBar.setProgress(progressValue);
        };
        UIAdapter adapter = new UIAdapter( updateCandidates,updateProgress);
        return adapter;
    }

    private void createTile(String output, MachineState foundByState, UUID agentID /*long timeToDecrypt*/) {
        try {
            URL decodedCandidateURL = GuiApplication.class.getResource(ResourceLocationService.getDecodeCandidatePath());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(decodedCandidateURL);
            Parent decodedCandidate = fxmlLoader.load(decodedCandidateURL.openStream());
            DecodedCandidateController decodedCandidateController = fxmlLoader.getController();
            decodedCandidateController.setCandidateLabel(output);
            decodedCandidateController.setFoundByConfigLabel(foundByState);
            decodedCandidateController.setFoundByAgentIdLabel(agentID.toString());

//            decodedCandidateController.setTimeLabel(String.valueOf(Math.floor(timeToDecrypt)));
            dmResultsFlowPane.getChildren().add(decodedCandidate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDecryptionManager(DecryptionManager decryptionManager) {
        this.decryptionManager = decryptionManager;
        this.isDecryptionRunningProperty.bind(decryptionManager.getIsBruteForceInitiatedProperty());
    }
}