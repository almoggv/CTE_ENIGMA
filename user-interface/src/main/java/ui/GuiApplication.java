package src.main.java.ui;
import javafx.application.Application;
import javafx.collections.ObservableList;
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
import main.java.component.MachineHandler;
import main.java.component.impl.MachineHandlerImpl;
import src.main.java.controller.*;
import src.main.java.service.PropertiesService;

import java.net.URL;

public class GuiApplication extends Application {
//
//    private BorderPane primaryScenePane;
//    private AppController appController;
//    private GridPane headerComponent;
//    private HeaderController headerController;
//    private GridPane machinePageComponent;
//    private MachinePageController machinePageController;
//    private GridPane encryptPageComponent;
//    private EncryptPageController encryptPageController;

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //Load Primary App
        URL appResource = GuiApplication.class.getResource(PropertiesService.getAppWithHeaderFxmlPath());
        System.out.println("found Url of primary scene:"+ appResource);
        fxmlLoader.setLocation(appResource);
        Object primaryScenePane = fxmlLoader.load(appResource.openStream());
        AppController appController = fxmlLoader.getController();
        appController.setPrimaryStage(primaryStage);

//        //Load Header Component
//        URL headerResource = GuiApplication.class.getResource(PropertiesService.getHeaderFxmlPath());
//        System.out.println("found Url of header component:"+ headerResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(headerResource);
//        headerComponent = fxmlLoader.load(headerResource.openStream());
//        headerController = fxmlLoader.getController();
//        headerController.setPrimaryStage(primaryStage);
//        //Load MachinePage
//        URL machinePageResource = GuiApplication.class.getResource(PropertiesService.getMachinePageTemplateFxmlPath());
//        System.out.println("found Url of machine component:"+ machinePageResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(machinePageResource);
//        machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
//        machinePageController = fxmlLoader.getController();
//        initializeMachinePage();
//        //Load Encrypt Page
//        URL encryptPageResource = GuiApplication.class.getResource(PropertiesService.getEncryptPageTemplateFxmlPath());
//        System.out.println("found Url of encrypt component:"+ encryptPageResource);
//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(encryptPageResource);
//        encryptPageComponent = fxmlLoader.load(encryptPageResource.openStream());
//        encryptPageController = fxmlLoader.getController();
//        //Connect header to top in primary
//        connectHeaderComponentToRootComponent();
//        ///////////////////// - connecting machine page
//        AnchorPane topPaneInPrimary = (AnchorPane) primaryScenePane.getCenter();
//        ScrollPane topPaneInnerScrollPane = (ScrollPane) topPaneInPrimary.getChildren().get(0);
//        topPaneInnerScrollPane.setContent(machinePageComponent);
//        topPaneInnerScrollPane.setVisible(false);
//        /////////////////////
//        //Connect Controllers
//        appController.setHeaderController(headerController);
//        appController.setMachinePageController(machinePageController);
//        appController.setEncryptPageController(encryptPageController);
//        MachineHandler machineHandler = new MachineHandlerImpl();
//        appController.setMachineHandler(machineHandler);

        Scene primaryScene = new Scene((Parent) primaryScenePane);
        primaryStage.setTitle("CTE Machine");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

//    private void connectHeaderComponentToRootComponent() {
//        AnchorPane topPaneInPrimary = (AnchorPane) primaryScenePane.getTop();
//        ScrollPane topPaneInnerScrollPane = (ScrollPane) topPaneInPrimary.getChildren().get(0);
//        topPaneInnerScrollPane.setContent(headerComponent);
//
//    }
//
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
//        connectSetConfigComponentToMachinePage(setCofigComponent);
//        //Connecting their Controllers
//        machinePageController.setCurrMachineConfigController(currMachineConfigController);
//        machinePageController.setSetMachineConfigController(setMachineConfigController);
//
//    }
//
//    private void connectSetConfigComponentToMachinePage(GridPane setMachineConfigComponent) {
//        SplitPane bottomSplitPane = (SplitPane) getNodeFromGridPane(machinePageComponent,0,1);
//        if(bottomSplitPane == null ){
//            throw new NullPointerException("Failed to connect SetMachineConfig Template, could not find col-0 row-1 in grid");
//        }
//        ObservableList<Node> panesOfBottomSplit = bottomSplitPane.getItems();
//        for (Node node : panesOfBottomSplit) {
//            if(node.idProperty().get().equals(PropertiesService.getMachinePageTemplateBottomLeftAnchorFxId())){
//                AnchorPane concretePane = (AnchorPane) node;
//                ScrollPane innerScrollPane = (ScrollPane) concretePane.getChildren().get(0);
//                innerScrollPane.setContent(setMachineConfigComponent);
//            }
//        }
//    }
//
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
//
//
//    //Generic Utility Method
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
}
