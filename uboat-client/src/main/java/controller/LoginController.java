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
import org.jetbrains.annotations.NotNull;
import service.HttpClientService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @Getter
    @Setter
    private AppController parentController;

    @Getter private final SimpleStringProperty nameProperty = new SimpleStringProperty();
    @Getter private final SimpleBooleanProperty isNameSelected = new SimpleBooleanProperty(false);


    @FXML
    private TextField loginTextField;

    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginTextField.textProperty().bindBidirectional(nameProperty);
        loginButton.disableProperty().bind(isNameSelected.not());
        loginTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.equals("")){
                isNameSelected.setValue(true);
            }
            else {
                isNameSelected.setValue(false);
            }
        });
    }

    @FXML
    void onLoginButtonAction(ActionEvent event) {
        String username = getNameProperty().get();

        if (username.isEmpty()) {
            System.out.println("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiLoginPageUrl())
                .newBuilder()
                .addQueryParameter("username", username)
                .build()
                .toString();

        System.out.println("New request is launched for: " + finalUrl);

        HttpClientService.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            System.out.println("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
//                        chatAppMainController.updateUserName(userName);
                        System.out.println("HI " + username);
//                            loginComponent.setVisible(false);
                    });
                }
            }
        });
    }
    }
