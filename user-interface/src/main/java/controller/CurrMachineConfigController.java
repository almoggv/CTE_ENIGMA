package src.main.java.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import lombok.Getter;
import main.java.dto.MachineState;
import main.java.generictype.MappingPair;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class CurrMachineConfigController implements Initializable {
    public Label machineConfigurationLabel;
    @Getter @FXML MachinePageController machinePageController;
    @Getter @FXML EncryptPageController encryptPageController;
    @Getter @FXML BruteForcePageController bruteForcePageController;
    @Getter @FXML MachineDetailsController machineDetailsController;
    @Getter @FXML private GridPane rootGridPane;
    @FXML private HBox rotorsHbox;
    @FXML private Button rotorButton2;
    @FXML private Button rotorButton1;
    @FXML private HBox plugBoardHbox;
    @FXML private Button plugBoardConnection2;
    @FXML private Button plugBoardConnection1;
    @FXML private Button reflectorButton;
    @FXML private HBox reflectorHbox;
    @FXML public Label machineNotConfiguredLabel;

    @Getter private SimpleBooleanProperty isAnimationOn = new SimpleBooleanProperty(true);

    public void setParentController(MachinePageController parentController){
        this.machinePageController = parentController;
    }

    public void setParentController(EncryptPageController parentController){
        this.encryptPageController = parentController;
    }

    public void setParentController(MachineDetailsController machineDetailsController){
        this.machineDetailsController = machineDetailsController;
    }

    public void setParentController(BruteForcePageController bruteForcePageController) {
        this.bruteForcePageController = bruteForcePageController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void changeHeaderToOriginal(){
        this.machineConfigurationLabel.setText("Original machine configuration");
    }
    public void bindToData(SimpleObjectProperty<MachineState> dataProperty){
        dataProperty.addListener(new ChangeListener<MachineState>() {
            @Override
            public void changed(ObservableValue<? extends MachineState> observable, MachineState oldValue, MachineState newValue) {
                showCurrConfiguration(newValue);
            }
        });
    }

    public void showCurrConfiguration(MachineState machineState) {
        if(machineState != null){
            machineNotConfiguredLabel.setVisible(false);
            setRotorsHbox(machineState);
            setPlugBoardHbox(machineState);
            setReflectorLabel(machineState);
        }
        else{
            machineNotConfiguredLabel.setVisible(true);
            clearConfiguration();
        }
    }

    private void clearConfiguration() {
        rotorsHbox.getChildren().clear();
        reflectorHbox.getChildren().clear();
        plugBoardHbox.getChildren().clear();
    }

    private void setRotorsHbox(MachineState machineState) {
        Platform.runLater(() -> {
            if (isAnimationOn.get()) {
                fadeOutComponent(rotorsHbox);
            }
            rotorsHbox.getChildren().clear();
            int numOfRotors = machineState.getRotorIds().size();
            List<Integer> rotorIds = machineState.getRotorIds();
            for (int i = numOfRotors - 1; i >= 0; i--) {
                int notchDistance = machineState.getNotchDistancesFromHead().get(i);
                String rotorButtonMsg = (rotorIds.get(i)) + " (" + notchDistance + ")";
                Button rotor = new Button(new String(String.valueOf(rotorButtonMsg)));
                rotorsHbox.getChildren().add(rotor);
            }
            if (isAnimationOn.get()) {
                fadeInComponent(rotorsHbox);
            }
        });
    }

    private void fadeInComponent(HBox Hbox) {
        FadeTransition fadeTransitionUp = new FadeTransition(Duration.seconds(0.3), Hbox);
        fadeTransitionUp.setFromValue(0.0);
        fadeTransitionUp.setToValue(1.0);
        fadeTransitionUp.play();
    }

    private void fadeOutComponent(HBox Hbox) {
        FadeTransition fadeTransitionDown = new FadeTransition(Duration.seconds(0.3), Hbox);
        fadeTransitionDown.setFromValue(1.0);
        fadeTransitionDown.setToValue(0.0);
        fadeTransitionDown.play();
    }

    private void setPlugBoardHbox(MachineState machineState) {
        Platform.runLater(() -> {
            if (isAnimationOn.get()) {
                fadeOutComponent(plugBoardHbox);
            }
            plugBoardHbox.getChildren().clear();
            int numOfPlugs = machineState.getPlugMapping().size();
            List<MappingPair<String, String>> plugMapping = machineState.getPlugMapping();
            for (MappingPair<String, String> mappingPair : plugMapping) {
                String plugString = mappingPair.getLeft() + "|" + mappingPair.getRight();
                Button plugButton = new Button(plugString);
                plugBoardHbox.getChildren().add(plugButton);
            }
            if (isAnimationOn.get()) {
                fadeInComponent(plugBoardHbox);
            }
        });
    }

    private void setReflectorLabel(MachineState machineState){
        Platform.runLater(() -> {
            if (isAnimationOn.get()) {
                fadeOutComponent(reflectorHbox);
            }
            reflectorHbox.getChildren().clear();
            Button refButton = new Button(machineState.getReflectorId().getName());
            reflectorHbox.getChildren().add(refButton);
            if (isAnimationOn.get()) {
                fadeInComponent(reflectorHbox);
            }
        });
    }
}
