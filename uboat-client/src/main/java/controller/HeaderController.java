package controller;

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
import service.DataService;
import service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.NotificationPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final SimpleStringProperty notificationMessageProperty = new SimpleStringProperty();
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
    @FXML Button uploadButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        machineSceneNavButton.disableProperty().bind(isFileSelected.not());
        contestSceneNavButton.disableProperty().bind(DataService.getCurrentMachineStateProperty().isNotNull().not());
        selectedFileName.textProperty().bindBidirectional(selectedFileNameProperty);
        createNotificationPane();
    }

    public StringProperty selectedFileProperty(){
        return selectedFileNameProperty;
    }

    public NotificationPane getRootComponent(){
        return notificationPane;
    }

    private void createNotificationPane(){
        this.notificationPane = new NotificationPane(this.getRootComponent());
        notificationPane.setVisible(true);
        notificationPane.textProperty().bind(notificationMessageProperty);
        notificationPane.autosize();
        notificationPane.setContent(headerComponentRootPane);
        notificationPane.setPrefWidth(headerComponentRootPane.getPrefWidth());
        notificationPane.setPrefHeight(headerComponentRootPane.getPrefHeight());
        notificationPane.getStyleClass().add("notify");
        notificationMessageProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == null || newValue.equals("")){
                    return;
                }
                try{
                    log.debug("Notification pane listener - calling .show()");
                    notificationPane.show();
                }
                catch(Exception e){
                    log.error("Error showing message=" + newValue + " exception=" + e.getMessage());
                }
            }
        });
    }

    public void showMessage(String message){
        if (message == null || message.trim().equals("")) {
            return;
        }
        notificationMessageProperty.setValue("");
        notificationMessageProperty.setValue(message);
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
//        isFileSelected.setValue(true);
        selectedFileNameProperty.setValue(absolutePath);
    }

    @FXML
    public void onChangeSceneToContestButtonClick(ActionEvent actionEvent) {
        if(parentController!=null){
            parentController.changeSceneToContest();
        }
    }

    @FXML
    void onChangeSceneToMachineButtonClick(ActionEvent event) {
        if(parentController!=null){
            parentController.changeSceneToMachine();
        }
    }

    public void onUploadButton(ActionEvent actionEvent) throws IOException {
        if(selectedFileNameProperty.get() == null){
            parentController.showMessage("Please choose a file first");
            return;
        }
        Path p = Paths.get(selectedFileProperty().get());
        if (!p.isAbsolute()) {
            parentController.showMessage("\""+ selectedFileProperty().get() +"\" is not an absolute path");
            return;
        }
        parentController.handleUploadFile(selectedFileProperty().get());
    }

    @FXML
    void onLogoutButtonClick(ActionEvent event) {
        if(parentController!=null){
            parentController.handleLogout();
        }
        else{
            log.error("Failed to logout - ParentController is null");
        }
    }

    public void onChangeSceneToDashboardButtonClick(ActionEvent actionEvent) {
    }
}



