package org.kp.lab1;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Компонент 2 — наблюдатель. Пауза M сек, затем музыка 10 сек. Разметка в main-view.fxml.
 * start() — запуск воспроизведения, stop() — остановка и отписка от TimeServer.
 */
public class ComponentTwo implements IObserver {

    private static final int PLAY_DURATION_SECONDS = 10;

    private final TimeServer timeServer;
    private final TextField delayField;
    private final MediaView mediaView;
    private final Label placeholderLabel;
    private final Label statusLabel;
    private MediaPlayer mediaPlayer;
    private boolean attached = true;

    public ComponentTwo(TimeServer timeServer, TextField delayField, MediaView mediaView,
                        Label placeholderLabel, Label statusLabel) {
        this.timeServer = timeServer;
        this.delayField = delayField;
        this.mediaView = mediaView;
        this.placeholderLabel = placeholderLabel;
        this.statusLabel = statusLabel;
    }

    /** Вызвать после создания (загрузка медиа). */
    public void init() {
        tryLoadMedia();
    }

    public void start() {
        if (!attached) {
            timeServer.attach(this);
            attached = true;
        }
        if (mediaPlayer != null && timeServer.isRunning()) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.stop();
            mediaPlayer.play();
            statusLabel.setText("Сейчас: играет");
        }
    }

    public void stop() {
        timeServer.detach(this);
        attached = false;
        if (mediaPlayer != null) mediaPlayer.stop();
        statusLabel.setText("Сейчас: тишина");
    }

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

    @Override
    public void update() {
        if (!attached) return;
        Platform.runLater(() -> {
            if (!timeServer.isRunning() && mediaPlayer != null) {
                mediaPlayer.stop();
                statusLabel.setText("Сейчас: тишина");
                return;
            }
            int delaySeconds = 10;
            try {
                int value = Integer.parseInt(delayField.getText().trim());
                if (value >= 0) delaySeconds = value;
            } catch (NumberFormatException ignored) { }
            int cycleLength = Math.max(1, delaySeconds + PLAY_DURATION_SECONDS);
            int phase = timeServer.getState() % cycleLength;
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
        });
    }
}
