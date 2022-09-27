package src.main.java.controller;

import javafx.animation.*;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import main.java.dto.InventoryInfo;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import src.main.java.fxcomponent.ComboBoxCell;
import src.main.java.fxcomponent.ComboBoxItem;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SetMachineConfigController implements Initializable {

    public Button clearSelectionButton;
    public GridPane rootGridPane;
    public ImageView setMachineImageView;
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

    private double origX;
    private double origY;
    private ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getInventoryInfoProperty().addListener(new ChangeListener<InventoryInfo>() {
            @Override
            public void changed(ObservableValue<? extends InventoryInfo> observable, InventoryInfo oldValue, InventoryInfo newValue) {
                setMachineDetails(newValue);
            }
        });
        setUserChoiceButton.disableProperty().bind(validationSupport.invalidProperty());

        setMachineImageView.setImage(new Image(ResourceLocationService.getEnigmaMachineIllustration()));
        origX = setMachineImageView.getX();
        origY = 50;
    }

    public void setMachineDetails(InventoryInfo inventoryInfo){
        setReflectorChoiceBoxHbox(inventoryInfo);
        setRotorsIdsHbox(inventoryInfo);
        setRotorsPositionsHBox(inventoryInfo);
        addPlugboardTab(inventoryInfo);
    }

    private void setRotorsIdsHbox(InventoryInfo inventoryInfo) {
        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        Label idLabel = (Label) rotorsHbox.getChildren().get(0);
        rotorsHbox.getChildren().clear();
        rotorsHbox.getChildren().add(idLabel);
        ObservableList<ComboBoxItem<String>> observableRotorIdList = createRotorsIdsObservableList(inventoryInfo);
        for (int i = 1; i <= numOfNeededRotors; i++) {
//            AtomicReference<Integer> lastSelected = new AtomicReference<>(0);
            ComboBox<ComboBoxItem<String>> rotorsIdsComboBox = new ComboBox<>(observableRotorIdList);
//            rotorsIdsComboBox.getSelectionModel().select(0);
            rotorsIdsComboBox.setCellFactory(param -> new ComboBoxCell<String>());
            rotorsIdsComboBox.setButtonCell(new ComboBoxCell<String>());
            validationSupport.registerValidator(rotorsIdsComboBox, Validator.createEmptyValidator("Selection required"));
            rotorsHbox.getChildren().add(rotorsIdsComboBox);
//            rotorsIdsComboBox.setOnAction(e-> observableRotorIdList.get(lastSelected.get()).setChosen(false));
//            rotorsIdsComboBox.setOnAction(e-> lastSelected.set(rotorsIdsComboBox.getSelectionModel().getSelectedIndex()));
            rotorsIdsComboBox.setOnAction(e ->
                    rotorsIdsComboBox.getSelectionModel()
                            .getSelectedItem()
                            .setChosen(true));

            //todo - remove selection - make available again;
//            rotorsIdsComboBox.getSelectionModel()
        }
    }
    private ObservableList<ComboBoxItem<String>> createRotorsIdsObservableList(InventoryInfo inventoryInfo){
        int numOfAvailableRotors = inventoryInfo.getNumOfAvailableRotors();
        List<ComboBoxItem<String>> availableIdsList = new ArrayList<>(numOfAvailableRotors);
        for (int i = 1; i <= numOfAvailableRotors; i++) {
            ComboBoxItem<String> item = new ComboBoxItem<String>(String.valueOf(i));
            availableIdsList.add(item);
        }
        ObservableList<ComboBoxItem<String>> observableRotorIdList = FXCollections.observableList(
                availableIdsList,
                comboBoxItem -> new Observable[] { comboBoxItem.isChosenProperty() }
        );
        return observableRotorIdList;
    }

    private void setReflectorChoiceBoxHbox(InventoryInfo inventoryInfo) {
        int reflectorNum = inventoryInfo.getNumOfAvailableReflectors();
        reflectorChoiceBox.getItems().clear();
        for (int i = 1; i <= reflectorNum; i++) {
            reflectorChoiceBox.getItems().add(ReflectorsId.getByNum(i).getName());
        }
        validationSupport.registerValidator(reflectorChoiceBox, Validator.createEmptyValidator( "Selection required"));
    }

    private void setRotorsPositionsHBox(InventoryInfo inventoryInfo){
        int numOfNeededRotors = inventoryInfo.getNumOfRotorsInUse();
        String Abc = inventoryInfo.getABC();
        Label posLabel = (Label) rotorsInitialPosHBox.getChildren().get(0);
        rotorsInitialPosHBox.getChildren().clear();
        rotorsInitialPosHBox.getChildren().add(posLabel);
        for(int i = 1; i <= numOfNeededRotors; i++) {
            ChoiceBox<String> newPosChoiceBox = createRotorPositionChoiceBox(Abc);
            rotorsInitialPosHBox.getChildren().add(newPosChoiceBox);
            validationSupport.registerValidator(newPosChoiceBox, Validator.createEmptyValidator( "Selection required"));
        }
    }

    private ChoiceBox<String> createRotorPositionChoiceBox(String ABC){
        ChoiceBox<String> newChoiceBox = new ChoiceBox<>();
        for (int i = 0; i < ABC.length(); i++) {
            newChoiceBox.getItems().add(ABC.substring(i,i+1));
        }
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
            //todo - can make message more clear - not a priority atm
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
        ReflectorsId reflectorId = ReflectorsId.getByNum(reflectorChoiceBox.getSelectionModel().getSelectedIndex()+1);
        List<Integer> rotorIdsList = new ArrayList<>();
        //todo - fix issue - thinks kids are lables
        for (int i = rotorsHbox.getChildren().size()-1; i >= 1; i--) {
            ComboBox<ComboBoxItem<String>> cb = (ComboBox<ComboBoxItem<String>>) rotorsHbox.getChildren().get(i);
            rotorIdsList.add(cb.getSelectionModel().getSelectedIndex()+1);
        }
        String rotorsStartingPositions = "";
        for (int i = rotorsInitialPosHBox.getChildren().size()-1; i >=1; i--) {
            ChoiceBox cb = (ChoiceBox) rotorsInitialPosHBox.getChildren().get(i);
            rotorsStartingPositions = rotorsStartingPositions.concat((String) cb.getSelectionModel().getSelectedItem());
        }
        List<MappingPair<String,String>> plugMapping = new ArrayList<>();
        for (int i = 0; i < plugBoardConnectionsLeft.getItems().size(); i++) {
            String ep1 = plugBoardConnectionsLeft.getItems().get(i);
            String ep2 = plugBoardConnectionsRight.getItems().get(i);
            MappingPair<String,String> plug = new MappingPair<>(ep1,ep2);
            plugMapping.add(plug);
        }

        parentController.handleManuelSetMachinePressed(reflectorId, rotorIdsList, rotorsStartingPositions, plugMapping);
        Platform.runLater(() -> {
            if (DataService.getIsAnimationOn().get()) {
                rotatePicture();
            }
        });
    }

    @FXML
    void onSetRandomChoiceButtonAction(ActionEvent event) {
        parentController.handleRandomSetMachinePressed();
        Platform.runLater(() -> {
            if (DataService.getIsAnimationOn().get()) {
//                fadeAndChangePic();
                movePic();
//                fadeAndChangePic();
            }
        });
    }

    public void addListenerOnClickSetRandomButton(ChangeListener listener) {
        //Triggers Twice (down click + release) - expected behaviours, but not wanted
//        setRandomChoiceButton.pressedProperty().addListener(listener);
        //Doesnt Trigger for unknown reason
//        setRandomChoiceButton.onActionProperty().addListener(listener);
//        setRandomChoiceButton.onMouseClickedProperty().addListener(listener);

    }

    public void onClearSelectionButton(ActionEvent actionEvent) {
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

    private void rotatePicture() {
        RotateTransition rt = new RotateTransition(Duration.millis(3000), setMachineImageView);
        rt.setByAngle(360);
        rt.setCycleCount(1);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
    }

    private void fadeAndChangePic(){
        Platform.runLater(() -> {
            if (DataService.getIsAnimationOn().get()) {
                FadeTransition fadeTransitionDown = new FadeTransition(Duration.seconds(0.3), setMachineImageView);
                fadeTransitionDown.setFromValue(1.0);
                fadeTransitionDown.setToValue(0.0);
                fadeTransitionDown.play();

                Random ran = new Random();
                int randomIndex = ran.nextInt(ResourceLocationService.getImageListForAnimation().size());
                setMachineImageView.setImage(new Image(ResourceLocationService.getImageListForAnimation().get(randomIndex)));


                FadeTransition fadeTransitionUp = new FadeTransition(Duration.seconds(0.3), setMachineImageView);
                fadeTransitionUp.setFromValue(0.0);
                fadeTransitionUp.setToValue(1.0);
                fadeTransitionUp.play();
            }
        });
    }

    public void movePic() {

        Random ran = new Random();
        int randomIndex = ran.nextInt(ResourceLocationService.getImageListForAnimation().size());

        Path path = new Path();
        path.getElements().addAll(new MoveTo(50, origY), new HLineTo(2000));
        path.getElements().addAll(new MoveTo(origX, origY), new HLineTo(50));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(2000));
        pathTransition.setNode(setMachineImageView);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
        setMachineImageView.setImage(new Image(ResourceLocationService.getImageListForAnimation().get(randomIndex)));
    }
}
