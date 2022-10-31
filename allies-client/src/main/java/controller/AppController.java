package controller;

import com.google.gson.Gson;
import dto.MachineInventoryPayload;
import enums.GameStatus;
import generictype.MappingPair;
import javafx.application.Platform;
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

    @FXML
    @Getter
    HeaderController headerComponentController;

    @FXML
    @Getter
    LoginController loginComponentController;
    @FXML
    private DashboardPageController dashboardPageController;
    @FXML
    @Getter private ContestPageController contestPageController;

    @Setter
    @Getter
    private Stage primaryStage;
    @FXML
    private BorderPane appBorderPane;
    @FXML
    private AnchorPane bodyWrapAnchorPane;
    @FXML
    private ScrollPane bodyWrapScrollPane;
    @FXML
    private GridPane imageGrid;
    @FXML
    private AnchorPane headerWrapAnchorPane;
    @FXML
    private ScrollPane headerWrapScrollPane;

    @FXML
    GridPane headerComponent;

    @FXML
    GridPane loginComponent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (headerComponentController != null) {
            headerComponentController.setParentController(this);
        }
        if(loginComponentController != null){
            loginComponentController.setParentController(this);
        }
        FXMLLoader fxmlLoader;

        //Load Dashboard Page
        URL dashboardPageResource = AppController.class.getResource(PropertiesService.getDashboardPageTemplateFxmlPath());
        log.info("AppController - found Url of machine component:" + dashboardPageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(dashboardPageResource);
        try {
            Parent dashboardComponent = fxmlLoader.load(dashboardPageResource.openStream());
        } catch (IOException e) {
            log.error("Failed to load DashBoardPage:" + dashboardPageResource.toString() + "Exception throws: " + e.getMessage());
            throw new NullPointerException("Failed to load DashBoardPage from resource, dashboardComponent is null");
        }
        dashboardPageController = fxmlLoader.getController();
        dashboardPageController.setParentController(this);
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
                this.changeSceneToDashboard();
            }
            else{
                this.changeSceneToLogin();
            }
        });
        loginComponentController.getIsLoggedInProperty().bindBidirectional(headerComponentController.getIsLoggedInProperty());;
        log.info("AppController - app initialized");

        DataService.getGameStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getGameState() == GameStatus.DONE) {
                Platform.runLater(()->{
                    showMessage("Contest done! Winner is: " + newValue.getWinner());
                    DataService.getDmProgressProperty().setValue(new MappingPair<Long,Long>(1L,1L));
                    DataService.getCurrentContestRoomStateProperty().setValue(null);
                    DataService.getLastCandidatesProperty().setValue(null);
                });
            }
        });
        }



    public void showMessage(String message) {
        if(headerComponentController!=null){
            headerComponentController.showMessage(message);
        }
    }

    public void changeSceneToDashboard() {
        if(dashboardPageController != null){
            Parent rootComponent = dashboardPageController.getRootComponent();
            bodyWrapScrollPane.setContent(rootComponent);
        }
        else{
            log.error("Failed to change scene to machine - machineController == null");
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
        //TODO: check null
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


