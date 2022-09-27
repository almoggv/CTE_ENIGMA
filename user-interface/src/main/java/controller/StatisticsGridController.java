package src.main.java.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import main.java.dto.EncryptionInfoHistory;

import java.util.List;

public class StatisticsGridController {

    @FXML
    private AnchorPane statisticsAnchor;

    @FXML
    private ScrollPane statisticsScroller;

    @FXML
    private GridPane statisticsGridOfScroll;

    @FXML
    private ListView<String> listViewOriginal;

    @FXML
    private ListView<String> listViewNanoSecs;

    @FXML
    private ListView<String> listViewEncrypted;
    public void addHistory(List<EncryptionInfoHistory> encryptionInfoHistoryList) {
        for (EncryptionInfoHistory encryptionHistory : encryptionInfoHistoryList) {
            listViewOriginal.getItems().add(encryptionHistory.getInput());
            listViewEncrypted.getItems().add(encryptionHistory.getOutput());
            listViewNanoSecs.getItems().add(String.valueOf(encryptionHistory.getTimeToEncrypt()));
        }
    }
}
