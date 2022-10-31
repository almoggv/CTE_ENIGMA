package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class GuiApplication extends Application {
    private static final Logger log = Logger.getLogger(GuiApplication.class);
    static {
        try {
            Properties p = new Properties();
            InputStream resourceAsInputStream = GuiApplication.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath());
            p.load(resourceAsInputStream);
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + GuiApplication.class.getSimpleName());
        }
        catch (IOException e) {
            System.out.println("Failed to configure logger of -" + GuiApplication.class.getSimpleName() + "IOException");
        }
        catch (NullPointerException e){
            System.out.println("Failed to configure logger of -" + GuiApplication.class.getSimpleName() + " Failed to find log4j.properties" );
        }
    }

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        //Load Primary App
        URL appResource = GuiApplication.class.getResource(PropertiesService.getAppFxmlPath());
//        URL appResource = GuiApplication.class.getResource(PropertiesService.getCandidateDataFxmlPath());
        if(appResource == null){
            log.fatal("GuiApplication - Start - Failed to find AppFxmlPath Url=" + null);
            throw new NullPointerException("GuiApplication - Start - Failed to find AppFxmlPath Url=" + appResource + ", Resource String="+PropertiesService.getAppFxmlPath());
        }
        log.info("GuiApplication - found Url of primary scene:"+ appResource);
        fxmlLoader.setLocation(appResource);
        Object primaryScenePane = fxmlLoader.load(appResource.openStream());
        Scene primaryScene = new Scene((Parent) primaryScenePane,PropertiesService.getAppWindowWidth(), PropertiesService.getAppWindowHeight());
        primaryStage.setTitle("Allies Client");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
}
