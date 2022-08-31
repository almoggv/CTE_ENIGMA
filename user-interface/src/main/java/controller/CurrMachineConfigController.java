package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import main.java.generictype.MappingPair;
import src.main.java.service.DateService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class CurrMachineConfigController implements Initializable {


    @Getter @Setter
    @FXML MachinePageController parentController;

    @FXML private GridPane rootGridPane;
    @FXML private HBox rotorsHbox;
    @FXML private Button rotorButton2;
    @FXML private Button rotorButton1;
    @FXML private HBox plugBoardHbox;
    @FXML private Button plugBoardConnection2;
    @FXML private Button plugBoardConnection1;
    @FXML private Button reflectorButton;

    @FXML
    private HBox reflectorHbox;

    @FXML
    public Label machineNotConfiguredLabel;
    public CurrMachineConfigController(){
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(DateService.getIsCurrentMachineStateConfigured()){
            showCurrConfiguration();
        }
    }

    public void showCurrConfiguration() {
        machineNotConfiguredLabel.setVisible(false);
        setRotorsHbox();
        setPlugBoardHbox();
        setReflectorLabel();
    }

    private void setRotorsHbox() {
        rotorsHbox.getChildren().clear();
        int numOfRotors = DateService.getCurrentMachineState().getRotorIds().size();
        List<Integer> rotorIds = DateService.getCurrentMachineState().getRotorIds();
        for (int i = 0; i < numOfRotors; i++) {
            Button rotor = new Button(new String(String.valueOf(rotorIds.get(i))));
            rotorsHbox.getChildren().add(rotor);
        }
    }

    private void setPlugBoardHbox() {
        plugBoardHbox.getChildren().clear();
        int numOfPlugs = DateService.getCurrentMachineState().getPlugMapping().size();
        List<MappingPair<String, String>> plugMapping = DateService.getCurrentMachineState().getPlugMapping();
        for (MappingPair<String,String> mappingPair : plugMapping) {
            String plugString = mappingPair.getLeft() + "|" + mappingPair.getRight();
            Button plugButton = new Button(plugString);
            plugBoardHbox.getChildren().add(plugButton);
        }
    }

    private void setReflectorLabel(){
        reflectorHbox.getChildren().clear();
        Button refButton = new Button(DateService.getCurrentMachineState().getReflectorId().getName());
        reflectorHbox.getChildren().add(refButton);
    }
}
