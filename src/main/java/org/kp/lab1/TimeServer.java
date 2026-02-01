package org.kp.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Сервер времени — реализация субъекта (Subject).
 *
 * Ведёт счётчик секунд (timeState) и раз в секунду вызывает задачу таймера,
 * которая увеличивает счётчик и вызывает notifyAllObservers(). Все подписанные
 * наблюдатели получают update() и могут запросить getState() для отображения времени.
 *
 * Управление: start() — запуск отсчёта, stop() — остановка, reset() — сброс в 0.
 */
public class TimeServer implements Subject {

    /** Список наблюдателей, которым рассылается update() при каждом тике. */
    private final List<IObserver> observers = new ArrayList<>();

    /** Текущее значение времени в секундах (увеличивается каждую секунду при запущенном таймере). */
    private int timeState = 0;

    /** Таймер из java.util — по расписанию вызывает задачу раз в секунду. */
    private final Timer timer = new Timer(true);

    /** Задача, выполняемая каждую секунду: инкремент timeState и уведомление наблюдателей. */
    private TimerTask task = newTask();

    /** Флаг: идёт ли сейчас отсчёт (таймер запущен) или остановлен. */
    private boolean running = false;

    /** Задержка перед первым тиком (0 = сразу). */
    private static final int DELAY_MS = 0;
    /** Период между тиками в миллисекундах (1 секунда). */
    private static final int PERIOD_MS = 1000;

    /**
     * Создаёт новую задачу таймера: при срабатывании увеличивает timeState на 1
     * и вызывает notifyAllObservers(). Используется при инициализации и после stop().
     */
    private TimerTask newTask() {
        return new TimerTask() {
            @Override
            public void run() {
                timeState++;
                notifyAllObservers();
            }
        };
    }

    /**
     * Запустить отсчёт времени. Таймер каждую секунду вызывает задачу:
     * timeState++, затем notifyAllObservers(). Повторный вызов при уже запущенном таймере игнорируется.
     */
    public void start() {
        if (running) return;
        running = true;
        timer.schedule(task, DELAY_MS, PERIOD_MS);
    }

    /**
     * Остановить отсчёт. Отменяет задачу таймера, создаёт новую задачу для следующего start(),
     * один раз вызывает notifyAllObservers(), чтобы наблюдатели обновили вид (например, выключили музыку).
     */
    public void stop() {
        if (!running) return;
        running = false;
        task.cancel();
        task = newTask();
        notifyAllObservers();
    }

    /**
     * Сбросить время в 0. Сначала останавливает таймер (stop()), затем обнуляет timeState
     * и уведомляет наблюдателей.
     */
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

    /** Установить значение счётчика (используется при сбросе; снаружи обычно не вызывается). */
    public void setState(int time) {
        this.timeState = time;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
