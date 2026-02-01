package org.kp.lab1;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

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

    /** Окно для отображения видео/аудио (здесь только аудио). */
    private final MediaView mediaView;

    /** Метка-заглушка Добавьте audio/sample.mp3 */
    private final Label placeholderLabel;

    /** Метка статуса: */
    private final Label statusLabel;

    /** Плеер для воспроизведения аудио; создаётся в tryLoadMedia(). */
    private MediaPlayer mediaPlayer;

    /** Подписан ли компонент на Subject */
    private boolean attached = true;

    /**
     * @param subject субъект времени (TimeServer)
     * @param delayField поле ввода паузы (сек)
     * @param mediaView MediaView для плеера
     * @param placeholderLabel метка-заглушка
     * @param statusLabel метка "Сейчас: играет" / "Сейчас: тишина"
     */
    public ComponentTwo(Subject subject, TextField delayField, MediaView mediaView,
                        Label placeholderLabel, Label statusLabel) {
        this.subject = subject;
        this.delayField = delayField;
        this.mediaView = mediaView;
        this.placeholderLabel = placeholderLabel;
        this.statusLabel = statusLabel;
    }

    /**
     * Вызвать после создания компонента. Загружает аудио из /audio/sample.mp3,
     * создаёт MediaPlayer, подвязывает к mediaView и скрывает placeholder при успехе.
     */
    public void init() {
        tryLoadMedia();
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
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
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

    /**
     * Пытается загрузить /audio/sample.mp3 с classpath. При успехе создаёт Media и MediaPlayer,
     * подвязывает к mediaView и скрывает placeholderLabel. При ошибке показывает "MP3 не найден".
     */
    private void tryLoadMedia() {
        try {
            java.net.URL resource = getClass().getResource("/audio/sample.mp3");
            if (resource != null) {
                mediaPlayer = new MediaPlayer(new Media(resource.toExternalForm()));
                mediaView.setMediaPlayer(mediaPlayer);
                placeholderLabel.setVisible(false);
            }
        } catch (Exception e) {
            placeholderLabel.setText("MP3 не найден");
        }
    }

    /**
     * Вызывается Subject каждую секунду, пока компонент подписан (attached == true).
     * По текущему времени (getState()) и длине цикла (пауза + 10 сек) вычисляется фаза:
     * если фаза в интервале "воспроизведение" — включается плеер, иначе — тишина.
     * Обновление UI выполняется в потоке JavaFX (Platform.runLater).
     */
    @Override
    public void update() {
        if (!attached) return;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // При остановленном таймере — выключить музыку и выйти
                if (!subject.isRunning() && mediaPlayer != null) {
                    mediaPlayer.stop();
                    statusLabel.setText("Сейчас: тишина");
                    return;
                }
                // Читаем паузу из поля (по умолчанию 10 при нечисле или отрицательном)
                int delaySeconds = 10;
                try {
                    int value = Integer.parseInt(delayField.getText().trim());
                    if (value >= 0) delaySeconds = value;
                } catch (NumberFormatException ignored) {
                }
                int cycleLength = Math.max(1, delaySeconds + PLAY_DURATION_SECONDS);
                int phase = subject.getState() % cycleLength;
                boolean inPlayWindow = phase >= delaySeconds;

                if (mediaPlayer == null) return;
                if (inPlayWindow) {
                    if (phase == delaySeconds) {
                        mediaPlayer.stop();
                        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    }
                    mediaPlayer.play();
                    statusLabel.setText("Сейчас: играет");
                } else {
                    mediaPlayer.stop();
                    statusLabel.setText("Сейчас: тишина");
                }
            }
        });
    }
}
