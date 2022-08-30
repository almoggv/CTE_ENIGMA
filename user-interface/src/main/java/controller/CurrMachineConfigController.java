package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;


public class CurrMachineConfigController implements Initializable {

    @Getter @Setter
    @FXML MachinePageController parentController;

    @FXML private GridPane rootGridPane;
    @FXML private HBox rotorsHbox;
    @FXML private Button rotorButton2;
    @FXML private Button rotorButton1;
    @FXML private HBox plugBoardHbox;
    @FXML private Button plugBoardConnection2;
    @FXML private Button plugBoardConnection1;
    @FXML private Button reflectorButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        setRotorsHbox();
//        setPlugBoardHbox();
//        setReflectorLable();
    }

//    private void setRotorsHbox() {
//        rotorsHbox.getChildren().clear();
//
//        for (int i = 0; i < 10; i++) {
//            Button rotor = new Button(new String(String.valueOf(i)));
//            rotorsHbox.getChildren().add(rotor);
//        }
//    }
//
//    private void setPlugBoardHbox() {
//        plugBoardHbox.getChildren().clear();
//        Button plug1 = new Button(new String("a|z"));
//        Button plug2 = new Button(new String("B|C"));
//        Button plug3 = new Button(new String("w|r"));
//        plugBoardHbox.getChildren().addAll(plug1,plug2,plug3);
//    }
//
//    private void setReflectorLable(){
//        reflectorButton.setText("IV");
//    }
}
