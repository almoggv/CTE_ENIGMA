package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

public class SetMachineConfigurationController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private GridPane innerGridPane;

    @FXML
    private Label reflectorLabel;

    @FXML
    private Label rotorsLabel;

    @FXML
    private Label plugboardLabel;

    @FXML
    private ChoiceBox<String> reflectorChoiceBox;

    @FXML
    private HBox rotorsHbox;

    @FXML
    private ChoiceBox<?> rotorChoice2;

    @FXML
    private ChoiceBox<?> rotorChoice1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setReflectorChoiceBox();
    }

    private void setReflectorChoiceBox() {
         String[] refs = {"I", "II", "III", "IV"};

        reflectorChoiceBox.getItems().addAll(refs);
    }
}
