package controller;

import app.GuiApplication;
import dto.AllyTeamData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    public GridPane contestDataGrid;
    public ContestDataController contestDataGridController;
    public GridPane allyCompetitionControls;

    public FlowPane teamsFlowPane;
    public CompetitionControlsController allyCompetitionControlsController;



    AppController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getCurrentContestRoomsStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                contestWordLabel.setText(newValue.getWordToDecrypt());
                contestDataGridController.setParentController(this);
                Platform.runLater(()->{
                    contestDataGridController.setData(newValue);
                });
            }
        });

//        DataService.
        if(contestDataGridController != null){
            contestDataGridController.setParentController(this);
        }
        if(allyCompetitionControlsController != null){
            allyCompetitionControlsController.setParentController(this);
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

    public void setParentController(AppController appController) {
        this.parentController = appController;
    }
    public GridPane getRootComponent() {
        return  rootGridPane;
    }

    public void showMessage(String message){
        parentController.showMessage(message);
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
}
