package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;
import service.DataService;
import service.HttpClientService;
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

        taskSizeTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            try{
                int value = Integer.parseInt(newValue);
                if(value < 1){
                    parentController.showMessage("Please enter natural number over 1");
                }
            }
            catch(Exception ignore){
                parentController.showMessage("Please enter natural number over 1");
            }
        }));

        DataService.getAgentsListStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                amountOfAgentsValueLabel.setText(String.valueOf(newValue.size()));
            }
        });
    }

    public void setParentController(ContestPageController contestPageController) {
        this.parentController = contestPageController;
    }

    @FXML
    void onReadyButtonClicked(ActionEvent event) {
        if(taskSizeTextField.getText().isEmpty() || taskSizeTextField.getText() == null ){
            parentController.showMessage("Please enter a task size first.");
        }
        else{
            String input = this.taskSizeTextField.getText();
            //noinspection ConstantConditions
            String finalUrl = HttpUrl
                    .parse(PropertiesService.getApiAllyReadyUrl())
                    .newBuilder()
                    .addQueryParameter(PropertiesService.getTaskSizeAttributeName(), input)
                    .build()
                    .toString();

            log.info("New request is sent for: " + finalUrl);
            HttpClientService.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        parentController.showMessage("Failed - cannot contact server");
                        log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                    });
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() >= 500){
                        parentController.showMessage("Failed - server error");
                        log.error("Failed to set task size - status=" + response.code() + " body=" + responseBody);
                        return;
                    }
                    if (response.code() != 200) {
                        Platform.runLater(() -> {
                            log.warn("Failed to tell server ally is ready - status=" + response.code() + " body=" + responseBody);
                            parentController.showMessage("Failed to set ready - "+ responseBody);
                        });
                    } else {
                        Platform.runLater(() -> {
                            log.debug("Successfully told server ally is ready :\", status=" + response.code() + ", response body=" + responseBody);
                            parentController.showMessage("Set ready!");
                            DataService.startCheckIsContestStarted();
                        });
                    }
                }
            });
        }
    }



}
