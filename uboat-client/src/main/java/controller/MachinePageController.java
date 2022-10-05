package controller;

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
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import service.DataService;
import service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    @Setter @Getter private SimpleBooleanProperty isMachineConfigured = new SimpleBooleanProperty(false);;
    @Setter @Getter private Boolean isFirstTimeConfigure = true;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(setMachineConfigurationComponentController != null){
            setMachineConfigurationComponentController.setParentController(this);
        }
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        if(machineDetailsComponentController != null){
            machineDetailsComponentController.setParentController(this);
        }
    }

    public void showMessage(String message) {
        parentController.showMessage(message);
    }

    public void handleManuelSetMachinePressed(MachineState userMachineSetupChoices) {
//        DataService.getOriginalMachineStateProperty().setValue(machineHandler.getInitialMachineState().get());
//        DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
//        log.info("MachinePageController - CurrMachine State =" + machineHandler.getMachineState().get());

        throw new NotImplementedException();
    }

    public void handleRandomSetMachinePressed() {
//        send http request to set machine state randomly

//        send http requst to get machine states
//        DataService.getOriginalMachineStateProperty().setValue(machineHandler.getInitialMachineState().get());
//        DataService.getCurrentMachineStateProperty().setValue(machineHandler.getMachineState().get());
//        log.info("CurrMachine State =" + machineHandler.getMachineState().get());
        throw new NotImplementedException();
    }

    public GridPane getRootComponent() {
        return rootGridPane;
    }

    public void bindComponent(CurrMachineConfigController controller){
        this.currMachineConfigComponentController = controller;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
    }




}
