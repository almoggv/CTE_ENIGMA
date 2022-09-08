package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import javafx.beans.property.SimpleStringProperty;

import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.NotificationPane;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @Setter @Getter private AppController parentController;
    @FXML private GridPane headerComponentRootPane;
    @FXML private Label titleLabel;
    @FXML private HBox browseFilesHBox;
    @FXML private Button browseFilesButton;
    @FXML private TextField selectedFileName;
    @FXML private Label selectedFileLabel;
    @FXML private HBox componentNavButtonsHBox;
    @FXML private Button machineSceneNavButton;
    @FXML private Button encryptSceneNavButton;
    @FXML private Button bruteForceSceneNavButton;
    @FXML private ChoiceBox<String> themeChoiceBox;
    private NotificationPane notificationPane;

    @Getter private SimpleStringProperty selectedFileProperty = new SimpleStringProperty();;
    @Getter private SimpleBooleanProperty isFileSelected = new SimpleBooleanProperty(false);
    @Getter private SimpleStringProperty notificationMessageProperty = new SimpleStringProperty();;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFileName.textProperty().bind(selectedFileProperty);
        machineSceneNavButton.disableProperty().bind(isFileSelected.not());
        encryptSceneNavButton.disableProperty().bind(DataService.getCurrentMachineStateProperty().isNotNull().not());
        bruteForceSceneNavButton.disableProperty().bind(DataService.getCurrentMachineStateProperty().isNotNull().not());
        createNotificationPane();
        initializeThemeBox();
    }

    private void initializeThemeBox(){
        for (String themeName : ResourceLocationService.getCssThemeToFileMap().keySet()) {
            themeChoiceBox.getItems().add(themeName);
            themeChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                    String themeResourcePath = ResourceLocationService.getCssThemeToFileMap().getOrDefault(newValue,"");
                    URL themeUrl = null;
                    try{
                        themeUrl = HeaderController.class.getResource(themeResourcePath);
                    }
                    catch(Exception ignore){}
                    parentController.loadCssFile(themeUrl);
                }
            });
        }
    }

    public NotificationPane getRootComponent(){
        return notificationPane;
    }

    @FXML
    void onBrowseFilesButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(parentController.getPrimaryStage());
        if (selectedFile == null) {
            return;
        }
        String absolutePath = selectedFile.getAbsolutePath();
        try{
            parentController.handleFileChosen(absolutePath);
            selectedFileProperty.set(absolutePath);
            isFileSelected.set(true);
        }
        catch (Exception e){
            notificationMessageProperty.setValue(e.getMessage());
        }


    }

    @FXML
    void onChangeSceneToMachineButtonClick(ActionEvent event) {
        parentController.changeSceneToMachine();
    }

    @FXML
    void onChangeSceneToEncryptButtonClick(ActionEvent event) {
        parentController.changeSceneToEncrypt();
    }

    private void createNotificationPane(){
        this.notificationPane = new NotificationPane(this.getRootComponent());
        notificationPane.textProperty().bind(notificationMessageProperty);
        notificationPane.autosize();
        notificationPane.setContent(headerComponentRootPane);
        notificationPane.setPrefWidth(headerComponentRootPane.getPrefWidth());
        notificationPane.setPrefHeight(headerComponentRootPane.getPrefHeight());
        notificationMessageProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                notificationPane.show();
            }
        });
    }

    public void onSelectFileTextFieldAction(ActionEvent actionEvent) {
        //todo - maybe add write path of file
    }
}



