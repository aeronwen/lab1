package org.kp.lab1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Контроллер главного окна (main-view.fxml). Вся разметка в одном FXML.
 */
public class MainController implements Initializable {

    private final TimeServer timeServer;

    @FXML
    private Label timeStateLabel;
    @FXML
    private VBox componentsContainer;
    @FXML
    private Label componentOneTimeLabel;
    @FXML
    private TextField delayField;
    @FXML
    private MediaView mediaView;
    @FXML
    private Label placeholderLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Button buttonPlay;
    @FXML
    private Button buttonStop;
    @FXML
    private Pane shapePane;
    @FXML
    private Button buttonOn;
    @FXML
    private Button buttonOff;

    public MainController(TimeServer timeServer) {
        this.timeServer = timeServer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeStateLabel.setMaxWidth(Double.MAX_VALUE);
        timeStateLabel.setText("Состояние времени: " + timeServer.getState() + " сек (неактивен)");
        timeServer.attach(() -> updateTimeLabel());

        ComponentOne componentOne = new ComponentOne(timeServer, componentOneTimeLabel);
        ComponentTwo componentTwo = new ComponentTwo(timeServer, delayField, mediaView, placeholderLabel, statusLabel);
        ComponentThree componentThree = new ComponentThree(timeServer, shapePane);

        componentTwo.init();

        buttonPlay.setOnAction(e -> componentTwo.start());
        buttonStop.setOnAction(e -> componentTwo.stop());
        buttonOn.setOnAction(e -> componentThree.onOn());
        buttonOff.setOnAction(e -> componentThree.onOff());

        for (IObserver observer : List.of(componentOne, componentTwo, componentThree)) {
            timeServer.attach(observer);
        }
    }

    private void updateTimeLabel() {
        int state = timeServer.getState();
        String active = timeServer.isRunning() ? "активен" : "неактивен";
        javafx.application.Platform.runLater(() ->
                timeStateLabel.setText("Состояние времени: " + state + " сек (" + active + ")"));
    }

    @FXML
    private void onStart() { timeServer.start(); }

    @FXML
    private void onStop() { timeServer.stop(); }

    @FXML
    private void onReset() { timeServer.reset(); }
}
