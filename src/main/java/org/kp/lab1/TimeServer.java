package org.kp.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Сервер времени — реализация Subject. Счётчик секунд, раз в секунду уведомляет наблюдателей.
 */
public class TimeServer implements Subject {

    private final List<IObserver> observers = new ArrayList<>();
    private int timeState = 0;
    private final Timer timer = new Timer(true);
    private TimerTask task = newTask();
    private boolean running = false;

    private static final int DELAY_MS = 0;
    private static final int PERIOD_MS = 1000;

    private TimerTask newTask() {
        return new TimerTask() {
            @Override
            public void run() {
                timeState++;
                notifyAllObservers();
            }
        };
    }

    public void start() {
        if (running) return;
        running = true;
        timer.schedule(task, DELAY_MS, PERIOD_MS);
    }

    /** Остановить отсчёт и один раз уведомить наблюдателей. */
    public void stop() {
        if (!running) return;
        running = false;
        task.cancel();
        task = newTask();
        notifyAllObservers();
    }

    public void reset() {
        stop();
        timeState = 0;
        notifyAllObservers();
    }

    @Override
    public void notifyAllObservers() {
        observers.forEach(IObserver::update);
    }

    @Override
    public void attach(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getState() {
        return timeState;
    }

    public void setState(int time) {
        this.timeState = time;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
