package org.kp.lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Точка входа приложения (класс с main).
 *
 * Создаёт экземпляр TimeServer, загружает main-view.fxml и подставляет MainController
 * с передачей timeServer (через setControllerFactory). Окно 800×600, заголовок "lab1".
 */
public class MainApp extends Application {

    /**
     * Создаёт TimeServer, загружает разметку из /org/kp/lab1/main-view.fxml,
     * задаёт фабрику контроллера так, чтобы MainController получал timeServer в конструкторе,
     * создаёт сцену и отображает окно.
     */
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
