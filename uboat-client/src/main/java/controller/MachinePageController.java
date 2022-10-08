package controller;

import com.google.gson.Gson;
import dto.LoginPayload;
import dto.MachineInventoryPayload;
import dto.MachineStatePayload;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import jsonadapter.LoginPayloadJsonAdapter;
import lombok.Getter;
import lombok.Setter;
import dto.MachineState;
import enums.ReflectorsId;
import generictype.MappingPair;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import service.DataService;
import service.HttpClientService;
import service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class MachinePageController implements Initializable {
    private static final Logger log = Logger.getLogger(MachinePageController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(MachinePageController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + MachinePageController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + MachinePageController.class.getSimpleName() ) ;
        }
    }

    @Setter @Getter private SimpleBooleanProperty isMachineConfigured = new SimpleBooleanProperty(false);;
    @Setter @Getter private Boolean isFirstTimeConfigure = true;

    @Setter @Getter @FXML private AppController parentController;
    @Getter @FXML private SetMachineConfigController setMachineConfigurationComponentController;
    @Getter @FXML private CurrMachineConfigController currMachineConfigComponentController;
    @FXML private MachineDetailsController machineDetailsComponentController;

    @FXML public GridPane setMachineConfigurationComponent;
    @FXML public GridPane currMachineConfigComponent;
    @FXML private GridPane rootGridPane;
    @FXML private SplitPane bottomSplitPane;
    @FXML private AnchorPane leftAnchorOfBottom;
    @FXML private ScrollPane scrollOfLeftBottomAnchor;
    @FXML private AnchorPane rightAnchorOfBottom;
    @FXML private ScrollPane scrollOfRightBottomAnchor;
    @FXML private GridPane machineDetailsComponent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(setMachineConfigurationComponentController != null){
            setMachineConfigurationComponentController.setParentController(this);
        }
        if(currMachineConfigComponentController != null){
            currMachineConfigComponentController.setParentController(this);
            currMachineConfigComponentController.bindToData(DataService.getCurrentMachineStateProperty());
        }
        if(machineDetailsComponentController != null){
            machineDetailsComponentController.setParentController(this);
        }
    }

    public void showMessage(String message) {
        parentController.showMessage(message);
    }

    public void handleManuelSetMachinePressed(MachineState userMachineSetupChoices) {
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiAssembleMachineManuallyPageUrl())
                .newBuilder()
                .build()
                .toString();

        MachineStatePayload sendPayload = new MachineStatePayload();
        sendPayload.setMachineState(userMachineSetupChoices);
        String userChoiceStateJson = new Gson().toJson(sendPayload);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), userChoiceStateJson);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to assemble machine - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to assemble machine - server error");
                    log.error("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                MachineStatePayload receiveMachineStatePayload;
                try{
                    receiveMachineStatePayload = new Gson().fromJson(responseBody, MachineStatePayload.class);
                }
                catch (Exception e){
                    log.error("Failed to parse response on assemble machine state randomly, Message=" + e.getMessage());
                    return;
                }
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to assemble machine - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to assemble machine - "+ receiveMachineStatePayload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        log.info("Successfully assembled machine, status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Successfully assembled machine");
                    });
                    DataService.startPullingMachineConfig();
                }
            }
        });
    }

    public void handleRandomSetMachinePressed() {
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiAssembleMachineRandomlyPageUrl())
                .newBuilder()
                .build()
                .toString();

        RequestBody body = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();

        log.info("New request is sent for: " + finalUrl);
        HttpClientService.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    parentController.showMessage("Failed to assemble machine - cannot contact server");
                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500){
                    parentController.showMessage("Failed to assemble machine - server error");
                    log.error("Failed to login to server - status=" + response.code() + " body=" + responseBody);
                    return;
                }
                MachineStatePayload machineStatePayload;
                try{
                    machineStatePayload = new Gson().fromJson(responseBody, MachineStatePayload.class);
                }
                catch (Exception e){
                    log.error("Failed to parse response on assemble machine state randomly, Message=" + e.getMessage());
                    return;
                }
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        log.warn("Failed to assemble machine - status=" + response.code() + " body=" + responseBody);
                        parentController.showMessage("Failed to assemble machine - "+ machineStatePayload.getMessage());
                    });
                } else {
                    Platform.runLater(() -> {
                        log.info("Successfully assembled machine, status=" + response.code() + ", response body=" + responseBody);
                        parentController.showMessage("Successfully assembled machine");
                    });
                    DataService.startPullingMachineConfig();
                }
            }
        });
    }

    public GridPane getRootComponent() {
        return rootGridPane;
    }

    public void bindComponent(CurrMachineConfigController controller){
        this.currMachineConfigComponentController = controller;
        currMachineConfigComponent = currMachineConfigComponentController.getRootGridPane();
    }

}
