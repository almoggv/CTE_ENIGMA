module com.example.decryptionmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires lombok;

    opens com.example.decryptionmanager to javafx.fxml;
    exports com.example.decryptionmanager;
}