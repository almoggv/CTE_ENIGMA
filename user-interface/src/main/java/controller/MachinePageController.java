package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.enums.ReflectorsId;
import main.java.generictype.MappingPair;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import src.main.java.service.DataService;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class MachinePageController implements Initializable {

    private static final Logger log = Logger.getLogger(MachinePageController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(MachinePageController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + MachinePageController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + MachinePageController.class.getSimpleName() ) ;
        }
    }

    @Setter @Getter @FXML private AppController parentController;
    @Getter @FXML private SetMachineConfigController setMachineConfigurationComponentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;
    @FXML private MachineDetailsController machineDetailsComponentController;

    @FXML public GridPane setMachineConfigurationComponent;
    @FXML public GridPane currMachineConfigComponent;
    @FXML private GridPane rootGridPane;
    @FXML private SplitPane bottomSplitPane;
    @FXML private AnchorPane leftAnchorOfBottom;
    @FXML private ScrollPane scrollOfLeftBottomAnchor;
    @FXML private AnchorPane rightAnchorOfBottom;
    @FXML private ScrollPane scrollOfRightBottomAnchor;
    @FXML private GridPane machineDetailsComponent;

    @Setter @Getter private SimpleBooleanProperty isMachineConfigured;

    public Boolean isFirstTimeConfigure = true;

    @Getter @Setter private MachineHandler machineHandler;
    @Getter private ActionListener onSetMachineConfigurationPressed;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(setMachineConfigurationComponentController != null){
            setMachineConfigurationComponentController.setParentController(this);
            setMachineConfigurationComponentController.addListenerOnClickSetRandomButton(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    setMachineConfigRandomlyPressed();
                }
            });
        }
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        if(machineDetailsComponentController != null){
            machineDetailsComponentController.setParentController(this);
        }
    }

    public MachinePageController(){
        isMachineConfigured = new SimpleBooleanProperty(false);
    }

    public GridPane getRootComponent() {
        return rootGridPane;
    }

    public void bindComponent(CurrMachineConfigController controller){
        this.currMachineConfigComponentController = controller;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
    }

    private void setMachineConfigRandomlyPressed(){
        try{
            machineHandler.assembleMachine();
            DataService.getOriginalMachineStateProperty().setValue(machineHandler.getInitialMachineState().get());
            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
            DataService.getEncryptionInfoHistoryProperty().setValue(null);
            DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
            log.info("CurrMachine State =" + machineHandler.getMachineState().get());
        }
        catch (Exception e){
            //TODO: log here
            throw new RuntimeException("Failed to assemble machine randomly : " + e.getMessage());
        }
    }

    public void handleManuelSetMachinePressed(ReflectorsId reflectorId, List<Integer> rotorIdsList,
                                              String rotorsStartingPositions,
                                              List<MappingPair<String,String>> plugMapping ) {
        try{
            machineHandler.assembleMachine(reflectorId, rotorIdsList, rotorsStartingPositions, plugMapping);
            DataService.getOriginalMachineStateProperty().setValue(machineHandler.getInitialMachineState().get());
            DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
            DataService.getEncryptionInfoHistoryProperty().setValue(null);
            DataService.getEncryptionInfoHistoryProperty().setValue(machineHandler.getMachineStatisticsHistory());
            System.out.println("CurrMachine State =" + machineHandler.getMachineState().get());
        }
        catch (Exception e){
            //TODO: log here
            throw new RuntimeException("Failed to assemble machine randomly : " + e.getMessage());
        }
    }

    public void showMessage(String message) {
        parentController.showMessage(message);
    }

    public void handleRandomSetMachinePressed() {
        setMachineConfigRandomlyPressed();
    }
}
