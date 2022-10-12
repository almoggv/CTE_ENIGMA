package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.DataService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class ContestPageController implements Initializable {

    private static final Logger log = Logger.getLogger(ContestPageController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(ContestPageController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + ContestPageController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + ContestPageController.class.getSimpleName() ) ;
        }
    }

    @Setter @Getter @FXML private AppController parentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;

    @Getter @FXML private EncryptionController encryptionComponentController;
    @FXML
    private ScrollPane dmResultsScrollPane;
    @FXML
    private FlowPane dmResultsFlowPane;
    @FXML
    private Button logoutButton;

    public GridPane rootGridPane;
    @FXML public GridPane currMachineConfigComponent;
    @FXML
    void onLogoutButtonAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
    }

    public void showMessage(String message) {
        parentController.showMessage(message);
    }

    public void bindComponent(CurrMachineConfigController controller) {
        this.currMachineConfigComponentController = controller;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
    }

    public GridPane getRootComponent() {
        return rootGridPane;
    }
}
