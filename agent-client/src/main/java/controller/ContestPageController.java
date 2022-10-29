package controller;

import adapter.ListenerAdapter;
import app.GuiApplication;
import dto.AgentData;
import dto.ContestRoomData;
import dto.DecryptionCandidateInfo;
import dto.EncryptionCandidate;
import enums.GameStatus;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import manager.AgentClientDM;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.DataService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    @FXML private GridPane rootGridPane;
    @FXML private Label contestWordLabel;
    @FXML private ContestDataController contestDataGridController;
    @FXML private FlowPane agentsDataFlowPane;
    @FXML private ScrollPane dmResultsScrollPane;
    @FXML private FlowPane contestDataFlowPane;
    @FXML private FlowPane dmResultsFlowPane;
    @FXML private Label statusValueLabel;
    @FXML private Label progressPrecentageValueLabel;
    @FXML private ProgressBar progressBar;

    AppController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataService.getCurrentContestRoomStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                createContestsDataComponents(newValue);
                if(newValue.getName()!=null){
                    statusValueLabel.setText(newValue.getName());
                }
            }
            else{
                Platform.runLater(()->{
                    contestDataFlowPane.getChildren().clear();
                    dmResultsFlowPane.getChildren().clear();
                });
            }
        });

        DataService.getGameStatusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.getGameState() == GameStatus.READY){
                Platform.runLater(()->{
                    showMessage("Contest starting!");
                });
            }
            else if (newValue != null && newValue.getGameState() == GameStatus.DONE) {
                Platform.runLater(()->{
                    showMessage("Contest done! Winner is: " + newValue.getWinner());
                });
            }
        });

        DataService.getLastCandidatesProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                createCandidatesComponents(newValue);
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

    private void createContestsDataComponents(ContestRoomData contestRoom) {
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

                    agentsDataFlowPane.getChildren().add(agentComponent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onSendRedButton(ActionEvent actionEvent) {
//        EncryptionCandidate encryptionCandidate = new EncryptionCandidate();
//        encryptionCandidate.setCandidate("atom");
//        encryptionCandidate.setAllyTeamName("ally team");
//
//        List<EncryptionCandidate> candidateList = new ArrayList<>();
//        candidateList.add(encryptionCandidate);
        DataService.sendCandidates();


    }

    private void createCandidatesComponents(List<EncryptionCandidate> candidateList) {
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

    public void connectToDm(AgentClientDM agentClientDM){
        if(agentClientDM == null){
            return;
        }
        ListenerAdapter listenerAdapter = agentClientDM.getListenerAdapter();
        listenerAdapter.getProgressProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null  || newValue.getLeft() == null || newValue.getRight() == null || newValue.getRight() <= 0){
                return;
            }

            double progressValue = ((double)newValue.getLeft() / (double)newValue.getRight());
            int progressPercentage = (int) (progressValue * 100);
            progressBar.setProgress(progressValue);
            progressPrecentageValueLabel.setText(String.valueOf(progressPercentage));
        });
        listenerAdapter.getDecryptionCandidatesProperty().addListener((observable, oldListValue, newListValue) -> {
            if(newListValue == null || newListValue.isEmpty()){
                return;
            }
            DecryptionCandidateInfo newestCandidate = newListValue.get(newListValue.size()-1);
            EncryptionCandidate newCandidate = new EncryptionCandidate(newestCandidate.getOutput(),parentController.getLoginComponentController().getAllyTeamName(),newestCandidate.getInitialState());
            createCandidateTile(newCandidate);
        });


    }
}
