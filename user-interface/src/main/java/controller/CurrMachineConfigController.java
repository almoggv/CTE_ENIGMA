package src.main.java.controller;

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
import lombok.Setter;
import main.java.dto.InventoryInfo;
import main.java.dto.MachineState;
import main.java.generictype.MappingPair;
import org.controlsfx.control.ToggleSwitch;
import src.main.java.service.DataService;



import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class CurrMachineConfigController implements Initializable {


    public Label machineConfigurationLabel;
    @Getter @FXML MachinePageController machinePageController;
    @Getter @FXML EncryptPageController encryptPageController;
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

    public void setParentController(MachinePageController parentController){
        this.machinePageController = parentController;
    }

    public void setParentController(EncryptPageController parentController){
        this.encryptPageController = parentController;
    }

    public void setParentController(MachineDetailsController machineDetailsController){
        this.machineDetailsController = machineDetailsController;
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
        machineNotConfiguredLabel.setVisible(false);
        setRotorsHbox(machineState);
        setPlugBoardHbox(machineState);
        setReflectorLabel(machineState);
    }

    private void setRotorsHbox(MachineState machineState) {
        rotorsHbox.getChildren().clear();
        int numOfRotors = machineState.getRotorIds().size();
        List<Integer> rotorIds = machineState.getRotorIds();
        for (int i = 0; i < numOfRotors; i++) {
            int notchDistance = machineState.getNotchDistancesFromHead().get(i);
            String rotorButtonMsg = i + " (" + notchDistance + ")";
            Button rotor = new Button(new String(String.valueOf(rotorButtonMsg)));
            rotorsHbox.getChildren().add(rotor);
        }
    }

    private void setPlugBoardHbox(MachineState machineState) {
        plugBoardHbox.getChildren().clear();
        int numOfPlugs = machineState.getPlugMapping().size();
        List<MappingPair<String, String>> plugMapping = machineState.getPlugMapping();
        for (MappingPair<String,String> mappingPair : plugMapping) {
            String plugString = mappingPair.getLeft() + "|" + mappingPair.getRight();
            Button plugButton = new Button(plugString);
            plugBoardHbox.getChildren().add(plugButton);
        }
    }

    private void setReflectorLabel(MachineState machineState){
        reflectorHbox.getChildren().clear();
        Button refButton = new Button(machineState.getReflectorId().getName());
        reflectorHbox.getChildren().add(refButton);
    }
}
