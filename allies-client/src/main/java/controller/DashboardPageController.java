package controller;

import com.google.gson.Gson;
import dto.LoginPayload;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import jsonadapter.LoginPayloadJsonAdapter;
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
    AppController parentController;

    @FXML
    private GridPane rootGridPane;

    public void setParentController(AppController appController) {
        this.parentController = appController;
    }

    public GridPane getRootComponent() {
       return  rootGridPane;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onRefreshContests(ActionEvent actionEvent) {
//        //noinspection ConstantConditions
//        String finalUrl = HttpUrl
//                .parse(PropertiesService.getApiLoginPageUrl())
//                .newBuilder()
//                .build()
//                .toString();
//
//        log.info("New request is sent for: " + finalUrl);
//        HttpClientService.runAsync(finalUrl, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Platform.runLater(() -> {
//                    parentController.showMessage("Failed to get contests data - cannot contact server");
//                    log.error("Request with URL=\"" + finalUrl + "\" FAILED, exception message=" + e.getMessage());
//                });
//            }
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String responseBody = response.body().string();
//                if (response.code() >= 500){
//                    parentController.showMessage("Failed to get contests data - server error");
//                    log.error("Failed to login to server - status=" + response.code() + " body=" + responseBody);
//                    return;
//                }
//                Gson gson = new Gson();
////                LoginPayload loginPayload = gson.fromJson(responseBody,LoginPayload.class);
//                if (response.code() != 200) {
//                    Platform.runLater(() -> {
//                        log.warn("Failed to login to server - status=" + response.code() + " body=" + responseBody);
//                        parentController.showMessage("Failed to login - "+ loginPayload.getMessage());
//                    });
//                } else {
//                    Platform.runLater(() -> {
//                        log.info("Successfully Logged in as :\"" + username + "\", status=" + response.code() + ", response body=" + responseBody);
//                        parentController.showMessage("Successfully Logged in as :" + username);
//                    });
//                }
//            }
//        });
    }
}
