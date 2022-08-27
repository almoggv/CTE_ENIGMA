package src.main.java.ui;
import com.sun.xml.internal.ws.api.message.Header;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.main.java.controller.AppController;
import src.main.java.controller.HeaderController;
import src.main.java.service.PropertiesService;
import sun.plugin.javascript.navig.Anchor;

import java.net.URL;

public class GuiApplication extends Application {


    
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Load Primary App
        URL appResource = GuiApplication.class.getResource(PropertiesService.getAppFxmlPath());
        System.out.println("found Url of primary scene:"+ appResource);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(appResource);
        BorderPane primaryScenePane = fxmlLoader.load(appResource.openStream());
        AppController appController = fxmlLoader.getController();
        //Load Header Component
        URL headerResource = GuiApplication.class.getResource(PropertiesService.getHeaderFxmlPath());
        System.out.println("found Url of header component:"+ headerResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(headerResource);
        GridPane headerComponent = fxmlLoader.load(headerResource.openStream());
        HeaderController headerController = fxmlLoader.getController();


        //Connect header to top in primary
        AnchorPane topPaneInPrimary = (AnchorPane) primaryScenePane.getTop();
        ScrollPane topPaneInnerScrollPane = (ScrollPane) topPaneInPrimary.getChildren().get(0);
        topPaneInnerScrollPane.setContent(headerComponent);

        appController.setHeaderController(headerController);

        Scene primaryScene = new Scene(primaryScenePane);
        primaryStage.setTitle("CTE Machine");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(GuiApplication.class);
    }

}
