package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class HeaderController {

    @FXML
    private Button browseFilesButton;

    @FXML
    private TextField browseFilesText;

    @FXML
    private Button MachineSceneNavButton;

    @FXML
    private Button encryptSceneNavButton;

    @FXML
    private Button bruteForceSceneNavButton;

    @FXML
    void onBrowseFilesButtonClicked(MouseEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        File selectedFile = fileChooser.showOpenDialog(mainStage);
//        if (selectedFile != null) {
//            mainStage.display(selectedFile);
//        }
    }


    @FXML
    void onMachineSceneNavButtonClicked(MouseEvent event) {

    }
}
