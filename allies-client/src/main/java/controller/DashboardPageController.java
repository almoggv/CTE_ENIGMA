package controller;

import app.GuiApplication;
import com.google.gson.Gson;
import dto.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
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
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

public class DashboardPageController implements Initializable {

    private static final Logger log = Logger.getLogger(DashboardPageController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(DashboardPageController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + DashboardPageController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + DashboardPageController.class.getSimpleName() ) ;
        }
    }

    public FlowPane contestDataFlowPane;
    public FlowPane agentsDataFlowPane;
    public ComboBox contestNameComboBox;
    AppController parentController;

    @FXML
    private GridPane rootGridPane;

    @FXML
    private Button joinContestButton;

//    @FXML
//    private TextField chosenContestTextField;

    public void setParentController(AppController appController) {
        this.parentController = appController;
    }

    public GridPane getRootComponent() {
       return  rootGridPane;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        joinContestButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
//                        chosenContestTextField.getText().trim().isEmpty(),
//                chosenContestTextField.textProperty()));

        DataService.getContestRoomsStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                createContestsDataComponents(newValue);
                if(newValue.size() != contestNameComboBox.getItems().size()) {
                    initializeAllieNamesComboBox(newValue);
                }
            }
            else{
                Platform.runLater(()->{
                    contestDataFlowPane.getChildren().clear();
                });
            }
        });

        DataService.getAgentsListStateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null ){
                createAgentsDataComponents(newValue);
            }
            else{
                Platform.runLater(()->{
                    agentsDataFlowPane.getChildren().clear();
                });
            }
        });
    }

    private void initializeAllieNamesComboBox(Set<ContestRoomData> roomDataList) {
        Platform.runLater(() -> {
            contestNameComboBox.getItems().clear();
            for (ContestRoomData contest : roomDataList) {
                contestNameComboBox.getItems().add(contest.getName());
            }
            contestNameComboBox.getSelectionModel().select(0);
        });
    }
    private void createAgentsDataComponents(List<AgentData> agentDataList) {
        Platform.runLater(() -> {
            try {
                agentsDataFlowPane.getChildren().clear();
                for (AgentData agentData : agentDataList) {
                    URL agentDataURL = GuiApplication.class.getResource(PropertiesService.getAgentDataFxmlPath());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(agentDataURL);
                    Parent agentComponent = fxmlLoader.load(agentDataURL.openStream());
                    AgentDataController agentDataController = fxmlLoader.getController();
                    agentDataController.setData(agentData);

//                    agentsDataFlowPane.setParentController(this);

                    agentsDataFlowPane.getChildren().add(agentComponent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onRefreshContests(ActionEvent actionEvent) {
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiContestsInfoUrl())
                .newBuilder()
                .build()
                .toString();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to get contests data - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to get contests data - server error");
                    log.error("Failed to get contests data - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                Gson gson = new Gson();
                AllContestRoomsPayload payload = gson.fromJson(responseBody,AllContestRoomsPayload.class);
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to get contests data - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("d get contests data  to login - "+ payload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        log.info("Successfully got contests data, status=" + response.code() + ", response body=" + responseBody);
                        createContestsDataComponents(payload.getContestRooms());
                    });
                }
            }
        });
    }

    private void createContestsDataComponents(Set<ContestRoomData> contestRooms) {
        Platform.runLater(() -> {
            try {
                contestDataFlowPane.getChildren().clear();
                for (ContestRoomData contestRoom : contestRooms) {
                    URL contestRoomURL = GuiApplication.class.getResource(PropertiesService.getContestDataFxmlPath());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(contestRoomURL);
                    Parent decodedCandidate = fxmlLoader.load(contestRoomURL.openStream());
                    ContestDataController contestDataController = fxmlLoader.getController();
                    contestDataController.setData(contestRoom);

                    contestDataController.setParentController(this);

                    contestDataFlowPane.getChildren().add(decodedCandidate);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void onJoinContestAction(ActionEvent event) {
        String roomName = (String) this.contestNameComboBox.getSelectionModel().getSelectedItem();
        if (roomName.isEmpty()) {
            parentController.showMessage("Room name cannot be empty");
            return;
        }
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiJoinContestUrl())
                .newBuilder()
                .addQueryParameter(PropertiesService.getRoomNameAttribute(), roomName)
                .build()
                .toString();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to join - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to join - server error");
                    log.error("Failed to to join a room - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                Gson gson = new Gson();
                ContestRoomPayload payload = gson.fromJson(responseBody,ContestRoomPayload.class);
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to join a room - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to join a room - "+ payload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        log.info("Successfully joined room:\"" + roomName + "\", status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Successfully joined room:" + roomName);
                        DataService.getCurrentContestRoomStateProperty().set(payload.getContestRoom());
                        parentController.changeSceneToContest();
                        DataService.startPullingTeamsData();
                        DataService.getDmProgressProperty().setValue(null);
                        DataService.startPullingContestRoomData();
                        parentController.getContestPageController().getProgressBar().setProgress(0);
                    });
                }
            }
        });
    }

//    public void handleContestClicked(Label battlefieldNameLabel) {
//        chosenContestTextField.setText(battlefieldNameLabel.getText());
//    }

}
