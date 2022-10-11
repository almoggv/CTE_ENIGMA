package controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

public class DashboardPageController {

    AppController parentController;

    @FXML
    private GridPane rootGridPane;

    public void setParentController(AppController appController) {
        this.parentController = appController;
    }

    public GridPane getRootComponent() {
       return  rootGridPane;
    }
}
