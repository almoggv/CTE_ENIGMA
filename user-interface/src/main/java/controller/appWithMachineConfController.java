package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import src.main.java.handler.PropertiesService;
import src.main.java.ui.GuiApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class appWithMachineConfController implements Initializable {
    @FXML
//    private AnchorPane headerAnchorPane;
    private GridPane machineConfStage;
    @FXML
    private GridPane headerPane;
    @FXML
    private GridPane encryptStage;
    @FXML
    private BorderPane rootBorderPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("in app controller");



       encryptStage.setVisible(false);
        encryptStage.setMaxSize(0,0);
//        rootBorderPane.getRight().setVisible(false);


//        rootBorderPane.setCenter(encryptStage);
//        rootBorderPane.setRight(machineConfStage);

    }
}
