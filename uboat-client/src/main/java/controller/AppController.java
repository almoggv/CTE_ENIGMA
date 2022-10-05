package controller;

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
import dto.InventoryInfo;
import service.InventoryService;
import service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
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
            System.out.println("Failed to configure logger of -" + AppController.class.getSimpleName() ) ;
        }
    }

    @FXML @Getter HeaderController headerComponentController;
//    @FXML private MachinePageController machinePageController;

    @Setter @Getter private Stage primaryStage;
    @FXML private BorderPane appBorderPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;
    @FXML private GridPane imageGrid;
    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;

    @FXML GridPane headerComponent;

    public void showMessage(String message) {
        if(message == null || message.trim().equals("")){
            return;
        }
        headerComponentController.getNotificationMessageProperty().setValue("");
        headerComponentController.getNotificationMessageProperty().setValue(message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (headerComponentController != null) {
            headerComponentController.setParentController(this);
        }
        FXMLLoader fxmlLoader;
//        //Load Current Machine Config
//        URL currConfigResource = AppController.class.getResource(PropertiesService.getCurrMachineConfigFxmlPath());
//        log.info("AppController - found Url of header component:"+ currConfigResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(currConfigResource);
//        try {
//            GridPane currConfigComponent = fxmlLoader.load(currConfigResource.openStream());
//        } catch (IOException e) {
//            log.error("Failed to load currConfigResource:" + currConfigResource.toString() + "Exception throws: " + e.getMessage());
//        }
//        CurrMachineConfigController currMachineConfigController = fxmlLoader.getController();
//        //Load MachinePage
//        URL machinePageResource = AppController.class.getResource(PropertiesService.getMachinePageTemplateFxmlPath());
//        log.info("AppController - found Url of machine component:"+ machinePageResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(machinePageResource);
//        try {
//            Parent machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
//        } catch (IOException e) {
//            log.error("Failed to load machinePageResource:" + machinePageResource.toString() + "Exception throws: " + e.getMessage());
//        }
//        machinePageController = fxmlLoader.getController();
//        machinePageController.setParentController(this);
//        machinePageController.bindComponent(currMachineConfigController);
//        //TODO: Load Contest Page
    }

//    public void changeSceneToMachine(){
//        Parent rootComponent = machinePageController.getRootComponent();
//        bodyWrapScrollPane.setContent(rootComponent);
//    }

    public void changeSceneToContest(){
//        Parent rootComponent = contestPageController.getRootComponent();
//        bodyWrapScrollPane.setContent(rootComponent);
        throw new UnsupportedOperationException();
    }

    public void makeBodyVisible() {
        bodyWrapScrollPane.setVisible(true);
    }

    public void handleFileChosen(String absolutePath){
        //TODO: implement
//        TODO: check string is really absolute path
        Path p = Paths.get(absolutePath);
        if (!p.isAbsolute()) {
            this.showMessage("\""+absolutePath+"\" is not an absolute path");
            return;
        }
////      if Status = 200
//        headerController.getSelectedFileNameProperty().setValue(absolutePath);
//        headerController.getIsFileSelected().setValue(true);
//        makeBodyVisible();
////      else
//        this.showMessage(error message);

        throw new UnsupportedOperationException();
    }
}
