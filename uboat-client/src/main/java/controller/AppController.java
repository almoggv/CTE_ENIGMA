package controller;

import com.google.gson.Gson;
import dto.InventoryInfo;
import dto.MachineInventoryPayload;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import service.DataService;
import service.HttpClientService;
import service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private static final Logger log = Logger.getLogger(AppController.class);
    static {
        try {
            Properties p = new Properties();
            p.load(AppController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + AppController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + AppController.class.getSimpleName());
        }
    }

    @FXML
    @Getter
    HeaderController headerComponentController;

    @FXML
    @Getter
    LoginController loginComponentController;
    @FXML
    private MachinePageController machinePageController;

    @Setter
    @Getter
    private Stage primaryStage;
    @FXML
    private BorderPane appBorderPane;
    @FXML
    private AnchorPane bodyWrapAnchorPane;
    @FXML
    private ScrollPane bodyWrapScrollPane;
    @FXML
    private GridPane imageGrid;
    @FXML
    private AnchorPane headerWrapAnchorPane;
    @FXML
    private ScrollPane headerWrapScrollPane;

    @FXML
    GridPane headerComponent;

    @FXML
    GridPane loginComponent;

    public void showMessage(String message) {
        if(headerComponentController!=null){
            headerComponentController.showMessage(message);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (headerComponentController != null) {
            headerComponentController.setParentController(this);
        }
        if(loginComponentController != null){
            loginComponentController.setParentController(this);
        }
        FXMLLoader fxmlLoader;
        //Load Current Machine Config
        URL currConfigResource = AppController.class.getResource(PropertiesService.getCurrMachineConfigFxmlPath());
        log.info("AppController - found Url of header component:" + currConfigResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(currConfigResource);
        try {
            GridPane currConfigComponent = fxmlLoader.load(currConfigResource.openStream());
        } catch (IOException e) {
            log.error("Failed to load currConfigResource:" + currConfigResource.toString() + "Exception throws: " + e.getMessage());
        }
        CurrMachineConfigController currMachineConfigController = fxmlLoader.getController();
        //Load MachinePage
        URL machinePageResource = AppController.class.getResource(PropertiesService.getMachinePageTemplateFxmlPath());
        log.info("AppController - found Url of machine component:" + machinePageResource);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(machinePageResource);
        try {
            Parent machinePageComponent = fxmlLoader.load(machinePageResource.openStream());
        } catch (IOException e) {
            log.error("Failed to load machinePageResource:" + machinePageResource.toString() + "Exception throws: " + e.getMessage());
        }
        machinePageController = fxmlLoader.getController();
        machinePageController.setParentController(this);
        machinePageController.bindComponent(currMachineConfigController);
//      //TODO: Load Contest Page

        headerWrapScrollPane.setContent(headerComponentController.getRootComponent());
        loginComponentController.getIsLoggedInProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==true){
                this.changeSceneToMachine();
            }
            else{
                this.changeSceneToLogin();
            }
        });
        log.info("AppController - app initialized");
    }

    public void changeSceneToMachine() {
        if(machinePageController != null){
            Parent rootComponent = machinePageController.getRootComponent();
            bodyWrapScrollPane.setContent(rootComponent);
        }
        else{
            log.error("Failed to change scene to machine - machineController == null");
        }
    }

    public void changeSceneToLogin(){
        if(loginComponentController!=null){
            Parent rootComponent = loginComponentController.getRootComponent();
            bodyWrapScrollPane.setContent(imageGrid);
        }
        else{
            log.error("Failed to change scene to login - loginComponentController == null");
        }
    }

//    private void getMachineInventory() {
//        String finalUrl = HttpUrl
//                .parse(PropertiesService.getApiInventoryPageUrl())
//                .newBuilder()
//                .build()
//                .toString();
//
//        System.out.println("New request is launched for: " + finalUrl);
//        HttpClientService.runAsync(finalUrl, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Platform.runLater(() ->
//                        System.out.println("Something went wrong: " + e.getMessage())
//                );
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (response.code() != 200) {
//                    String responseBody = response.body().string();
//                    Platform.runLater(() ->
//                            System.out.println("Something went wrong: " + responseBody)
//                    );
//                } else {
//                    Platform.runLater(() -> {
//                        System.out.println("loaded inventory");
//                    });
//                }
//            }
//        });
//
//    }

    public void changeSceneToContest() {
        //TODO: check null
//        Parent rootComponent = contestPageController.getRootComponent();
//        bodyWrapScrollPane.setContent(rootComponent);
        throw new UnsupportedOperationException();
    }

    public void makeBodyVisible() {
        bodyWrapScrollPane.setVisible(true);
    }

    public void handleUploadFile(String absolutePath) throws IOException {
        Path p = Paths.get(absolutePath);
        if (!p.isAbsolute()) {
            this.showMessage("\"" + absolutePath + "\" is not an absolute path");
            return;
        }

        //todo - decide if need to fix issue here - thinks folder is a file
        File f = new File(absolutePath);
        if(!f.exists()){
            this.showMessage("Problem with file, doesn't exist " + absolutePath);
            return;
        }
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("file", f.getName(), RequestBody.create(f, MediaType.parse("text/plain")))
                        //.addFormDataPart("key1", "value1") // you can add multiple, different parts as needed
                        .build();
        String finalUrl = HttpUrl
                .parse(PropertiesService.getApiUploadPageUrl())
                .newBuilder()
//                .addQueryParameter("username", userName)
                .build()
                .toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();
        HttpClientService.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() >= 500) {
                    Platform.runLater(() ->{
                        log.error("Failed to UploadFile  - statusCode=" + response.code());
                        showMessage("A Server side error occurred");
                    });
                    return;
                }
                MachineInventoryPayload inventoryPayload;
                try{
                    inventoryPayload = new Gson().fromJson(responseBody,MachineInventoryPayload.class);
                }
                catch (Exception e){
                    log.error("Failed to parse response on FileUpload, Message=" + e.getMessage());
                    Platform.runLater(()->showMessage("Ran into a problem uploading the file"));
                    return;
                }
                if (response.code() != 200) {
                    Platform.runLater(() ->{
                        log.error("Failed to UploadFile  - statusCode=" + response.code() + ", ServerMessage=" + inventoryPayload.getMessage());
                        showMessage(inventoryPayload.getMessage());
                    });
                }
                else {
                    Platform.runLater(() -> {
                        log.info("File Uploaded Successfully - responseCode = 200, ServerMessage="+ inventoryPayload.getMessage());
                        showMessage("Uploaded machine file successfully");
                        headerComponentController.getIsFileSelected().set(true);
                        DataService.fetchInventoryInfo(inventoryPayload.getInventory());
                    });
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    log.error("Failed to UploadFile - OnFailure triggered, ExceptionMessage=" + e.getMessage());
                    showMessage("Failed to contact server");
                });
            }
        });
    }

    public void handleLogout() {
        if(loginComponentController!=null){
            loginComponentController.handleLogout();
        }
        else{
            log.error("Failed to logout - loginComponentController is null");
        }
    }
}


