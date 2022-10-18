package controller;

import com.google.gson.Gson;
import dto.EncryptionResponsePayload;
import dto.LoginPayload;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import jsonadapter.LoginPayloadJsonAdapter;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.textfield.CustomTextField;
import org.jetbrains.annotations.NotNull;
import service.HttpClientService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class EncryptionController implements Initializable {
    private static final Logger log = Logger.getLogger(EncryptionController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(EncryptionController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + EncryptionController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + EncryptionController.class.getSimpleName());
        }
    }
    @Setter @Getter
    @FXML ContestPageController parentController;
    @FXML
    public CustomTextField encryptTextField;
    @FXML public Button encryptButton;
    @FXML public CustomTextField resultTextField;
    @FXML public Button clearButton;
    @FXML public Button resetMachineStateButton;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        encryptButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        encryptTextField.getText().trim().isEmpty(),
                encryptTextField.textProperty()));
    }

    public void onEncryptButtonAction(ActionEvent actionEvent) {
        String input = this.encryptTextField.getText().toUpperCase();
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiEncryptPageUrl())
                .newBuilder()
                .addQueryParameter(PropertiesService.getInputAttributeName(), input)
                .build()
                .toString();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to encrypt - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to encrypt - server error");
                    log.error("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                Gson gson = new Gson();
                EncryptionResponsePayload payload = gson.fromJson(responseBody,EncryptionResponsePayload.class);
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to encrypt - "+ payload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        log.debug("Successfully encrypted input :\"" + payload.getOutput() + "\", status=" + response.code() + ", response body=" + responseBody);
                        resultTextField.setText(payload.getOutput().trim());
                    });
                }
            }
        });

    }

    public void onClearButtonAction(ActionEvent actionEvent) {
        resultTextField.clear();
        encryptTextField.clear();
    }

    public void onResetMachineStateButtonAction(ActionEvent actionEvent) {
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiResetMachineStateUrl())
                .newBuilder()
                .build()
                .toString();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to reset machine state - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to reset machine state - server error");
                    log.error("Failed to reset machine state in server - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to reset machine state in server - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to login - "+ responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        log.info("Successfully reset machine state \", status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Successfully reset machine state");
                    });
                }
            }
        });
    }
}
