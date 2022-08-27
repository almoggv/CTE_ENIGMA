package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;


public class HeaderController implements Initializable {

    private AppController parentController;

    @FXML private GridPane rootPane;
    @FXML private Label titleLabel;
    @FXML private HBox browseFilesHBox;
    @FXML private Button browseFilesButton;
    @FXML private TextField browseFilesText;
    @FXML private HBox componentNavButtonsHBox;
    @FXML private Button machineSceneNavButton;
    @FXML private Button encryptSceneNavButton;
    @FXML private Button bruteForceSceneNavButton;

    public void setParentController(AppController parentController){
        this.parentController = parentController;
    }

    @FXML
    void onBrowseFilesButtonClicked(MouseEvent event) {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}



