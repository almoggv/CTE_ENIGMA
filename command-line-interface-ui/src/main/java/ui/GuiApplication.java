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
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = GuiApplication.class.getResource(PropertiesService.getPrimarySceneFxmlPath());
        primaryStage.setTitle("CTE");
        System.out.println("here IN START METHOD:"+ resource);
        Parent load = FXMLLoader.load(resource);
        Scene scene = new Scene(load, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

}
