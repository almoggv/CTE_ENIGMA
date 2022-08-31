package src.main.java.controller;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import main.java.enums.ReflectorsId;
import src.main.java.service.DateService;

import java.net.URL;
import java.util.*;


public class SetMachineConfigController implements Initializable {

    @Getter @Setter
    @FXML MachinePageController parentController;

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
    private SimpleBooleanProperty areValuesSetUp;

    @Getter private SimpleBooleanProperty isSetRandomPressed;

    private final List<SimpleBooleanProperty> areRotorsChosenList = new ArrayList<>();
//    private final List<SimpleBooleanProperty> areRotorsChosenList =  FXCollections.observableArrayList(c -> new Observable[]{c.singleProperty()});

    private SimpleBooleanProperty areRotorsChosen;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        setUserChoiceButton.disableProperty().bind(areValuesSetUp.not());
//        isSetRandomPressed.bind(setRandomChoiceButton.pressedProperty());
        setReflectorChoiceBoxHbox();
        setRotorsHbox();
        addPlugboardTab();
        setRandomChoiceButton.pressedProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("setRandom Pressed!"));
    }

    public SetMachineConfigController(){
        areValuesSetUp = new SimpleBooleanProperty(false);
        areRotorsChosen = new SimpleBooleanProperty(false);
        isSetRandomPressed = new SimpleBooleanProperty(false);
//        areRotorsChosen = areRotorsChosenList.stream().noneMatch(ChoiceBox);
//        children.stream().anyMatch(Child::isSingle), children);

    }
    private void setReflectorChoiceBoxHbox() {
        int reflectorNum = DateService.getInventoryInfo().getNumOfAvailableRotors();
        rotorsHbox.getChildren().clear();
        for (int i = 1; i <= reflectorNum; i++) {
            reflectorChoiceBox.getItems().add(ReflectorsId.getByNum(i).getName());
        }
    }

    private void setRotorsHbox(){
        int numOfAvailableRotors = DateService.getInventoryInfo().getNumOfAvailableRotors();
        int numOfNeededRotors = DateService.getInventoryInfo().getNumOfRotorsInUse();
        rotorsHbox.getChildren().clear();
        for (int i = 1; i <= numOfNeededRotors; i++) {
            ChoiceBox<Integer> rotorChoiceBox= makeRotorChoiceBox(numOfAvailableRotors);
            SimpleBooleanProperty rotorProperty = new SimpleBooleanProperty(false);
            rotorProperty.bind(rotorChoiceBox.valueProperty().isNotNull());
            areRotorsChosenList.add(rotorProperty);
            rotorsHbox.getChildren().add(rotorChoiceBox);
        }
        setUserChoiceButton.disableProperty().bind(areRotorsChosen.not());
    }

    private ChoiceBox<Integer> makeRotorChoiceBox(int rotorsNum) {
        ChoiceBox<Integer> rotorChoiceBox = new ChoiceBox<>();
        for (int i = 1; i <= rotorsNum; i++) {
            rotorChoiceBox.getItems().add(i);
        }
        return rotorChoiceBox;
    }

//    public void loadData(InventoryInfo inventoryInfo) {
////        setReflectorChoiceBoxHbox(inventoryInfo);
//        setRotorsHbox(inventoryInfo);
//        addPlugboardTab(inventoryInfo);
//    }

    private void addPlugboardTab() {
        String ABC = DateService.getInventoryInfo().getABC();
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
//        parentController.handleSetRandomPressed();
    }


    public void addOnSetButtonListener(ChangeListener<Boolean> booleanChangeListener) {
        setRandomChoiceButton.pressedProperty().addListener(booleanChangeListener);
    }
}
