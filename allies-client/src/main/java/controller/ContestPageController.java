package controller;

import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class ContestPageController implements Initializable {

    private static final Logger log = Logger.getLogger(ContestPageController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(ContestPageController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + ContestPageController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + ContestPageController.class.getSimpleName() ) ;
        }
    }

    public GridPane rootGridPane;


    AppController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setParentController(AppController appController) {
        this.parentController = appController;
    }
    public GridPane getRootComponent() {
        return  rootGridPane;
    }

}
