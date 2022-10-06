package controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @Getter
    @Setter
    private AppController parentController;

    @Getter private final SimpleStringProperty nameProperty = new SimpleStringProperty();
    @Getter private final SimpleBooleanProperty isNameSelected = new SimpleBooleanProperty(false);


    @FXML
    private TextField loginTextField;

    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginTextField.textProperty().bindBidirectional(nameProperty);
        loginButton.disableProperty().bind(isNameSelected.not());
        loginTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.equals("")){
                isNameSelected.setValue(true);
            }
            else {
                isNameSelected.setValue(false);
            }
        });
    }

    @FXML
    void onLoginButtonAction(ActionEvent event) {
        if(nameProperty.get() != null && parentController!= null){
            parentController.handleLogin(nameProperty.get());
        };
    }


}
