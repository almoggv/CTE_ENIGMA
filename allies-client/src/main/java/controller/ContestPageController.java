package controller;

import app.GuiApplication;
import dto.AllyTeamData;
import dto.EncryptionCandidate;
import enums.GameStatus;
import generictype.MappingPair;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.Getter;
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

    AppController parentController;
    public CompetitionControlsController allyCompetitionControlsController;
    public ContestDataController contestDataGridController;

    @FXML private GridPane rootGridPane;
    @FXML private Label contestWordLabel;
    @FXML private GridPane contestDataGrid;
    @FXML private GridPane allyCompetitionControls;
    @FXML private FlowPane teamsFlowPane;
    @FXML private FlowPane dmResultsFlowPane;
    @FXML private Label statusValueLabel;
    @FXML private Label progressPrecentageValueLabel;
    @FXML @Getter private ProgressBar progressBar;
    @FXML private Button clearScreenButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(contestDataGridController != null){
            contestDataGridController.setParentController(this);
        }
        if(allyCompetitionControlsController != null){
            allyCompetitionControlsController.setParentController(this);
        }

        DataService.getCurrentContestRoomStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                contestDataGridController.setParentController(this);
                Platform.runLater(()->{
                    contestDataGrid.setVisible(true);
                    contestWordLabel.setText(newValue.getWordToDecrypt());
                    contestDataGridController.setData(newValue);
                });
                return;
            }
            boolean isLoggedIn = parentController.headerComponentController.getIsLoggedInProperty().get();
            if(isLoggedIn && newValue == null ){
                Platform.runLater(()->{
                    clearContestPageContent();
                });
            }

        });
        DataService.getCurrentTeamsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                createTeamDataComponents(newValue);
            }
            else{
                Platform.runLater(()->{
                    teamsFlowPane.getChildren().clear();
                });
            }
        });
        DataService.getGameStatusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && (newValue.getGameState() == GameStatus.READY || newValue.getGameState() == GameStatus.IN_PROGRESS)){
                Platform.runLater(()->{
                    showMessage("Contest starting!");
                    DataService.startPullingProgress();
                    DataService.startPullingCandidates();
                });
            }
            if(newValue!=null){
                Platform.runLater(()->{
                    statusValueLabel.setText(newValue.getGameState().name());
                });
            }
            else if (newValue != null && newValue.getGameState() == GameStatus.DONE) {
                Platform.runLater(()->{
                    showMessage("Contest done! Winner is: " + newValue.getWinner());
                    DataService.getDmProgressProperty().setValue(new MappingPair<Long,Long>(1L,1L));
                    DataService.getCurrentContestRoomStateProperty().setValue(null);
                    DataService.getLastCandidatesProperty().setValue(null);
                    clearScreenButton.setDisable(false);

                });
            }
        });

        DataService.getLastCandidatesProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.isEmpty()){
                createCandidateTile(newValue.get(newValue.size()-1));
            }
        });
        DataService.getDmProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.getRight() == 0){
                return;
            }
            double progressBarValue = (double)newValue.getLeft() / (double)newValue.getRight();
            int progressPercentage = (int)Math.ceil(progressBarValue * 100);
            Platform.runLater(()->{
                progressBar.setProgress(progressBarValue);
                progressPrecentageValueLabel.setText(String.valueOf(progressPercentage));
            });
        });

        clearScreenButton.setDisable(true);
    }

    @FXML
    void onClearScreenButtonClick(ActionEvent event) {
        clearContestPageContent();
    }

    public void clearContestPageContent(){
        DataService.getCurrentTeamsProperty().setValue(null);
        DataService.getCurrentContestRoomStateProperty().setValue(null);
        DataService.getGameStatusProperty().setValue(null);
        DataService.getLastCandidatesProperty().setValue(null);
        DataService.getDmProgressProperty().setValue(null);
        DataService.sendGotWin();
        Platform.runLater(()->{
            contestWordLabel.setText(null);
            contestDataGrid.setVisible(false);
            dmResultsFlowPane.getChildren().clear();
            teamsFlowPane.getChildren().clear();
            parentController.changeSceneToDashboard();
            progressBar.setProgress(0);
            statusValueLabel.setText("");
            progressPrecentageValueLabel.setText("0");
            clearScreenButton.setDisable(true);
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

    private void createCandidatesComponents(List<EncryptionCandidate> candidateList) {
        Platform.runLater(()->{
            dmResultsFlowPane.getChildren().clear();
        });
        for (EncryptionCandidate candidate : candidateList ) {
            createCandidateTile(candidate);
        }
    }


    private void createCandidateTile(EncryptionCandidate candidate) {
        Platform.runLater(() -> {
            try {
                URL decodedCandidateURL = GuiApplication.class.getResource(PropertiesService.getCandidateDataFxmlPath());
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(decodedCandidateURL);
                Parent decodedCandidate = fxmlLoader.load(decodedCandidateURL.openStream());
                CandidateController decodedCandidateController = fxmlLoader.getController();
                decodedCandidateController.setData(candidate);

                dmResultsFlowPane.getChildren().add(decodedCandidate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
