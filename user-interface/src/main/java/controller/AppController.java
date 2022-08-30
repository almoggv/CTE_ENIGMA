package src.main.java.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
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
    GridPane HeaderComponent;
    @Getter
    @FXML private HeaderController HeaderComponentController;


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
    @FXML
    private Controller BodyComponentController;
    @FXML private GridPane machinePageComponent;

    @FXML private MachinePageController machinePageComponentController;
    public void setMachineHandler(MachineHandler machineHandler) {
        this.machineHandler = machineHandler;
    }

    @FXML
    public void initialize(/*URL location, ResourceBundle resources*/) {
        if(HeaderComponentController!=null){
            HeaderComponentController.setParentController(this);
        }
        if(machinePageController!=null){
            machinePageController.setParentController(this);
        }
        if(encryptPageController!=null){
            encryptPageController.setParentController(this);
        }
        machineHandler = new MachineHandlerImpl();
    }

    public void changeCenterComponent(String absolutePath) {
        Platform.runLater(()->{
            try{
                changeCenterComponentAndController(absolutePath);
            }
            catch (Exception e){
                System.out.println("problem changing center");
            }
        });
    }

    private void changeCenterComponentAndController(String absolutePath) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL appResource = GuiApplication.class.getResource(absolutePath);
        System.out.println("changing center component to: "+ absolutePath);
        fxmlLoader.setLocation(appResource);
        Parent rootComponent = (Parent) fxmlLoader.load(appResource.openStream());
        bodyWrapScrollPane.setContent(rootComponent);
    }

//    private void initializeMachinePage() throws Exception{
//        FXMLLoader fxmlLoader;
//        //Load Current Machine Config
//        URL currConfigResource = GuiApplication.class.getResource(PropertiesService.getCurrMachineConfigTemplateFxmlPath());
//        System.out.println("found Url of header component:"+ currConfigResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(currConfigResource);
//        GridPane currConfigComponent = fxmlLoader.load(currConfigResource.openStream());
//        CurrMachineConfigController currMachineConfigController = fxmlLoader.getController();
//        //Load Set Machine Config
//        URL setConfigResource = GuiApplication.class.getResource(PropertiesService.getSetMachineConfigTemplateFxmlPath());
//        System.out.println("found Url of header component:"+ setConfigResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(setConfigResource);
//        GridPane setCofigComponent = fxmlLoader.load(setConfigResource.openStream());
//        SetMachineConfigController setMachineConfigController = fxmlLoader.getController();
//        //Connecting them to their parent component
//        connectCurrConfigComponentToMachinePage(currConfigComponent);
////        connectSetConfigComponentToMachinePage(setCofigComponent);
//        //Connecting their Controllers
//        machinePageController.setCurrMachineConfigController(currMachineConfigController);
//        machinePageController.setSetMachineConfigController(setMachineConfigController);
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
//        //Generic Utility Method
//    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
//        if(gridPane == null || gridPane.getChildren() == null){
//            return null;
//        }
//        ObservableList<Node> children = gridPane.getChildren();
//        for (Node node : children) {
//            Integer columnIndex = GridPane.getColumnIndex(node);
//            Integer rowIndex = GridPane.getRowIndex(node);
//            if (columnIndex == null)
//                columnIndex = 0;
//            if (rowIndex == null)
//                rowIndex = 0;
//            if (columnIndex == col && rowIndex == row) {
//                return node;
//            }
//        }
//        return null;
//    }
    public void changeSceneToMachine(){
        Parent rootComponent = machinePageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }
    public void changeSceneToEncrypt() {
        Parent rootComponent = encryptPageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }
    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.HeaderComponentController = headerComponentController;
        headerComponentController.setParentController(this);
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
            HeaderComponentController.setMessageLabel("Message: File Loaded Successfully");
            return true;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            HeaderComponentController.setMessageLabel("Message: "+ e.getMessage());
            return false;
        }
    }

    public boolean handleFileChosen(String absolutePath) {
        if(loadFile(absolutePath)) {
            makeBodyVisible();
            Optional<InventoryInfo> optionalInventoryInfo = machineHandler.getInventoryInfo();
            //                machinePageController.getSetMachineConfigController().loadData(inventoryInfo.get());
            optionalInventoryInfo.ifPresent(DateService::setInventoryInfo);
//            optionalInventoryInfo.ifPresent(info -> inventoryInfo = info);
            return true;
//            }};
        }
        return false;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        HeaderComponentController.setPrimaryStage(primaryStage);
    }
}
