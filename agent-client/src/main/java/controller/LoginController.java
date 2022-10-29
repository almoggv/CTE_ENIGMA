package controller;

import com.google.gson.Gson;
import dto.AllyTeamData;
import dto.LoginPayload;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import jsonadapter.LoginPayloadJsonAdapter;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.jetbrains.annotations.NotNull;
import service.DataService;
import service.HttpClientService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private static final Logger log = Logger.getLogger(LoginController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(LoginController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + LoginController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + LoginController.class.getSimpleName());
        }
    }


    private ValidationSupport validationSupport = new ValidationSupport();
    @Getter
    private final SimpleStringProperty usernameProperty = new SimpleStringProperty();
    @Getter
    private final SimpleBooleanProperty isLoggedInProperty = new SimpleBooleanProperty(false);
    @Getter private final StringProperty allyNameProperty = new SimpleStringProperty();

    @Getter @Setter private AppController parentController;

    @FXML public TextField taskSizeTextField;
    @FXML public TextField threadNumTextField;
    @FXML public ComboBox allieNamesComboBox;
    @FXML private TextField loginTextField;
    @FXML private Button loginButton;
    @FXML private GridPane agentrootGridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginTextField.textProperty().bindBidirectional(usernameProperty);

        DataService.startPullingTeamsData();
        DataService.getCurrentTeamsProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                if(newValue.size() != allieNamesComboBox.getItems().size()) {
                    initializeAllieNamesComboBox(newValue);
                }
            }
        });
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
        threadNumTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            try{
                int value = Integer.parseInt(newValue);
                if(value < 1 || value > 4){
                    parentController.showMessage("Please enter number between 1 and 4");
                }
            }
            catch(Exception ignore){
                parentController.showMessage("Please enter natural number over 1");
            }
        }));
    }

    private void initializeAllieNamesComboBox(List<AllyTeamData> allyTeamDataList) {
        Platform.runLater(() -> {
            allieNamesComboBox.getItems().clear();
            for (AllyTeamData allyTeam : allyTeamDataList) {
                allieNamesComboBox.getItems().add(allyTeam.getTeamName());
            }
            allieNamesComboBox.getSelectionModel().select(0);
        });
    }

    @FXML
    void onLoginButtonAction(ActionEvent event) {
        isLoggedInProperty.setValue(false);
        String username = this.getUsernameProperty().get();
        String allyname = (String) this.allieNamesComboBox.getSelectionModel().getSelectedItem();
        String taskSize = this.taskSizeTextField.getText();
        String threadCount = this.threadNumTextField.getText();
        if (username == null || username.isEmpty()) {
            parentController.showMessage("Username cannot be empty");
            return;
        }
        if (allyname == null || allyname.isEmpty()) {
            parentController.showMessage("Ally name cannot be empty");
            return;
        }
        if (taskSize ==null || taskSize.isEmpty()) {
            parentController.showMessage("Task size cannot be empty");
            return;
        }
        if (threadCount == null || threadCount.isEmpty()) {
            parentController.showMessage("Thread count cannot be empty");
            return;
        }
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiLoginPageUrl())
                .newBuilder()
                .addQueryParameter(PropertiesService.getUsernameAttribute(), username)
                .addQueryParameter(PropertiesService.getUserTypeAttributeName(), PropertiesService.getAgentAttributeName())
                .addQueryParameter(PropertiesService.getAllyNameAttribute(), allyname)
                .addQueryParameter(PropertiesService.getTaskSizeAttributeName(), taskSize)
                .addQueryParameter(PropertiesService.getThreadCountAttributeName(), threadCount)
                .build()
                .toString();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    isLoggedInProperty.setValue(false);
                    parentController.showMessage("Failed to login - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to login - server error");
                    log.error("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
                LoginPayload loginPayload = gson.fromJson(responseBody,LoginPayload.class);
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        isLoggedInProperty.setValue(false);
                        log.warn("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to login - "+ loginPayload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        isLoggedInProperty.setValue(true);
                        allyNameProperty.setValue(allyname);
                        log.info("Successfully Logged in as :\"" + username + "\", status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Successfully Logged in as :" + username);
                        parentController.headerComponent.setVisible(true);
                        DataService.startPullingRoomData();
                        DataService.startPullingAgentData();
                    });
                }
            }
        });
    }

    public GridPane getRootComponent() {
        return agentrootGridPane;
    }

}
