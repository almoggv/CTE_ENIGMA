package src.main.java.ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.java.handler.FileConfigurationHandler;
import src.main.java.handler.PropertiesService;

import java.net.URL;
import java.util.Properties;

public class GuiApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL appResource = GuiApplication.class.getResource(PropertiesService.getPrimarySceneFxmlPath());

        primaryStage.setTitle("CTE Machine");
        System.out.println("found Url of primary scene:"+ appResource);

        Parent primarySceneParent = FXMLLoader.load(appResource);
        Scene primaryScene = new Scene(primarySceneParent);

        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

    private static Pane createMachineScenePane(){
        return null;
    }


}
