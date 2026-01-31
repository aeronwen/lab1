package org.kp.lab1;

/**
 * Субъект (Subject): хранит наблюдателей, при изменении состояния вызывает у них update().
 */
public interface Subject {

    void notifyAllObservers();
    void attach(IObserver observer);
    void detach(IObserver observer);
    int getState();
    /** Запущен ли таймер (кнопки компонентов активны только при running). */
    boolean isRunning();
}
