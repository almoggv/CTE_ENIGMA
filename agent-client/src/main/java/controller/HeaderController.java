package controller;

import com.google.gson.Gson;
import dto.AllyTeamData;
import dto.GameStatePayload;
import dto.LoginPayload;
import enums.GameStatus;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import jsonadapter.LoginPayloadJsonAdapter;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.NotificationPane;
import org.jetbrains.annotations.NotNull;
import service.DataService;
import service.HttpClientService;
import service.PropertiesService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    private static final Logger log = Logger.getLogger(HeaderController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(HeaderController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + HeaderController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + HeaderController.class.getSimpleName() ) ;
        }
    }

    @Getter @Setter private AppController parentController;

    @Getter private final BooleanProperty isLoggedInProperty = new SimpleBooleanProperty(false);
    private final SimpleStringProperty notificationMessageProperty = new SimpleStringProperty();
    private NotificationPane notificationPane;

    @FXML GridPane headerComponentRootPane;
    @FXML Label titleLabel;
    @FXML HBox componentNavButtonsHBox;
    public Button logoutButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createNotificationPane();

        DataService.getCurrentContestRoomStateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getGameStatus()!= null && newValue.getGameStatus() == GameStatus.READY) { //contest running
                logoutButton.setDisable(true);
            }
            else{
                logoutButton.setDisable(false);
            }
        });

        DataService.getCurrentTeamsProperty().addListener((observable, oldValue, newValue) -> {
//            if(newValue == null || newValue.isEmpty()){
//                logOutAction();
//            } else
            if (newValue != null
                    && isLoggedInProperty.get() == true
                    && !allyExist(newValue, parentController.loginComponentController.getAllyNameProperty().getValue())){
                logOutAction();
            }
        });
    }

    private boolean allyExist(List<AllyTeamData> teamDataList, String allyNameProperty) {
        for (AllyTeamData ally : teamDataList) {
            if(ally.getTeamName().equals(allyNameProperty)){
                return true;
            }
        }
        return false;
    }

    public NotificationPane getRootComponent(){
        return notificationPane;
    }

    private void createNotificationPane(){
        this.notificationPane = new NotificationPane(this.getRootComponent());
        notificationPane.setVisible(true);
        notificationPane.textProperty().bind(notificationMessageProperty);
        notificationPane.autosize();
        notificationPane.setContent(headerComponentRootPane);
        notificationPane.setPrefWidth(headerComponentRootPane.getPrefWidth());
        notificationPane.setPrefHeight(headerComponentRootPane.getPrefHeight());
        notificationPane.getStyleClass().add("notify");
        notificationMessageProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == null || newValue.equals("")){
                    return;
                }
                try{
                    log.debug("Notification pane listener - calling .show()");
                    notificationPane.show();
                }
                catch(Exception e){
                    log.error("Error showing message=" + newValue + " exception=" + e.getMessage());
                }
            }
        });
    }

    public void showMessage(String message){
        if (message == null || message.trim().equals("")) {
            return;
        }
        notificationMessageProperty.setValue("");
        notificationMessageProperty.setValue(message);
    }
    @FXML
    void onLogoutButtonClick(ActionEvent event) {
        logOutAction();
    }
    private void logOutAction(){
        if(isLoggedInProperty.get() == false){
            return;
        }

        String username = parentController.loginComponentController.getUsernameProperty().get();
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiLogoutPageUrl())
                .newBuilder()
                .addQueryParameter(PropertiesService.getUsernameAttribute(), username)
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
                    parentController.showMessage("Failed to Logout - server error");
                    log.error("Failed to logout from server - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                Gson gson = LoginPayloadJsonAdapter.buildGsonLoginPayloadAdapter();
                LoginPayload loginPayload = gson.fromJson(responseBody,LoginPayload.class);
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to logout from server - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to logout - " + loginPayload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        isLoggedInProperty.setValue(false);
                        log.info("Successfully Logged Out as :\"" + username + "\", status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Logged Out from: " + username);
                        parentController.headerComponent.setVisible(false);
                        parentController.loginComponentController.getUsernameProperty().setValue("");
                        parentController.loginComponentController.getAllyNameProperty().setValue("");
                        parentController.headerComponentController.isLoggedInProperty.setValue(false);

                        DataService.getCurrentContestRoomStateProperty().setValue(null);
                        DataService.getGameStatusProperty().setValue(null);
                        DataService.getLastCandidatesProperty().setValue(null);
                        DataService.getAgentsListStateProperty().setValue(null);
                        DataService.getGameStatusProperty().setValue(new GameStatePayload());
                    });
                }
            }
        });
    }
}



