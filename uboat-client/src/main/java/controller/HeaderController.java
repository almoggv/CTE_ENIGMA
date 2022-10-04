package main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import main.java.service.DataService;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.NotificationPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    private static final Logger log = Logger.getLogger(HeaderController.class);

    static {
        try {
            Properties p = new Properties();
            p.load(HeaderController.class.getResourceAsStream(PropertiesService.getLog4jPropertiesResourcePath()));
            PropertyConfigurator.configure(p);      //Dont forget here
            log.debug("Logger Instantiated for : " + HeaderController.class.getSimpleName());
        } catch (IOException e) {
            System.out.println("Failed to configure logger of -" + HeaderController.class.getSimpleName() ) ;
        }
    }

    @Getter @Setter private AppController parentController;

    @Getter private final SimpleStringProperty selectedFileNameProperty = new SimpleStringProperty();
    @Getter private final SimpleBooleanProperty isFileSelected = new SimpleBooleanProperty(false);

    @Getter private final SimpleStringProperty notificationMessageProperty = new SimpleStringProperty();
    private NotificationPane notificationPane;

    @FXML GridPane headerComponentRootPane;
    @FXML Label titleLabel;
    @FXML HBox browseFilesHBox;
    @FXML Label selectedFileLabel;
    @FXML TextField selectedFileName;
    @FXML Button browseFilesButton;
    @FXML HBox componentNavButtonsHBox;
    @FXML Button machineSceneNavButton;
    @FXML Button contestSceneNavButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFileName.textProperty().bind(selectedFileNameProperty);
        machineSceneNavButton.disableProperty().bind(isFileSelected.not());
        contestSceneNavButton.disableProperty().bind(DataService.getCurrentMachineStateProperty().isNotNull().not());
//  ======================================================
        supportPastePathingInTextField();
//  ======================================================
        createNotificationPane();
    }

    private void supportPastePathingInTextField(){
        // paste path
        selectedFileName.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(parentController!=null){
                parentController.handleFileChosen(newValue);
            }
        }));
    }

    public StringProperty selectedFileProperty(){
        return selectedFileNameProperty;
    }

    public GridPane getRootComponent(){
        return headerComponentRootPane;
    }

    private void createNotificationPane(){
        this.notificationPane = new NotificationPane(this.getRootComponent());
        notificationPane.textProperty().bind(notificationMessageProperty);
        notificationPane.autosize();
        notificationPane.setContent(headerComponentRootPane);
        notificationPane.setPrefWidth(headerComponentRootPane.getPrefWidth());
        notificationPane.setPrefHeight(headerComponentRootPane.getPrefHeight());
        notificationPane.getStyleClass().add("notify");
        notificationMessageProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                notificationPane.show();
            }
        });
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
        parentController.handleFileChosen(absolutePath);
    }

//    @FXML
//    public void onChangeSceneToContestButtonClick(ActionEvent actionEvent) {
//        if(parentController!=null){
//            parentController.changeSceneToMachine();
//        }
//    }

    @FXML
    void onChangeSceneToMachineButtonClick(ActionEvent event) {
        if(parentController!=null){
            parentController.changeSceneToContest();
        }
    }




}



