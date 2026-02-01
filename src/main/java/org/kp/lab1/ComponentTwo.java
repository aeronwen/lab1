package org.kp.lab1;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Компонент 2 — второй наблюдатель (IObserver).
 *
 * Реализует цикл: сначала пауза M секунд (значение из поля "Пауза (сек)"), затем
 * воспроизведение музыки 10 секунд, затем снова пауза
 *
 * Разметка в main-view.fxml; ссылки на элементы
 * передаются из MainController. После создания нужно вызвать init() для загрузки медиа.
 */
public class ComponentTwo implements IObserver {

    /** Длительность воспроизведения музыки в секундах (фиксированная). */
    private static final int PLAY_DURATION_SECONDS = 10;

    /** Субъект (Subject) — по нему вычисляется фаза цикла (пауза / воспроизведение) через getState() и isRunning(). */
    private final Subject subject;

    /** Поле ввода "Пауза (сек)" — число M секунд паузы между воспроизведениями. */
    private final TextField delayField;

    /** Метка статуса "Сейчас: играет" / "Сейчас: тишина". */
    private final Label statusLabel;

    /** Плеер для воспроизведения аудио; создаётся в tryLoadMedia(). */
    private MediaPlayer mediaPlayer;

    /** Подписан ли компонент на Subject */
    private boolean attached = true;

    /**
     * @param subject субъект времени (TimeServer)
     * @param delayField поле ввода паузы (сек)
     * @param statusLabel метка "Сейчас: играет" / "Сейчас: тишина"
     */
    public ComponentTwo(Subject subject, TextField delayField, Label statusLabel) {
        this.subject = subject;
        this.delayField = delayField;
        this.statusLabel = statusLabel;
    }

    /**
     * Вызвать после создания. Загружает аудио из /audio/sample.mp3 и создаёт MediaPlayer.
     */
    public void init() {
        loadMedia();
    }

    /**
     * Запуск воспроизведения. Если компонент отписан (attached == false), снова подписывается
     * на subject. Если таймер запущен и медиа загружено — принудительно запускает плеер
     * и ставит статус "Сейчас: играет". Вызывается по кнопке "Воспроизвести" из MainController.
     */
    public void start() {
        if (!attached) {
            subject.attach(this);
            attached = true;
        }
        if (mediaPlayer != null && subject.isRunning()) {
            mediaPlayer.stop();
            mediaPlayer.play();
            statusLabel.setText("Сейчас: играет");
        }
    }

    /**
     * Остановка воспроизведения и отписка от Subject. Плеер останавливается,
     * статус — "Сейчас: тишина". После этого update() больше не вызывается, пока
     * пользователь снова не нажмёт "Воспроизвести" (start()). Вызывается по кнопке "Стоп".
     */
    public void stop() {
        subject.detach(this);
        attached = false;
        if (mediaPlayer != null) mediaPlayer.stop();
        statusLabel.setText("Сейчас: тишина");
    }

    /** Загружает /audio/sample.mp3 с classpath и создаёт MediaPlayer. Цикл — чтобы короткий трек повторялся в 10-сек окне. */
    private void loadMedia() {
        java.net.URL resource = getClass().getResource("/audio/sample.mp3");
        if (resource != null) {
            mediaPlayer = new MediaPlayer(new Media(resource.toExternalForm()));
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
    }

    /**
     * Вызывается Subject каждую секунду. Решает: сейчас пауза или окно воспроизведения,
     * и обновляет плеер и метку статуса. Выполняется в потоке JavaFX (Platform.runLater).
     */
    @Override
    public void update() {
        if (!attached) return;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Таймер остановлен — выключить музыку и выйти
                if (!subject.isRunning()) {
                    setSilence();
                    return;
                }
                // Фаза в цикле: 0..delaySeconds-1 — пауза, delaySeconds..cycleLength-1 — воспроизведение
                int delaySeconds = getDelaySeconds();
                int cycleLength = Math.max(1, delaySeconds + PLAY_DURATION_SECONDS);
                int phase = subject.getState() % cycleLength;
                boolean inPlayWindow = phase >= delaySeconds;

                if (mediaPlayer == null) return;
                if (inPlayWindow) {
                    mediaPlayer.play();
                    statusLabel.setText("Сейчас: играет");
                } else {
                    setSilence();
                }
            }
        });
    }

    private void setSilence() {
        if (mediaPlayer != null) mediaPlayer.stop();
        statusLabel.setText("Сейчас: тишина");
    }

    private int getDelaySeconds() {
        try {
            int value = Integer.parseInt(delayField.getText().trim());
            return value >= 0 ? value : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }
}
