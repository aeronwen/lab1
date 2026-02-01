package org.kp.lab1;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Компонент 1 — первый наблюдатель (IObserver).
 *
 * Всегда показывает текущее время в формате "Время: N сек".
 *
 * Разметка (заголовок и метка времени) задаётся в main-view.fxml; ссылка на Label
 * передаётся из MainController в конструктор.
 */
public class ComponentOne implements IObserver {

    /** Субъект (TimeServer), у которого запрашиваем getState() для отображения секунд. */
    private final Subject subject;

    /** Метка для отображения текста "Время: N сек" (элемент из FXML). */
    private final Label timeLabel;

    /**
     * @param subject субъект времени (TimeServer)
     * @param timeLabel метка из main-view.fxml (fx:id="componentOneTimeLabel")
     */
    public ComponentOne(Subject subject, Label timeLabel) {
        this.subject = subject;
        this.timeLabel = timeLabel;
    }

    /**
     * Вызывается TimeServer каждую секунду. Обновляет текст метки на "Время: N сек",
     * где N — текущее значение subject.getState(). Обновление UI выполняется в потоке JavaFX (Platform.runLater).
     */
    @Override
    public void update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                timeLabel.setText("Время: " + subject.getState() + " сек");
            }
        });
    }
}
