package src.main.java.controller;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import main.java.dto.InventoryInfo;
import main.java.enums.ReflectorsId;
import src.main.java.fxcomponent.ComboBoxCell;
import src.main.java.fxcomponent.ComboBoxItem;
import src.main.java.service.DataService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class SetMachineConfigController implements Initializable {

    @Getter @Setter
    @FXML MachinePageController parentController;

    private SimpleBooleanProperty areRotorsChosen;
    private SimpleBooleanProperty areValuesSetUp;
    @Getter private SimpleBooleanProperty isSetRandomPressed;
    private final List<SimpleBooleanProperty> areRotorsChosenList = new ArrayList<>();

    //    private final List<SimpleBooleanProperty> areRotorsChosenList =  FXCollections.observableArrayList(c -> new Observable[]{c.singleProperty()});

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
    @FXML @Getter private Button setRandomChoiceButton;
    @FXML private HBox rotorsInitialPosHBox;
    @FXML private ChoiceBox<?> rotorInitialPosChoice2;
    @FXML private ChoiceBox<?> rotorInitialPosChoice1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
            @Override
            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
                setMachineDetails(newValue);
            }
        });

    }



    public void setMachineDetails(InventoryInfo inventoryInfo){

        setReflectorChoiceBoxHbox(inventoryInfo);
        setRotorsIdsHbox(inventoryInfo);
        setRotorsPositionsHBox(inventoryInfo);
        addPlugboardTab(inventoryInfo);

    }

    private void setRotorsIdsHbox(InventoryInfo inventoryInfo){
        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        Label idLabel = (Label) rotorsHbox.getChildren().get(0);
        rotorsHbox.getChildren().clear();
        rotorsHbox.getChildren().add(idLabel);
        ObservableList<ComboBoxItem<String>> observableRotorIdList = createRotorsIdsObservableList(inventoryInfo);
        for (int i = 1; i <= numOfNeededRotors; i++) {
            ComboBox<ComboBoxItem<String>> rotorsIdsComboBox = new ComboBox<>(observableRotorIdList);
//            rotorsIdsComboBox.getSelectionModel().select();
            rotorsIdsComboBox.setCellFactory(param -> new ComboBoxCell<String>());
            rotorsIdsComboBox.setButtonCell(new ComboBoxCell<String>());
            rotorsHbox.getChildren().add(rotorsIdsComboBox);
        }
    }

    private ObservableList<ComboBoxItem<String>> createRotorsIdsObservableList(InventoryInfo inventoryInfo){
        int numOfAvailableRotors = inventoryInfo.getNumOfAvailableRotors();
        List<ComboBoxItem<String>> availableIdsList = new ArrayList<>(numOfAvailableRotors);
//        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        for (int i = 1; i <= numOfAvailableRotors; i++) {
            availableIdsList.add(new ComboBoxItem<String>(String.valueOf(i)));
        }
        ObservableList<ComboBoxItem<String>> observableRotorIdList = FXCollections.observableList(
                availableIdsList,
                comboBoxItem -> new Observable[] { comboBoxItem.isChosenProperty() }
        );
        return observableRotorIdList;
    }

    //    public void setMachineDetails(InventoryInfo inventoryInfo) {
//        setReflectorChoiceBoxHbox(inventoryInfo);
//        setRotorsIdsHbox(inventoryInfo);
//        setRotorsPositionsHBox(inventoryInfo);
//        addPlugboardTab(inventoryInfo);
//    }
//
    private void setReflectorChoiceBoxHbox(InventoryInfo inventoryInfo) {
        int reflectorNum = inventoryInfo.getNumOfAvailableRotors();
        reflectorChoiceBox.getItems().clear();
        for (int i = 1; i <= reflectorNum; i++) {
            reflectorChoiceBox.getItems().add(ReflectorsId.getByNum(i).getName());
        }
    }
//
//    private void setRotorsIdsHbox(InventoryInfo inventoryInfo){
//        int numOfAvailableRotors = inventoryInfo.getNumOfAvailableRotors();
//        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
//        Label idLabel = (Label) rotorsHbox.getChildren().get(0);
//        rotorsHbox.getChildren().clear();
//        rotorsHbox.getChildren().add(idLabel);
//        for (int i = 1; i <= numOfNeededRotors; i++) {
//            ChoiceBox<Integer> rotorChoiceBox= createRotorIdChoiceBox(numOfAvailableRotors);
//            rotorsHbox.getChildren().add(rotorChoiceBox);
//        }
//    }
//
    private void setRotorsPositionsHBox(InventoryInfo inventoryInfo){
        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        String Abc = inventoryInfo.getABC();
        Label posLabel = (Label) rotorsInitialPosHBox.getChildren().get(0);
        rotorsInitialPosHBox.getChildren().clear();
        rotorsInitialPosHBox.getChildren().add(posLabel);
        for(int i = 1; i <= numOfNeededRotors; i++) {
            ChoiceBox<String> newPosChoiceBox = createRotorPositionChoiceBox(Abc);
            rotorsInitialPosHBox.getChildren().add(newPosChoiceBox);
        }
    }

    private ChoiceBox<String> createRotorPositionChoiceBox(String ABC){
        ChoiceBox<String> newChoiceBox = new ChoiceBox<>();
        for (int i = 0; i < ABC.length(); i++) {
            newChoiceBox.getItems().add(ABC.substring(i,i+1));
        }
        return newChoiceBox;
    }
//
//    private ChoiceBox<Integer> createRotorIdChoiceBox(int amountOfRotors) {
//        ChoiceBox<Integer> rotorChoiceBox = new ChoiceBox<>();
//        for (int i = 1; i <= amountOfRotors; i++) {
//            rotorChoiceBox.getItems().add(i);
//        }
//        return rotorChoiceBox;
//    }
//
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
        plugBoardConnectionsLeft.getItems().add(plugBoardAddNewEP1Choice.getValue());
        plugBoardConnectionsRight.getItems().add(plugBoardAddNewEP2Choice.getValue());
    }

    @FXML
    void onSetUserChoiceButtonAction(ActionEvent event) {
        if (reflectorChoiceBox.getValue()== null){
//      todo - get message to header
//            parentController.
        }
    }

    @FXML
    void onSetRandomChoiceButtonAction(ActionEvent event) {

    }

    public void addListenerOnClickSetRandomButton(ChangeListener listener) {
        //Triggers Twice (down click + release) - expected behaviours, but not wanted
        setRandomChoiceButton.pressedProperty().addListener(listener);
        //Doesnt Trigger for unknown reason
//        setRandomChoiceButton.onActionProperty().addListener(listener);
//        setRandomChoiceButton.onMouseClickedProperty().addListener(listener);

    }

}
