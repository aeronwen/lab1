package org.kp.lab1;

/**
 * Наблюдатель (Observer): реагирует на изменение субъекта.
 * Пассивная модель — в update() сам запрашивает subject.getState().
 */
public interface IObserver {

    /** Вызывается субъектом при изменении состояния. */
    void update();
}
