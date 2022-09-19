package src.main.java.ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import src.main.java.controller.*;
import src.main.java.service.ResourceLocationService;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class GuiApplication extends Application {

    private static final Logger log = Logger.getLogger(GuiApplication.class);
    static {
        try {
            Properties p = new Properties();
            p.load(GuiApplication.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + GuiApplication.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GuiApplication.class.getSimpleName() ) ;
        }
    }

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //Load Primary App
        URL appResource = GuiApplication.class.getResource(ResourceLocationService.getAppFxmlPath());
        log.info("GuiApplication - found Url of primary scene:"+ appResource);
        fxmlLoader.setLocation(appResource);
        Object primaryScenePane = fxmlLoader.load(appResource.openStream());
        AppController appController = fxmlLoader.getController();
        appController.setPrimaryStage(primaryStage);
        Scene primaryScene = new Scene((Parent) primaryScenePane,950, 650);
//        primaryScene.getStylesheets().add(getClass().getResource(ResourceLocationService.getLightThemeCssPath()).toExternalForm());
        primaryScene.getStylesheets().add(getClass().getResource(ResourceLocationService.getLightThemeCssPath()).toExternalForm());
        primaryStage.setTitle("CTE Machine");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

}
