package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class EncryptPageController implements Initializable {

    @Getter @Setter
    @FXML AppController parentController;

    @FXML private GridPane rootGrid;
    @FXML private TextField testTextArea;

    public Parent getRootComponent(){
        return rootGrid;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}