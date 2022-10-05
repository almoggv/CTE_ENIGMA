module com.example.javafxtest {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;

    opens com.example.javafxtest to javafx.fxml;
    exports com.example.javafxtest;
}