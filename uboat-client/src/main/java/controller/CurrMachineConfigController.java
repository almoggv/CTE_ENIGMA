package controller;

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
import lombok.Getter;
import dto.MachineState;
import generictype.MappingPair;
import service.DataService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CurrMachineConfigController implements Initializable {

    @Getter @FXML MachinePageController machinePageController;
    @Getter @FXML MachineDetailsController machineDetailsController;

    @Getter @FXML private GridPane rootGridPane;
    @FXML Label machineConfigurationLabel;
    @FXML HBox rotorsHbox;
    @FXML Button rotorButton2;
    @FXML Button rotorButton1;
    @FXML HBox plugBoardHbox;
    @FXML Button plugBoardConnection2;
    @FXML Button plugBoardConnection1;
    @FXML Button reflectorButton;
    @FXML HBox reflectorHbox;
    @FXML Label machineNotConfiguredLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ignore
    }

    public void setParentController(MachinePageController parentController){
        this.machinePageController = parentController;
    }

    public void setParentController(MachineDetailsController parentController){
        this.machineDetailsController = parentController;
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
            rotorsHbox.getChildren().clear();
            int numOfRotors = machineState.getRotorIds().size();
            List<Integer> rotorIds = machineState.getRotorIds();
            for (int i = numOfRotors - 1; i >= 0; i--) {
                int notchDistance = machineState.getNotchDistancesFromHead().get(i);
                String rotorButtonMsg = (rotorIds.get(i)) + " (" + notchDistance + ")";
                Button rotor = new Button(new String(String.valueOf(rotorButtonMsg)));
                rotorsHbox.getChildren().add(rotor);
            }
        });
    }

    private void setPlugBoardHbox(MachineState machineState) {
        Platform.runLater(() -> {
            plugBoardHbox.getChildren().clear();
            int numOfPlugs = machineState.getPlugMapping().size();
            List<MappingPair<String, String>> plugMapping = machineState.getPlugMapping();
            for (MappingPair<String, String> mappingPair : plugMapping) {
                String plugString = mappingPair.getLeft() + "|" + mappingPair.getRight();
                Button plugButton = new Button(plugString);
                plugBoardHbox.getChildren().add(plugButton);
            }
        });
    }

    private void setReflectorLabel(MachineState machineState){
        Platform.runLater(() -> {
            reflectorHbox.getChildren().clear();
            Button refButton = new Button(machineState.getReflectorId().getName());
            reflectorHbox.getChildren().add(refButton);
        });
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
}
