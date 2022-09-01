package src.main.java.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import main.java.dto.InventoryInfo;
import src.main.java.service.DateService;
import src.main.java.service.PropertiesService;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class AppController/* implements Initializable */{

    private MachineHandler machineHandler;

    private InventoryInfo inventoryInfo;
    @FXML
    GridPane headerComponentRootPane;
    @Getter
    @FXML private HeaderController headerComponentRootPaneController;
    @Getter
    @FXML MachinePageController machinePageController;
    @Getter
    @FXML EncryptPageController encryptPageController;
    private Stage primaryStage;
    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;

    //todo - change bodyWrapScrollPane to body component
    @FXML
    private ScrollPane BodyComponent;
//    @FXML
//    private Controller bodyComponentController;
    @FXML private GridPane machinePageComponent;

    @FXML private MachinePageController machinePageComponentController;

    @FXML private GridPane encryptPageComponent;

    @FXML private MachinePageController encryptPageComponentController;
    @Setter
    @Getter private SimpleBooleanProperty isMachineConfigured;

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
//        if(machinePageController!=null){
//            machinePageController.setParentController(this);
//        }
//        if(encryptPageController!=null){
//            encryptPageController.setParentController(this);
//        }
        machineHandler = new MachineHandlerImpl();
        //Load MachinePage
        URL machinePageResource = GuiApplication.class.getResource(PropertiesService.getMachinePageWithIncludesFxmlPath());
        System.out.println("found Url of machine component:"+ machinePageResource);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(machinePageResource);
        machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
        machinePageController = fxmlLoader.getController();
        machinePageController.setMachineHandler(machineHandler);
        //Load Encrypt Page
        URL encryptPageResource = GuiApplication.class.getResource(PropertiesService.getEncryptPageTemplateFxmlPath());
        System.out.println("found Url of encrypt component:"+ encryptPageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(encryptPageResource);
        encryptPageComponent = fxmlLoader.load(encryptPageResource.openStream());
        encryptPageController = fxmlLoader.getController();
        encryptPageController.setMachineHandler(machineHandler);
    }

//    public void changeCenterComponent(String absolutePath) {
//        Platform.runLater(()->{
//            try{
//                changeCenterComponentAndController(absolutePath);
//            }
//            catch (Exception e){
//                System.out.println("problem changing center");
//            }
//        });
//    }
//
//    private void changeCenterComponentAndController(String absolutePath) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        URL appResource = GuiApplication.class.getResource(absolutePath);
//        System.out.println("changing center component to: "+ absolutePath);
//        fxmlLoader.setLocation(appResource);
//        Parent rootComponent = (Parent) fxmlLoader.load(appResource.openStream());
//        bodyComponentController = fxmlLoader.getController();
//        bodyComponentController.setParentController(this);
//        bodyComponentController.setMachineHandler(machineHandler);
//        bodyWrapScrollPane.setContent(rootComponent);
//    }

//    private void connectCurrConfigComponentToMachinePage(GridPane currMachineConfigComponent) {
//        SplitPane bottomSplitPane = (SplitPane) getNodeFromGridPane(machinePageComponent,0,1);
//        if(bottomSplitPane == null ){
//            throw new NullPointerException("Failed to connect SetMachineConfig Template, could not find col-0 row-1 in grid");
//        }
//        ObservableList<Node> panesOfBottomSplit = bottomSplitPane.getItems();
//        for (Node node : panesOfBottomSplit) {
//            if(node.idProperty().get().equals(PropertiesService.getMachinePageTemplateBottomRightAnchorFxId())){
//                AnchorPane concretePane = (AnchorPane) node;
//                ScrollPane innerScrollPane = (ScrollPane) concretePane.getChildren().get(0);
//                innerScrollPane.setContent(currMachineConfigComponent);
//            }
//        }
//    }

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
    public void setHeaderComponentRootPaneController(HeaderController headerComponentRootPaneController) {
        this.headerComponentRootPaneController = headerComponentRootPaneController;
        headerComponentRootPaneController.setParentController(this);
    }

    public void setMachinePageController(MachinePageController machinePageController) {
        this.machinePageController = machinePageController;
        machinePageController.setParentController(this);
    }

    public void setEncryptPageController(EncryptPageController encryptPageController) {
        this.encryptPageController = encryptPageController;
        encryptPageController.setParentController(this);
    }

    public void makeBodyVisible() {
        bodyWrapScrollPane.setVisible(true);
    }

    public boolean loadFile(String absolutePath) {
        try {
            machineHandler.buildMachinePartsInventory(absolutePath);
            System.out.println("File Loaded Successfully");
            headerComponentRootPaneController.setMessageLabel("Message: File Loaded Successfully");
            return true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            headerComponentRootPaneController.setMessageLabel("Message: "+ e.getMessage());
            return false;
        }
    }

    public boolean handleFileChosen(String absolutePath) {
        if(loadFile(absolutePath)) {
            makeBodyVisible();
            Optional<InventoryInfo> optionalInventoryInfo = machineHandler.getInventoryInfo();
            optionalInventoryInfo.ifPresent(DateService::setInventoryInfo);
            if(optionalInventoryInfo.isPresent()){
                DateService.setIsMachineInventoryConfigured(true);
                //todo - see if we can connect components to the data service to show automatically
                machinePageController.getSetMachineConfigurationComponentController().setMachineDetails();
            }
            return true;
        }
        return false;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        headerComponentRootPaneController.setPrimaryStage(primaryStage);
    }
}
