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
        Scene primaryScene = new Scene((Parent) primaryScenePane);
        primaryStage.setTitle("CTE Machine");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

}
