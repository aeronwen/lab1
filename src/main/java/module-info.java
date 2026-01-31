module org.kp.lab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens org.kp.lab1 to javafx.fxml;
    exports org.kp.lab1;
}