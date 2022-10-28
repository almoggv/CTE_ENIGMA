package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.controlsfx.control.NotificationPane;
import service.DataService;
import service.PropertiesService;

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

    @Getter private final BooleanProperty isLoggedInProperty = new SimpleBooleanProperty(false);
    private final SimpleStringProperty notificationMessageProperty = new SimpleStringProperty();
    private NotificationPane notificationPane;

    @FXML GridPane headerComponentRootPane;
    @FXML Label titleLabel;
    @FXML HBox componentNavButtonsHBox;
    public Button logoutButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createNotificationPane();

        DataService.getCurrentContestRoomsStateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) { //contest running
                logoutButton.setDisable(true);
            }
            else{
                logoutButton.setDisable(false);
            }
        });
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
    void onLogoutButtonClick(ActionEvent event) {
        this.isLoggedInProperty.setValue(false);

//        if(parentController!=null){
//            parentController.handleLogout();
//        }
//        else{
//            log.error("Failed to logout - ParentController is null");
//        }
    }
}



