package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jetbrains.annotations.NotNull;
import service.HttpClientService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
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

    @Getter
    @Setter
    private AppController parentController;

    @Getter
    private final SimpleStringProperty nameProperty = new SimpleStringProperty();
    @Getter
    private final SimpleBooleanProperty isNameSelected = new SimpleBooleanProperty(false);


    @FXML
    private TextField loginTextField;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginTextField.textProperty().bindBidirectional(nameProperty);
        loginButton.disableProperty().bind(isNameSelected.not());
        loginTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("")) {
                isNameSelected.setValue(true);
            } else {
                isNameSelected.setValue(false);
            }
        });
    }

    @FXML
    void onLoginButtonAction(ActionEvent event) {
        String username = getNameProperty().get();
        if (username.isEmpty()) {
            parentController.showMessage("Username cannot be empty");
            return;
        }
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiLoginPageUrl())
                .newBuilder()
                .addQueryParameter(PropertiesService.getUsernameAttribute(), username)
                .build()
                .toString();

        log.debug("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to login - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to login");
                    });
                } else {
                    Platform.runLater(() -> {
                        log.info("Successfully Logged in as :\"" + username + "\", status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Successfully Logged in as :" + username);
                    });
                }
            }
        });
    }

    @FXML
    void onShowMessageButtonClick(ActionEvent event) {
        parentController.showMessage("Test");
    }
}
