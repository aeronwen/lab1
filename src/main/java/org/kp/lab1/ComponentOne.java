package org.kp.lab1;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Компонент 1 — наблюдатель. Показывает «Время: N сек». Разметка в main-view.fxml.
 */
public class ComponentOne implements IObserver {

    private final Subject subject;
    private final Label timeLabel;

    public ComponentOne(Subject subject, Label timeLabel) {
        this.subject = subject;
        this.timeLabel = timeLabel;
    }

    @Override
    public void update() {
        Platform.runLater(() -> timeLabel.setText("Время: " + subject.getState() + " сек"));
    }
}
