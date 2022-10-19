package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class CompetitionControlsController implements Initializable {

    private static final Logger log = Logger.getLogger(CompetitionControlsController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(CompetitionControlsController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + CompetitionControlsController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + CompetitionControlsController.class.getSimpleName() ) ;
        }
    }


    @FXML
    private Label amountOfAgentsValueLabel;

    @FXML
    private TextField taskSizeTextField;

    @FXML
    private Button readyButton;

    ContestPageController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setParentController(ContestPageController appController) {
        this.parentController = appController;
    }

    @FXML
    void onReadyButtonClicked(ActionEvent event) {

    }

    @FXML
    void onTaskSizeTextFieldFilled(ActionEvent event) {

    }

}
