package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    HeaderController headerController;

    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;

    public void setHeaderController(HeaderController headerController){
        this.headerController = headerController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
