package controller;

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

    public void showMessage(String message) {
        if (message == null || message.trim().equals("")) {
            return;
        }
        headerComponentController.getNotificationMessageProperty().setValue("");
        headerComponentController.getNotificationMessageProperty().setValue(message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (headerComponentController != null) {
            headerComponentController.setParentController(this);
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

        System.out.println("app initialized");
        showMessage("app initialized");
//        //TODO: Load Contest Page
    }

    public void changeSceneToMachine() {
        //TODO: check null
        Parent rootComponent = machinePageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }

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

//        Call call = HttpClientService.getHTTP_CLIENT().newCall(request);
//        Response response = call.execute();
//        System.out.println(response.body().string());
//        this.showMessage(response.body().string());

        HttpClientService.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            System.out.println("Something went wrong: " + responseBody)
//                            showMessage("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            System.out.println("Uploaded machine file successfully" + response.body().string());
                        } catch (IOException ignored) {

                        }
//                        showMessage("Uploaded machine file successfully" );
                        headerComponentController.getIsFileSelected().set(true);
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("Something went wrong: " + e.getMessage())
//                        showMessage("Something went wrong: " + e.getMessage())
                );
            }
        });
    }
}


