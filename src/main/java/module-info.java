module com.emaple.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires opencv;
    requires libtensorflow;
    opens com.emaple.demo2 to javafx.fxml;
    exports com.emaple.demo2;
}