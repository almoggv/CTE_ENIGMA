package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.InventoryInfo;
import main.java.manager.DecryptionManager;
import main.java.manager.impl.DecryptionManagerImpl;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class AppController/* implements Initializable */{

    private MachineHandler machineHandler;
    private InventoryInfo inventoryInfo;
    private DecryptionManager decryptionManager;

    @Getter private Stage primaryStage;

    @Setter @Getter
    private SimpleBooleanProperty isMachineConfigured;

    public ImageView mainViewImage;
    @FXML
    GridPane headerComponentRootPane;
    @Getter
    @FXML private HeaderController headerComponentRootPaneController;
    @Getter
    @FXML MachinePageController machinePageController;
    @Getter
    @FXML EncryptPageController encryptPageController;
    @Getter
    @FXML private BruteForcePageController bruteForcePageController;
    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;


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
        URL currConfigResource = GuiApplication.class.getResource(ResourceLocationService.getCurrMachineConfigTemplateFxmlPath());
        System.out.println("found Url of header component:"+ currConfigResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(currConfigResource);
        GridPane currConfigComponent = fxmlLoader.load(currConfigResource.openStream());
        CurrMachineConfigController currMachineConfigController = fxmlLoader.getController();
        //Load MachinePage
        URL machinePageResource = GuiApplication.class.getResource(ResourceLocationService.getMachinePageTemplateFxmlPath());
        System.out.println("found Url of machine component:"+ machinePageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(machinePageResource);
        Parent machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
        machinePageController = fxmlLoader.getController();
        machinePageController.setParentController(this);
        machinePageController.setMachineHandler(machineHandler);
        machinePageController.bindComponent(currMachineConfigController);
        //Load Encrypt Page
        URL encryptPageResource = GuiApplication.class.getResource(ResourceLocationService.getEncryptPageTemplateFxmlPath2());
        System.out.println("found Url of encrypt component:"+ encryptPageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(encryptPageResource);
        Parent encryptPageComponent = fxmlLoader.load(encryptPageResource.openStream());
        encryptPageController = fxmlLoader.getController();
        encryptPageController.setParentController(this);
        encryptPageController.setMachineHandler(machineHandler);
        encryptPageController.bindComponent(currMachineConfigController);
        //Load BruteForce Page
        URL bruteForcePageResource = GuiApplication.class.getResource(ResourceLocationService.getBruteForcePageTemplateFxmlPath());
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(bruteForcePageResource);
        Parent bruteForcePageComponent = fxmlLoader.load(bruteForcePageResource.openStream());
        bruteForcePageController = fxmlLoader.getController();
        bruteForcePageController.setParentController(this);
        bruteForcePageController.bindComponent(currMachineConfigController);
        bruteForcePageController.setMachineHandler(machineHandler);

        //added picture
        headerWrapScrollPane.setContent(headerComponentRootPaneController.getRootComponent());
//        mainViewImage.setImage(new Image(ResourcesService.getEnigmaMachineIllustration2()));
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

    public void changeSceneToBruteForce(){
        Parent rootComponent = bruteForcePageController.getRootComponent();
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

    public void loadCssFile(URL cssAbsolutePath) {
        primaryStage.getScene().getStylesheets().clear();
        if(cssAbsolutePath == null || cssAbsolutePath.getPath().equals("")){
            return;
        }
        primaryStage.getScene().getStylesheets().add(cssAbsolutePath.getPath());
    }

    public void loadCssFile(String cssAbsolutePath) {
        if(cssAbsolutePath != null) {
            primaryStage.getScene().getStylesheets().clear();

            primaryStage.getScene().getStylesheets().add(cssAbsolutePath);
        }
    }
}
