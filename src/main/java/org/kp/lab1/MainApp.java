package org.kp.lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Точка входа. Окно 800×600. */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        TimeServer timeServer = new TimeServer();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/kp/lab1/main-view.fxml"));
        loader.setControllerFactory(cl -> cl == MainController.class ? new MainController(timeServer) : null);

        Parent root = loader.load();
        stage.setTitle("lab1");
        stage.setScene(new Scene(root, 800, 600));
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
