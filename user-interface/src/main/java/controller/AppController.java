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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.InventoryInfo;
import main.java.manager.DecryptionManager;
import main.java.manager.DictionaryManager;
import main.java.manager.impl.DecryptionManagerImpl;
import main.java.service.InventoryService;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

public class AppController/* implements Initializable */{
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

    private MachineHandler machineHandler;
    private InventoryInfo inventoryInfo;
    private DecryptionManager decryptionManager;
    @Setter @Getter
    private final SimpleBooleanProperty isMachineConfigured = new SimpleBooleanProperty(false);;

    @Getter private Stage primaryStage;

    @Getter
    @FXML private HeaderController headerComponentRootPaneController;
    @Getter
    @FXML private MachinePageController machinePageController;
    @Getter
    @FXML private EncryptPageController encryptPageController;
    @Getter
    @FXML private BruteForcePageController bruteForcePageController;
    CurrMachineConfigController currMachineConfigController;

    public GridPane imageGrid;
    public BorderPane appBorderPane;
    public ImageView mainViewImage;
    @FXML GridPane headerComponentRootPane;
    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;

    public void setMachineHandler(MachineHandler machineHandler) {
        this.machineHandler = machineHandler;
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
        log.info("AppController - found Url of header component:"+ currConfigResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(currConfigResource);
        GridPane currConfigComponent = fxmlLoader.load(currConfigResource.openStream());
        currMachineConfigController = fxmlLoader.getController();
        //Load MachinePage
        URL machinePageResource = GuiApplication.class.getResource(ResourceLocationService.getMachinePageTemplateFxmlPath());
        log.info("AppController - found Url of machine component:"+ machinePageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(machinePageResource);
        Parent machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
        machinePageController = fxmlLoader.getController();
        machinePageController.setParentController(this);
        machinePageController.setMachineHandler(machineHandler);
        machinePageController.bindComponent(currMachineConfigController);
        //Load Encrypt Page
        URL encryptPageResource = GuiApplication.class.getResource(ResourceLocationService.getEncryptPageTemplateFxmlPath());
        log.info("AppController - found Url of encrypt component:"+ encryptPageResource);
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
        decryptionManager = new DecryptionManagerImpl(machineHandler, bruteForcePageController.createUIAdapter());
        decryptionManager.setNumberOfAgents(DataService.getCurrNumberOfAgentsProperty().get());
        bruteForcePageController.setDecryptionManager(decryptionManager);

        headerWrapScrollPane.setContent(headerComponentRootPaneController.getRootComponent());
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
        if(optionalInventoryInfo.isPresent()){
            DataService.getInventoryInfoProperty().setValue(null);
            optionalInventoryInfo.ifPresent(inventoryInfo -> DataService.getInventoryInfoProperty().setValue(inventoryInfo));
            DataService.getOriginalMachineStateProperty().setValue(null);
            DataService.getCurrentMachineStateProperty().setValue(null);
            DataService.getEncryptionInfoHistoryProperty().setValue(null);
            DataService.getMaxAgentNumProperty().setValue(InventoryService.getAgentsInventory());
            DictionaryManager.loadDictionary(absolutePath);
            bruteForcePageController.clearDecryptionResults();
        }
        changeSceneToMachine();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Displays a message in the Notification Panel
     * @param message
     */
    public void showMessage(String message) {
        if(message == null || message.trim().equals("")){
            return;
        }
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
            if(cssAbsolutePath.contains(ResourceLocationService.getDarkThemeCssPath())){
                mainViewImage.setImage(new Image(ResourceLocationService.getEnigmaMachineIllustrationDark()));
            }
            else if (cssAbsolutePath.contains(ResourceLocationService.getLightThemeCssPath())){
                mainViewImage.setImage(new Image(ResourceLocationService.getEnigmaMachineIllustration2()));
            }
        }
    }
}
