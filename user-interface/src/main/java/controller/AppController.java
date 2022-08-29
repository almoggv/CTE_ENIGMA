package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import main.java.component.MachineHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private MachineHandler machineHandler;
    @Getter
    @FXML HeaderController headerController;
    @Getter
    @FXML MachinePageController machinePageController;
    @Getter
    @FXML EncryptPageController encryptPageController;

    @FXML private AnchorPane headerWrapAnchorPane;
    @FXML private ScrollPane headerWrapScrollPane;
    @FXML private AnchorPane bodyWrapAnchorPane;
    @FXML private ScrollPane bodyWrapScrollPane;

    public void setMachineHandler(MachineHandler machineHandler) {
        this.machineHandler = machineHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(headerController!=null){
            headerController.setParentController(this);
        }
        if(machinePageController!=null){
            machinePageController.setParentController(this);
        }
        if(encryptPageController!=null){
            encryptPageController.setParentController(this);
        }
    }

    public void changeSceneToMachine(){
        Parent rootComponent = machinePageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }

    public void changeSceneToEncrypt() {
        Parent rootComponent = encryptPageController.getRootComponent();
        bodyWrapScrollPane.setContent(rootComponent);
    }

    public void setHeaderController(HeaderController headerController) {
        this.headerController = headerController;
        headerController.setParentController(this);
    }

    public void setMachinePageController(MachinePageController machinePageController) {
        this.machinePageController = machinePageController;
        machinePageController.setParentController(this);
    }

    public void setEncryptPageController(EncryptPageController encryptPageController) {
        this.encryptPageController = encryptPageController;
        encryptPageController.setParentController(this);
    }

    public void makeBodyVisible() {
        bodyWrapScrollPane.setVisible(true);
    }

    public void loadFile(String absolutePath) {
        try {
            machineHandler.buildMachinePartsInventory(absolutePath);
            System.out.println("File Loaded Successfully");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
