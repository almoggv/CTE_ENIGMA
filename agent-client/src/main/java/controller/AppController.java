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
import manager.DictionaryManagerStatic;
import manager.impl.AgentClientDMImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.DataService;
import service.DecryptionWorkPayloadParserService;
import service.InventoryService;
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
        DataService.getLastCandidatesProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()){
                return;
            }

        });
        DataService.getGameStatusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && (newValue.getGameState() == GameStatus.IN_PROGRESS || newValue.getGameState() == GameStatus.READY)){
                MachineHandler machineHandler = null;
                while(machineHandler == null && (newValue.getGameState() != GameStatus.DONE || newValue.getGameState() != GameStatus.WAITING)){
                    machineHandler = DataService.fetchMachineHandler();
                }
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
                    log.info("App - A New candidate was found Value=" + encryptionCandidate);
                    //Update DataService's property
                    if(DataService.getLastCandidatesProperty().get() == null){
                        DataService.getLastCandidatesProperty().setValue(new ArrayList<>());
                    }
                    List<EncryptionCandidate> updatedList = new ArrayList<>(DataService.getLastCandidatesProperty().get());
                    if(!candidateAlreadyExists(encryptionCandidate,updatedList)){
                        updatedList.add(encryptionCandidate);
                        DataService.getLastCandidatesProperty().setValue(updatedList);
                    }
                });
                agentClientDM.getIsReadyForMoreWorkProperty().addListener((observable1, oldReadyForMoreValue, newReadyForMoreValue) -> {
                    if(newReadyForMoreValue == true){
                        synchronized (agentClientDM){
                            MachineHandler machineHandler1 = agentClientDM.getMachineHandler();
                            DecryptionWorkPayload zippedWork = null;
                            while(zippedWork == null && (DataService.getGameStatusProperty().get() != null && DataService.getGameStatusProperty().get().getGameState() != GameStatus.DONE)){
                                zippedWork = DataService.fetchWorkBatch(agentClientDM.getMaxNumberOfTasks());
                            }
                            if(DataService.getGameStatusProperty().get() != null && DataService.getGameStatusProperty().get().getGameState() != GameStatus.DONE){
                                log.info("Not Fetching Work (inside event listener), game status=" + DataService.getGameStatusProperty().get().getGameState());
                                return;
                            }
                            List<MachineState> unzippedWork = DecryptionWorkPayloadParserService.unzip(zippedWork,machineHandler1.getInventoryInfo().get());
                            agentClientDM.assignWork(unzippedWork,zippedWork.getInputToDecrypt());
                        }
                    }
                });
                //Start running
                agentClientDmThread = new Thread(agentClientDM);
                agentClientDmThread.start();
                //Fetch Dictionary
                while(DictionaryManagerStatic.getDictionary().isEmpty() && (DataService.getGameStatusProperty().get() != null && DataService.getGameStatusProperty().get().getGameState() != GameStatus.DONE)){
                    DataService.loadDictionaryManager();
                }
                log.info("App - loaded Dictionary");
                InventoryService.setReflectorsInventory(machineHandler.getInventoryComponents().get().getReflectorsInventory());
                log.info("App - loaded Inventory service");
                //Fetch work:
                DecryptionWorkPayload zippedWork = DataService.fetchWorkBatch(agentClientDM.getMaxNumberOfTasks());
                while(zippedWork == null && (DataService.getGameStatusProperty().get() != null && DataService.getGameStatusProperty().get().getGameState() != GameStatus.DONE)){
                    zippedWork = DataService.fetchWorkBatch(agentClientDM.getMaxNumberOfTasks());
                }
                log.info("App - first work batch fetched, Value=" + zippedWork);
                List<MachineState> unzippedWork = DecryptionWorkPayloadParserService.unzip(zippedWork,machineHandler.getInventoryInfo().get());
                log.info("App - first work batch unzipped - amount=" + unzippedWork.size());
                agentClientDM.assignWork(unzippedWork,zippedWork.getInputToDecrypt());
                log.info("App - first work batch assigned to AgentClientDM");
            }
            if(newValue != null && newValue.getGameState() == GameStatus.DONE){
                agentClientDM.kill();
            }
        });
        log.info("AppController - app initialized");
    }

    private boolean candidateAlreadyExists(EncryptionCandidate newCandidate, List<EncryptionCandidate> candidateList) {
        for (EncryptionCandidate candidate: candidateList ) {
            if(candidate.getCandidate().equals(newCandidate.getCandidate())){
                return true;
            }
        }
        return false;
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


