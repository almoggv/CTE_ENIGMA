package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import src.main.java.service.DataService;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class MachinePageController implements Initializable {

    @Setter @Getter @FXML private AppController parentController;
    @Getter @FXML private SetMachineConfigController setMachineConfigurationComponentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;
    
    @FXML public GridPane setMachineConfigurationComponent;
    @FXML public GridPane currMachineConfigComponent;
    @FXML private GridPane rootGridPane;
    @FXML private SplitPane bottomSplitPane;
    @FXML private AnchorPane leftAnchorOfBottom;
    @FXML private ScrollPane scrollOfLeftBottomAnchor;
    @FXML private AnchorPane rightAnchorOfBottom;
    @FXML private ScrollPane scrollOfRightBottomAnchor;
    @FXML private GridPane machineDetailsComponent;
    @FXML private MachineDetailsController machineDetailsComponentController;

    @Setter @Getter private SimpleBooleanProperty isMachineConfigured;

    public Boolean isFirstTimeConfigure = true;

    @Getter @Setter private MachineHandler machineHandler;
    @Getter private ActionListener onSetMachineConfigurationPressed;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(setMachineConfigurationComponentController != null){
            setMachineConfigurationComponentController.setParentController(this);
            setMachineConfigurationComponentController.addOnSetButtonListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    System.out.println("in MachinePageController, onSetRandPress!");
                    try {
                        machineHandler.assembleMachine();
                        if (isFirstTimeConfigure) {
                            DataService.setOriginalMachineState(machineHandler.getMachineState().get());
                            DataService.setIsOriginalMachineStateConfigured(true);
                            isFirstTimeConfigure = false;
                        }
                        DataService.setCurrentMachineState(machineHandler.getMachineState().get());
                        DataService.setIsCurrentMachineStateConfigured(true);
                        currMachineConfigComponentController.showCurrConfiguration();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

        }
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
        }
        if(machineDetailsComponentController != null){
            machineDetailsComponentController.setParentController(this);
        }
    }

    public MachinePageController(){
        isMachineConfigured = new SimpleBooleanProperty(false);
    }

    public Parent getRootComponent() {
        return rootGridPane;
    }
}
