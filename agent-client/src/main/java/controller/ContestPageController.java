package controller;

import app.GuiApplication;
import dto.AgentData;
import dto.ContestRoom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.DataService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

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

    public GridPane rootGridPane;
    public Label contestWordLabel;
    public ContestDataController contestDataGridController;
    public FlowPane agentsDataFlowPane;
    @FXML
    private FlowPane contestDataFlowPane;

    AppController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getCurrentContestRoomsStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                createContestsDataComponents(newValue);
            }
        });

        DataService.getAgentsListStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                createAgentsDataComponents(newValue);
            }
        });


    }

    public void setParentController(AppController appController) {
        this.parentController = appController;
    }
    public GridPane getRootComponent() {
        return  rootGridPane;
    }
    public void showMessage(String message){
        parentController.showMessage(message);
    }

    private void createContestsDataComponents(ContestRoom contestRoom) {
        Platform.runLater(() -> {
            try {
                contestDataFlowPane.getChildren().clear();
                URL contestRoomURL = GuiApplication.class.getResource(PropertiesService.getContestDataFxmlPath());
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(contestRoomURL);
                Parent decodedCandidate = fxmlLoader.load(contestRoomURL.openStream());
                ContestDataController contestDataController = fxmlLoader.getController();
                contestDataController.setData(contestRoom);

                contestDataController.setParentController(this);

                contestDataFlowPane.getChildren().add(decodedCandidate);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createAgentsDataComponents(List<AgentData> agentDataList) {
        Platform.runLater(() -> {
            try {
                agentsDataFlowPane.getChildren().clear();
                for (AgentData agentData : agentDataList) {
                    URL agentDataURL = GuiApplication.class.getResource(PropertiesService.getAgentDataFxmlPath());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(agentDataURL);
                    Parent agentComponent = fxmlLoader.load(agentDataURL.openStream());
                    AgentDataController agentDataController = fxmlLoader.getController();
                    agentDataController.setData(agentData);

//                    agentsDataFlowPane.setParentController(this);

                    agentsDataFlowPane.getChildren().add(agentComponent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
