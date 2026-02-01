package org.kp.lab1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
 * Контроллер главного окна приложения (main-view.fxml).
 *
 * Вся разметка интерфейса описана в одном FXML-файле. При инициализации создаёт
 * TimeServer (передаётся из MainApp), подписывает метку состояния времени на обновления,
 * создаёт три компонента-наблюдателя (ComponentOne, ComponentTwo, ComponentThree),
 * передаёт им ссылки на соответствующие элементы из FXML, вызывает init() у ComponentTwo
 * (загрузка аудио), вешает обработчики на кнопки компонентов 2 и 3 и регистрирует
 * все три компонента как наблюдателей TimeServer.
 */
public class MainController implements Initializable {

    /** Сервер времени (создаётся в MainApp и передаётся в конструктор). */
    private final TimeServer timeServer;

    // Элементы из main-view.fxml (инжектируются FXMLLoader после load())

    /** Метка "Состояние времени: N сек (активен/неактивен)". */
    @FXML
    private Label timeStateLabel;

    /** Контейнер, внутри которого в FXML размещены три блока компонентов (1, 2, 3). */
    @FXML
    private VBox componentsContainer;

    /** Метка "Время: N сек" в компоненте 1. */
    @FXML
    private Label componentOneTimeLabel;

    /** Поле "Пауза (сек)" в компоненте 2. */
    @FXML
    private TextField delayField;

    /** MediaView для аудио в компоненте 2. */
    @FXML
    private MediaView mediaView;

    /** Метка-заглушка / ошибка загрузки MP3 в компоненте 2. */
    @FXML
    private Label placeholderLabel;

    /** Метка "Сейчас: играет" / "Сейчас: тишина" в компоненте 2. */
    @FXML
    private Label statusLabel;

    /** Кнопка "Воспроизвести" в компоненте 2. */
    @FXML
    private Button buttonPlay;

    /** Кнопка "Стоп" в компоненте 2. */
    @FXML
    private Button buttonStop;

    /** Контейнер для фигуры в компоненте 3. */
    @FXML
    private Pane shapePane;

    /** Кнопка "Вкл" в компоненте 3. */
    @FXML
    private Button buttonOn;

    /** Кнопка "Выкл" в компоненте 3. */
    @FXML
    private Button buttonOff;

    public MainController(TimeServer timeServer) {
        this.timeServer = timeServer;
    }

    /**
     * Вызывается FXMLLoader после загрузки main-view.fxml. Настраивает метку времени,
     * подписывает её на обновления TimeServer, создаёт три компонента с передачей
     * ссылок на элементы FXML, инициализирует ComponentTwo (загрузка медиа), привязывает
     * кнопки компонентов 2 и 3 к методам start/stop и onOn/onOff, регистрирует все три
     * компонента как наблюдателей TimeServer.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeStateLabel.setMaxWidth(Double.MAX_VALUE);
        timeStateLabel.setText("Состояние времени: " + timeServer.getState() + " сек (неактивен)");
        timeServer.attach(this::updateTimeLabel);

        ComponentOne componentOne = new ComponentOne(timeServer, componentOneTimeLabel);
        ComponentTwo componentTwo = new ComponentTwo(timeServer, delayField, mediaView, placeholderLabel, statusLabel);
        ComponentThree componentThree = new ComponentThree(timeServer, shapePane);

        componentTwo.init();

        buttonPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                componentTwo.start();
            }
        });
        buttonStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                componentTwo.stop();
            }
        });
        buttonOn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                componentThree.onOn();
            }
        });
        buttonOff.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                componentThree.onOff();
            }
        });

        for (IObserver observer : List.of(componentOne, componentTwo, componentThree)) {
            timeServer.attach(observer);
        }
    }

    /**
     * Обновляет текст метки времени: "Состояние времени: N сек (активен/неактивен)".
     * Вызывается из update() анонимного наблюдателя, зарегистрированного в initialize();
     * выполнение обновления UI переносится в поток JavaFX через Platform.runLater.
     */
    private void updateTimeLabel() {
        int state = timeServer.getState();
        String active = timeServer.isRunning() ? "активен" : "неактивен";
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                timeStateLabel.setText("Состояние времени: " + state + " сек (" + active + ")");
            }
        });
    }

    /** Запуск таймера TimeServer. Вызывается по кнопке "Старт" из FXML (onAction="#onStart"). */
    @FXML
    private void onStart() { timeServer.start(); }

    /** Остановка таймера TimeServer. Вызывается по кнопке "Стоп" из FXML (onAction="#onStop"). */
    @FXML
    private void onStop() { timeServer.stop(); }

    /** Сброс времени в 0 и остановка таймера. Вызывается по кнопке "Сброс" из FXML (onAction="#onReset"). */
    @FXML
    private void onReset() { timeServer.reset(); }
}
