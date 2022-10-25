package controller;

import app.GuiApplication;
import dto.AllyTeamData;
import dto.ContestRoom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
import java.util.List;
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

    public GridPane encryptionComponent;

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

    public FlowPane teamsFlowPane;

    @FXML
    void onLogoutButtonAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        if(encryptionComponentController != null){
            encryptionComponentController.setParentController(this);
        }
        DataService.getCurrentTeamsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                createTeamDataComponents(newValue);
            }
        });

        DataService.getIsContestStartedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == true){
                Platform.runLater(()->{
                    showMessage("Contest starting!");
                });
            }
        });
    }

    private void createTeamDataComponents(List<AllyTeamData> allyTeamDataList) {
        Platform.runLater(() -> {
            try {
                teamsFlowPane.getChildren().clear();
                for (AllyTeamData allyTeamData : allyTeamDataList) {
                    URL teamComponentURL = GuiApplication.class.getResource(PropertiesService.getAllyTeamComponentFxmlPath());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(teamComponentURL);
                    Parent team = fxmlLoader.load(teamComponentURL.openStream());
                    AllyTeamController controller = fxmlLoader.getController();
                    controller.setData(allyTeamData);

//                    controller.setParentController(this);
                    teamsFlowPane.getChildren().add(team);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
