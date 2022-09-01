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
import src.main.java.service.DateService;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class MachinePageController implements Initializable {

    @Setter @Getter
    @FXML private AppController parentController;
    public GridPane setMachineConfigurationComponent;
    @Getter
    @FXML private SetMachineConfigController setMachineConfigurationComponentController;
    public GridPane currentMachineConfigurationComponent;
    @Getter
    @FXML private CurrMachineConfigController currentMachineConfigurationComponentController;

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
                        if(isFirstTimeConfigure){
                            DateService.setOriginalMachineState(machineHandler.getMachineState().get());
                            DateService.setIsOriginalMachineStateConfigured(true);
                            isFirstTimeConfigure = false;
                        }
                        DateService.setCurrentMachineState(machineHandler.getMachineState().get());
                        DateService.setIsCurrentMachineStateConfigured(true);
                        currentMachineConfigurationComponentController.showCurrConfiguration();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

        }
        if(currentMachineConfigurationComponentController != null){
            currentMachineConfigurationComponentController.setParentController(this);
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

    public void setCurrentMachineConfigurationComponentController(CurrMachineConfigController currentMachineConfigurationComponentController) {
        this.currentMachineConfigurationComponentController = currentMachineConfigurationComponentController;
        currentMachineConfigurationComponentController.setParentController(this);
    }

    public void setSetMachineConfigurationComponentController(SetMachineConfigController setMachineConfigurationComponentController) {
        this.setMachineConfigurationComponentController = setMachineConfigurationComponentController;
        setMachineConfigurationComponentController.setParentController(this);
    }
}
