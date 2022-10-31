package controller;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import dto.InventoryInfo;
import dto.MachineState;
import enums.ReflectorsId;
import fx.component.ComboBoxCell;
import fx.component.ComboBoxItem;
import generictype.MappingPair;
import service.DataService;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SetMachineConfigController implements Initializable {
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Getter @Setter @FXML MachinePageController parentController;

    private SimpleBooleanProperty areRotorsChosen;
    private SimpleBooleanProperty areValuesSetUp;
    @Getter private SimpleBooleanProperty isSetRandomPressed;
    private final List<SimpleBooleanProperty> areRotorsChosenList = new ArrayList<>();

    @FXML @Getter private Button setRandomChoiceButton;
    @FXML private Button clearSelectionButton;
    @FXML private GridPane rootGridPane;
    @FXML private ImageView setMachineImageView;
    @FXML private Label titleLabel;
    @FXML private GridPane innerGridPane;
    @FXML private Label reflectorLabel;
    @FXML private Label rotorsLabel;
    @FXML private Label plugboardLabel;
    @FXML private ChoiceBox<String> reflectorChoiceBox;
    @FXML private HBox rotorsHbox;
    @FXML private ChoiceBox<?> rotorChoice2;
    @FXML private ChoiceBox<?> rotorChoice1;
    @FXML private TabPane plugBoardTabPane;
    @FXML private Tab plugBoardConnectionsTab;
    @FXML private ScrollPane plugBoardConnectionsScroller;
    @FXML private GridPane plugBoardConnectionsGridOfScroll;
    @FXML private ListView<String> plugBoardConnectionsLeft;
    @FXML private ListView<String> plugBoardConnectionsRight;
    @FXML private Tab plugBoardAddNewTab;
    @FXML private Pane plugBoardAddNewInnerPane;
    @FXML private ChoiceBox<String> plugBoardAddNewEP1Choice;
    @FXML private ChoiceBox<String> plugBoardAddNewEP2Choice;
    @FXML private Button plugBoardAddNewButton;
    @FXML private Button setUserChoiceButton;
    @FXML private HBox rotorsInitialPosHBox;
    @FXML private ChoiceBox<?> rotorInitialPosChoice2;
    @FXML private ChoiceBox<?> rotorInitialPosChoice1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
            @Override
            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
                if(newValue == null){
                    clearPageInfo();
                }else {
                    setMachineDetails(newValue);
                }
            }
        });
        setUserChoiceButton.disableProperty().bind(validationSupport.invalidProperty());
    }

    private void clearPageInfo() {
        Platform.runLater(() -> {
            rotorsHbox.getChildren().clear();
            reflectorChoiceBox.getItems().clear();
            rotorsInitialPosHBox.getChildren().clear();
            plugBoardAddNewEP1Choice.getItems().clear();
            plugBoardAddNewEP2Choice.getItems().clear();
        });
    }

    public void setMachineDetails(InventoryInfo inventoryInfo) {
        Platform.runLater(()->{
            setReflectorChoiceBoxHbox(inventoryInfo);
            setRotorsIdsHbox(inventoryInfo);
            setRotorsPositionsHBox(inventoryInfo);
            addPlugboardTab(inventoryInfo);
        });
    }

    private void setRotorsIdsHbox(InventoryInfo inventoryInfo) {
        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        Platform.runLater(()-> {
            Label idLabel = (Label) rotorsHbox.getChildren().get(0);
            rotorsHbox.getChildren().clear();
            rotorsHbox.getChildren().add(idLabel);
            ObservableList<ComboBoxItem<String>> observableRotorIdList = createRotorsIdsObservableList(inventoryInfo);
            for (int i = 1; i <= numOfNeededRotors; i++) {
                ComboBox<ComboBoxItem<String>> rotorsIdsComboBox = new ComboBox<>(observableRotorIdList);
                rotorsIdsComboBox.setCellFactory(param -> new ComboBoxCell<String>());
                rotorsIdsComboBox.setButtonCell(new ComboBoxCell<String>());
                validationSupport.registerValidator(rotorsIdsComboBox, Validator.createEmptyValidator("Selection required"));
                rotorsHbox.getChildren().add(rotorsIdsComboBox);
                rotorsIdsComboBox.setOnAction(e ->
                rotorsIdsComboBox.getSelectionModel().getSelectedItem().setChosen(true));
            }
        });
    }

    private ObservableList<ComboBoxItem<String>> createRotorsIdsObservableList(InventoryInfo inventoryInfo) {
        int numOfAvailableRotors = inventoryInfo.getNumOfAvailableRotors();
        List<ComboBoxItem<String>> availableIdsList = new ArrayList<>(numOfAvailableRotors);
            for (int i = 1; i <= numOfAvailableRotors; i++) {
                ComboBoxItem<String> item = new ComboBoxItem<String>(String.valueOf(i));
                availableIdsList.add(item);
            }
        ObservableList<ComboBoxItem<String>> observableRotorIdList = FXCollections.observableList(
                availableIdsList,
                comboBoxItem -> new Observable[]{comboBoxItem.isChosenProperty()}
        );
        return observableRotorIdList;
    }

    private void setReflectorChoiceBoxHbox(InventoryInfo inventoryInfo) {
        int reflectorNum = inventoryInfo.getNumOfAvailableReflectors();
        Platform.runLater(()->{
            reflectorChoiceBox.getItems().clear();
            for (int i = 1; i <= reflectorNum; i++) {
                reflectorChoiceBox.getItems().add(ReflectorsId.getByNum(i).getName());
            }});
        validationSupport.registerValidator(reflectorChoiceBox, Validator.createEmptyValidator( "Selection required"));
    }

    private void setRotorsPositionsHBox(InventoryInfo inventoryInfo){
        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        String Abc = inventoryInfo.getABC();
        Label posLabel = (Label) rotorsInitialPosHBox.getChildren().get(0);
        Platform.runLater(()-> {
            rotorsInitialPosHBox.getChildren().clear();
            rotorsInitialPosHBox.getChildren().add(posLabel);
            for (int i = 1; i <= numOfNeededRotors; i++) {
                ChoiceBox<String> newPosChoiceBox = createRotorPositionChoiceBox(Abc);
                rotorsInitialPosHBox.getChildren().add(newPosChoiceBox);
                validationSupport.registerValidator(newPosChoiceBox, Validator.createEmptyValidator("Selection required"));
            }
        });
    }

    private ChoiceBox<String> createRotorPositionChoiceBox(java.lang.String ABC){
        ChoiceBox<java.lang.String> newChoiceBox = new ChoiceBox<>();
        Platform.runLater(()->{
            for (int i = 0; i < ABC.length(); i++) {
            newChoiceBox.getItems().add(ABC.substring(i,i+1));
        }});
        return newChoiceBox;
    }

    private void addPlugboardTab(InventoryInfo inventoryInfo) {
        String ABC = inventoryInfo.getABC();
        plugBoardAddNewEP1Choice.getItems().clear();
        plugBoardAddNewEP2Choice.getItems().clear();
        for (int i = 0; i < ABC.length(); i++) {
            plugBoardAddNewEP1Choice.getItems().add(ABC.substring(i,i+1));
            plugBoardAddNewEP2Choice.getItems().add(ABC.substring(i,i+1));
        }
    }

    @FXML
    void onPlugBoardAddNewButtonAction(ActionEvent event) {
        String ep1 = plugBoardAddNewEP1Choice.getValue();
        String ep2 = plugBoardAddNewEP2Choice.getValue();
        if(Objects.equals(ep1, ep2) && ep1 != null){
            parentController.showMessage("Can't connect plug to itself");
            return;
        }
        if(plugBoardConnectionsLeft.getItems().contains(ep1)
                || plugBoardConnectionsLeft.getItems().contains(ep2)
                || plugBoardConnectionsRight.getItems().contains(ep1)
                || plugBoardConnectionsRight.getItems().contains(ep2)){
            parentController.showMessage("Plug already in use");
        }
        else if (ep1 != null && ep2 != null){
            plugBoardConnectionsLeft.getItems().add(ep1);
            plugBoardConnectionsRight.getItems().add(ep2);
            parentController.showMessage("Plug connection added successfully");
        }
    }

    @FXML
    void onSetUserChoiceButtonAction(ActionEvent event) {
        // Create the Machine State DTO
        ReflectorsId reflectorId = ReflectorsId.getByNum(reflectorChoiceBox.getSelectionModel().getSelectedIndex()+1);
        List<Integer> rotorIdsList = new ArrayList<>();
        List<String> rotorsStartingPositions = new ArrayList<>();
        for (int i = rotorsHbox.getChildren().size()-1; i >= 1; i--) {
            ComboBox<ComboBoxItem<String>> cb = (ComboBox<ComboBoxItem<String>>) rotorsHbox.getChildren().get(i);
            rotorIdsList.add(cb.getSelectionModel().getSelectedIndex()+1);
        }
        for (int i = rotorsInitialPosHBox.getChildren().size()-1; i >=1; i--) {
            ChoiceBox cb = (ChoiceBox) rotorsInitialPosHBox.getChildren().get(i);
            rotorsStartingPositions.add((String) cb.getSelectionModel().getSelectedItem());
        }
        List<MappingPair<String,String>> plugMapping = new ArrayList<>();
        for (int i = 0; i < plugBoardConnectionsLeft.getItems().size(); i++) {
            String ep1 = plugBoardConnectionsLeft.getItems().get(i);
            String ep2 = plugBoardConnectionsRight.getItems().get(i);
            MappingPair<String,String> plug = new MappingPair<>(ep1,ep2);
            plugMapping.add(plug);
        }
        MachineState machineSetupUserChoice = new MachineState(reflectorId, rotorIdsList, rotorsStartingPositions, plugMapping, null);
        // Pass it upwards
        parentController.handleManuelSetMachinePressed(machineSetupUserChoice);
    }

    @FXML
    void onSetRandomChoiceButtonAction(ActionEvent event) {
        parentController.handleRandomSetMachinePressed();
    }

    @FXML
    void onClearSelectionButton(ActionEvent actionEvent) {
        //reflector
        reflectorChoiceBox.getSelectionModel().clearSelection();
        //rotors
        for (int i = rotorsHbox.getChildren().size()-1; i >= 1; i--) {
            ComboBox<ComboBoxItem<String>> cb = (ComboBox<ComboBoxItem<String>>) rotorsHbox.getChildren().get(i);
            try{
                if(cb!=null && cb.getSelectionModel() != null && !cb.getSelectionModel().isEmpty() ){
                    cb.getSelectionModel().clearSelection();
                }
            }
            catch(NullPointerException ignore){}
            for (ComboBoxItem<String> item : cb.getItems()) {
                item.setChosen(false);
            }

        }
        //rotor starting pos
        for (int i = rotorsInitialPosHBox.getChildren().size()-1; i >=1; i--) {
            ChoiceBox cb = (ChoiceBox) rotorsInitialPosHBox.getChildren().get(i);
            if(cb.getSelectionModel()!= null && cb.getSelectionModel().getSelectedItem() != null) {
                cb.getSelectionModel().clearSelection();
            }
        }
        //plug board
        plugBoardConnectionsLeft.getItems().clear();
        plugBoardConnectionsRight.getItems().clear();
    }

    @FXML
    void onRefreshInventoryButtonClick(ActionEvent event) {
        DataService.fetchInventoryInfo();
    }
}
