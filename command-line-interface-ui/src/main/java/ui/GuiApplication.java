package src.main.java.ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.handler.FileConfigurationHandler;
import src.main.java.handler.PropertiesService;

import java.net.URL;
import java.util.Properties;

public class GuiApplication extends Application {
    private Scene bruteForceScene;
    private Scene machineScene;
    private Scene encryptScene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL machineResource = GuiApplication.class.getResource(PropertiesService.getPrimarySceneFxmlPath());
        URL bruteForceResource = GuiApplication.class.getResource(PropertiesService.getPrimarySceneFxmlPath());
        URL encryptResource = GuiApplication.class.getResource(PropertiesService.getPrimarySceneFxmlPath());

        primaryStage.setTitle("CTE Machine");
        System.out.println("here IN START METHOD:"+ machineResource);

        Parent machineSceneParent = FXMLLoader.load(machineResource);
        Parent bruteForceSceneParent = FXMLLoader.load(bruteForceResource);
        Parent encryptSceneParent = FXMLLoader.load(encryptResource);

        machineScene = new Scene(machineSceneParent);
        bruteForceScene = new Scene(bruteForceSceneParent);
        encryptScene = new Scene(encryptSceneParent);

        primaryStage.setScene(machineScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

}
