package src.main.java.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import javafx.beans.property.SimpleStringProperty;

import lombok.Getter;
import lombok.Setter;
import main.java.service.PropertiesService;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.NotificationPane;
import src.main.java.service.DataService;
import src.main.java.service.ResourceLocationService;

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

    public MenuBar menuBar;
    public Menu themeMenu;
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

    @FXML
    void onChangeSceneToBruteForceButtonClick(ActionEvent event) {
        parentController.changeSceneToBruteForce();
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

    public void onSelectFileTextFieldAction(ActionEvent actionEvent) {
        //todo - maybe add write path of file
    }

    public void onAnimationsOnAction(ActionEvent actionEvent) {
        DataService.getIsAnimationOn().setValue(true);
    }

    public void onAnimationsOffAction(ActionEvent actionEvent) {
        DataService.getIsAnimationOn().setValue(false);
    }

    public void onDarkThemeMenuAction(ActionEvent actionEvent) {
        changeTheme(ResourceLocationService.getDarkThemeName());
    }

    public void onGreenThemeMenuAction(ActionEvent actionEvent) {
        changeTheme(ResourceLocationService.getLightThemeName());
    }

    private void changeTheme(String themeName){
        String themeResourcePath = ResourceLocationService.getCssThemeToFileMap().getOrDefault(themeName,ResourceLocationService.getLightThemeCssPath());
        URL themeUrl = null;
        String themeString ="";
        try{
            themeUrl = HeaderController.class.getResource(themeResourcePath);
            themeString = HeaderController.class.getResource(themeResourcePath).toExternalForm();

            log.info("HeaderController - Theme Url =" + themeUrl);
            log.debug("HeaderController - Theme String Path=" + themeResourcePath);
        }
        catch(Exception ignore){}
        parentController.loadCssFile(themeString);
    }

    public void onOriginalThemeMenuAction(ActionEvent actionEvent) {
        changeTheme(ResourceLocationService.getDefaultThemeName());
    }
}



