package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.InventoryInfo;
import src.main.java.service.DataService;
import src.main.java.service.PropertiesService;
import src.main.java.service.ResourcesService;
import src.main.java.ui.GuiApplication;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class AppController/* implements Initializable */{

    public ImageView mainViewImage;
    private MachineHandler machineHandler;
    private InventoryInfo inventoryInfo;
    @Getter private Stage primaryStage;

    @Setter @Getter
    private SimpleBooleanProperty isMachineConfigured;


    @FXML
    GridPane headerComponentRootPane;
    @Getter
    @FXML private HeaderController headerComponentRootPaneController;
    @Getter
    @FXML MachinePageController machinePageController;
    @Getter
    @FXML EncryptPageController encryptPageController;
    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;

    //todo - change bodyWrapScrollPane to body component
    @FXML private MachinePageController machinePageComponentController;
    @FXML private EncryptPageController encryptPageComponentController;
//    @FXML private BruteForcePageController bruteForcePageComponentController; //Comment out when created



    public void setMachineHandler(MachineHandler machineHandler) {
        this.machineHandler = machineHandler;
    }

    public AppController(){
        isMachineConfigured = new SimpleBooleanProperty(false);
    }
    @FXML
    public void initialize(/*URL location, ResourceBundle resources*/) throws IOException {
        if(headerComponentRootPaneController !=null){
            headerComponentRootPaneController.setParentController(this);
        }
        FXMLLoader fxmlLoader;
        machineHandler = new MachineHandlerImpl();
        //Load Current Machine Config
        URL currConfigResource = GuiApplication.class.getResource(PropertiesService.getCurrMachineConfigTemplateFxmlPath());
        System.out.println("found Url of header component:"+ currConfigResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(currConfigResource);
        GridPane currConfigComponent = fxmlLoader.load(currConfigResource.openStream());
        CurrMachineConfigController currMachineConfigController = fxmlLoader.getController();
        //Load MachinePage
        URL machinePageResource = GuiApplication.class.getResource(PropertiesService.getMachinePageTemplateFxmlPath());
        System.out.println("found Url of machine component:"+ machinePageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(machinePageResource);
        Parent machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
        machinePageController = fxmlLoader.getController();
        machinePageController.setParentController(this);
        machinePageController.setMachineHandler(machineHandler);
        machinePageController.bindComponent(currMachineConfigController);
        //Load Encrypt Page
        URL encryptPageResource = GuiApplication.class.getResource(PropertiesService.getEncryptPageTemplateFxmlPath2());
        System.out.println("found Url of encrypt component:"+ encryptPageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(encryptPageResource);
        Parent encryptPageComponent = fxmlLoader.load(encryptPageResource.openStream());
        encryptPageController = fxmlLoader.getController();
        encryptPageController.setParentController(this);
        encryptPageController.setMachineHandler(machineHandler);
        encryptPageController.bindComponent(currMachineConfigController);
        encryptPageController.setMachineHandler(machineHandler);

        //added picture
        headerWrapScrollPane.setContent(headerComponentRootPaneController.getRootComponent());
        mainViewImage.setImage(new Image(ResourcesService.getEnigmaMachineIllustration()));
        // Load Brute Force Page :TODO
    }

    //Generic Utility Method
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        if(gridPane == null || gridPane.getChildren() == null){
            return null;
        }
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if (columnIndex == null)
                columnIndex = 0;
            if (rowIndex == null)
                rowIndex = 0;
            if (columnIndex == col && rowIndex == row) {
                return node;
            }
        }
        return null;
    }

    public void changeSceneToMachine(){
        Parent rootComponent = machinePageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }

    public void changeSceneToEncrypt() {
        Parent rootComponent = encryptPageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }

    public void makeBodyVisible() {
        bodyWrapScrollPane.setVisible(true);
    }

    public void handleFileChosen(String absolutePath) throws Exception{
        machineHandler.buildMachinePartsInventory(absolutePath);
        makeBodyVisible();
        Optional<InventoryInfo> optionalInventoryInfo = machineHandler.getInventoryInfo();
        optionalInventoryInfo.ifPresent(inventoryInfo -> DataService.getInventoryInfoProperty().setValue(inventoryInfo));
        DataService.getOriginalMachineStateProperty().setValue(null);
        DataService.getCurrentMachineStateProperty().setValue(null);
        DataService.getEncryptionInfoHistoryProperty().setValue(null);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showMessage(String message) {
        headerComponentRootPaneController.getNotificationMessageProperty().setValue(message);
    }
}
