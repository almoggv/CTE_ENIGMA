package controller;

import component.MachineHandler;
import dto.DecryptionCandidateInfo;
import dto.DecryptionWorkPayload;
import dto.EncryptionCandidate;
import dto.MachineState;
import enums.GameStatus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import manager.AgentClientDM;
import manager.impl.AgentClientDMImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.DataService;
import service.DecryptionWorkPayloadParserService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private static final Logger log = Logger.getLogger(AppController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AppController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AppController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AppController.class.getSimpleName());
        }
    }

    private Thread agentClientDmThread;
    @Getter private AgentClientDM agentClientDM;

    @FXML @Getter HeaderController headerComponentController;
    @FXML @Getter LoginController loginComponentController;
    @FXML private ContestPageController contestPageController;

    @Setter @Getter private Stage primaryStage;
    @FXML private BorderPane appBorderPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;
    @FXML private GridPane imageGrid;
    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;
    @FXML GridPane headerComponent;
    @FXML GridPane loginComponent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (headerComponentController != null) {
            headerComponentController.setParentController(this);
        }
        if(loginComponentController != null){
            loginComponentController.setParentController(this);
        }
        FXMLLoader fxmlLoader;

//      //Load Contest Page
        URL contestPageResource = AppController.class.getResource(PropertiesService.getContestPageTemplateFxmlPath());
        log.info("AppController - found Url of machine component:" + contestPageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(contestPageResource);
        try {
            Parent contestComponent = fxmlLoader.load(contestPageResource.openStream());
        } catch (IOException e) {
            log.error("Failed to load contestPageResource:" + contestPageResource.toString() + "Exception throws: " + e.getMessage());
            throw new NullPointerException("Failed to load contestPageResource from resource, contestPageResource is null");
        }
        contestPageController = fxmlLoader.getController();
        contestPageController.setParentController(this);

        headerWrapScrollPane.setContent(headerComponentController.getRootComponent());
        loginComponentController.getIsLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==true){
                this.changeSceneToContest();
            }
            else{
                this.changeSceneToLogin();
            }
        });
        loginComponentController.getIsLoggedInProperty().bindBidirectional(headerComponentController.getIsLoggedInProperty());;
        log.info("AppController - app initialized");

//        setupDataServiceConnections();
            //todo: finish - moved here instead of ally
        DataService.getGameStatusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.getGameState() == GameStatus.IN_PROGRESS || newValue.getGameState() == GameStatus.READY){
                MachineHandler machineHandler = DataService.fetchMachineHandler();
                agentClientDM = new AgentClientDMImpl(machineHandler, loginComponentController.getTaskSize(), loginComponentController.getNumberOfThreads());
                contestPageController.connectToDm(agentClientDM);
                agentClientDM.getListenerAdapter().getDecryptionCandidatesProperty().addListener((observable1, oldCandidatesList, newCandidatesList) -> {
                    if(newCandidatesList == null || newCandidatesList.isEmpty() ){
                        return;
                    }
                    EncryptionCandidate encryptionCandidate = new EncryptionCandidate();
                    DecryptionCandidateInfo newestCandidateInfo = newCandidatesList.get(newCandidatesList.size()-1);
                    encryptionCandidate.setCandidate(newestCandidateInfo.getOutput());
                    encryptionCandidate.setAllyTeamName(loginComponentController.getAllyTeamName());
                    encryptionCandidate.setFoundByState(newestCandidateInfo.getInitialState());
                    List<EncryptionCandidate> previousFoundCandidatesList = new ArrayList<>(DataService.getLastCandidatesProperty().get());
                    previousFoundCandidatesList.add(encryptionCandidate);
                    DataService.getLastCandidatesProperty().setValue(previousFoundCandidatesList);
                });
                agentClientDM.getListenerAdapter().getIsWorkCompletedProperty().addListener((observable1, oldWorkCompetedStatus, newWorkCompletedStatus) -> {
                    if(newWorkCompletedStatus == true){
                        DecryptionWorkPayload zippedWork = DataService.fetchWorkBatch(agentClientDM.getMaxNumberOfTasks());
                        List<MachineState> unzippedWork = DecryptionWorkPayloadParserService.unzip(zippedWork,machineHandler.getInventoryInfo().get());
                        agentClientDM.assignWork(unzippedWork,zippedWork.getInputToDecrypt());
                    }
                });
                //Start running
                agentClientDmThread = new Thread(agentClientDM);
                agentClientDmThread.start();
                //Fetch work:
                DecryptionWorkPayload zippedWork = DataService.fetchWorkBatch(agentClientDM.getMaxNumberOfTasks());
                List<MachineState> unzippedWork = DecryptionWorkPayloadParserService.unzip(zippedWork,machineHandler.getInventoryInfo().get());
                agentClientDM.assignWork(unzippedWork,zippedWork.getInputToDecrypt());
            }
        });
    }

    public void showMessage(String message) {
        if(headerComponentController!=null){
            headerComponentController.showMessage(message);
        }
    }

    public void changeSceneToLogin(){
        if(loginComponentController!=null){
            Parent rootComponent = loginComponentController.getRootComponent();
            bodyWrapScrollPane.setContent(imageGrid);
        }
        else{
            log.error("Failed to change scene to login - loginComponentController == null");
        }
    }

    public void changeSceneToContest() {
        if(contestPageController == null){
            log.error("Failed to change scene to Contest - contestController is null");
            return;
        }
        Parent rootComponent = contestPageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }

    public void makeBodyVisible() {
        bodyWrapScrollPane.setVisible(true);
    }

    public boolean isUserLoggedIn(){
        return loginComponentController.getIsLoggedInProperty().get();
    }
}


