package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;

import lombok.Getter;
import lombok.Setter;
import src.main.java.service.PropertiesService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class HeaderController implements Initializable {
    @Setter @Getter
    private AppController parentController;
    @FXML private GridPane rootPane;
    @FXML private Label titleLabel;
    @FXML private HBox browseFilesHBox;
    @FXML private Button browseFilesButton;
    @FXML private TextField browseFilesText;
    @FXML Label selectedFileName;
    @FXML private HBox componentNavButtonsHBox;
    @FXML private Button machineSceneNavButton;
    @FXML private Button encryptSceneNavButton;
    @FXML private Button bruteForceSceneNavButton;

    @FXML
    private Label messageLabel;
    @Setter private Stage primaryStage;
    @Getter private SimpleStringProperty selectedFileProperty;
    @Getter private SimpleBooleanProperty isFileSelected;

    public HeaderController (){
        selectedFileProperty = new SimpleStringProperty();
        isFileSelected = new SimpleBooleanProperty(false);
    }
    @FXML
    void onBrowseFilesButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        selectedFileProperty.set(absolutePath);
        boolean isFileValid = parentController.handleFileChosen(absolutePath);
        isFileSelected.set(isFileValid);
    }

    @FXML
    void onChangeSceneToMachineButtonClick(ActionEvent event) {
//        parentController.changeCenterComponent(PropertiesService.getCurrMachineConfigTemplateFxmlPath());;
        parentController.changeCenterComponent(PropertiesService.getMachinePageWithIncludesFxmlPath());;
    }

    @FXML
    void onChangeSceneToEncryptButtonClick(ActionEvent event) {
        parentController.changeCenterComponent(PropertiesService.getEncryptPageTemplateFxmlPath());;

//        parentController.changeSceneToEncrypt();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFileName.textProperty().bind(selectedFileProperty);
        machineSceneNavButton.disableProperty().bind(isFileSelected.not());
        //todo add other connections
        encryptSceneNavButton.disableProperty().bind(isFileSelected.not());
        bruteForceSceneNavButton.disableProperty().bind(isFileSelected.not());
    }

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
    }
}



