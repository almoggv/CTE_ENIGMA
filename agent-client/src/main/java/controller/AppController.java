package controller;

import com.google.gson.Gson;
import component.MachineHandler;
import dto.AgentData;
import dto.MachineInventoryPayload;
import enums.GameStatus;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
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
import okhttp3.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;
import service.DataService;
import service.HttpClientService;
import service.PropertiesService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private AgentClientDM decryptionAgent;

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

        DataService.getIsContestStartedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == true){
                showMessage("Contest starting!");
            }
        });
    }

    public void initDecryptionAgent(MachineHandler machineHandler, AgentData agentData){
        if(machineHandler == null || agentData == null){
            log.error("Failed to initialize AgentClient's DecryptionManager machineHandler or agentData are null");
            return;
        }
        this.decryptionAgent = new AgentClientDMImpl(machineHandler,agentData.getNumberOfTasksThatTakes(),agentData.getNumberOfThreads(), agentData.getAllyName());
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

//    public void handleLogout() {
//        if(loginComponentController!=null){
//            loginComponentController.handleLogout();
//        }
//        else{
//            log.error("Failed to logout - loginComponentController is null");
//        }
//    }

    public boolean isUserLoggedIn(){
        return loginComponentController.getIsLoggedInProperty().get();
    }
}


