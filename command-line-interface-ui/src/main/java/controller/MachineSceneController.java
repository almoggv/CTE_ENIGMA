package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import src.main.java.handler.PropertiesService;


import java.io.IOException;

public class MachineSceneController {
    private Stage stage;
    private Scene bruteForceScene;
    private Scene machineScene;
    private Scene encryptScene;
    @FXML
    private Button browseFilesButton;

    @FXML
    private TextField browseFileTextField;

    @FXML
    private Button machineSceneButton;

    @FXML
    private Button encryptSceneButton;

    @FXML
    private Button bruteForceSceneButton;

    public void onBruteForceSceneButtonClickedListener(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(PropertiesService.getBruteForceSceneFxmlPath()));
        stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        System.out.println(stage.getScene().getProperties().keySet());
        System.out.println(stage.getScene().getProperties().values());
        bruteForceScene = new Scene(root);
        stage.setScene(bruteForceScene);
        stage.show();
    }
}
