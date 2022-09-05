package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StatisticsTableController {

    @FXML
    private TableView<?> statisticsTable;

    @FXML
    private TableColumn<String, String> statisticsColOriginal;

    @FXML
    private TableColumn<?, ?> statisticsColEncrypted;

    @FXML
    private TableColumn<?, ?> statisticsColTime;

    public StatisticsTableController(){
        statisticsColOriginal = new TableColumn<String, String>("Original");
        statisticsColEncrypted = new TableColumn<>("Encrypted");
        statisticsColTime = new TableColumn<>("Nano-secs");
        putStatisticsInTable();
    }

    private void putStatisticsInTable() {

    }

    @FXML
    public void initialize() {
        statisticsColOriginal.setCellValueFactory(new PropertyValueFactory<String, String>("id"));
//        statisticsColEncrypted.setCellValueFactory(new PropertyValueFactory<Subject,String>("name"));
//        statisticsColTime.setCellValueFactory(new PropertyValueFactory<Teacher, String>("id"));
    }
}
