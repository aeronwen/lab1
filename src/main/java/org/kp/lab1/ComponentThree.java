package org.kp.lab1;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Компонент 3 — третий наблюдатель (IObserver).
 *
 * Отображает фигуру в зависимости от (state % 10): 0 — пусто, 1 — точка (маленький круг),
 * 2 — горизонтальная линия, 3 и больше — правильный многоугольник с соответствующим числом углов.
 * Кнопки "Вкл" и "Выкл": onOn() — подписка и включение отображения, onOff() — отписка.
 *
 * Разметка (Pane для фигуры и кнопки) в main-view.fxml; ссылка на Pane передаётся из MainController.
 */
public class ComponentThree implements IObserver {

    /** Координаты центра фигуры и радиус описанной окружности для многоугольника/линии. */
    private static final double CENTER_X = 40;
    private static final double CENTER_Y = 40;
    private static final double RADIUS = 35;

    /** Цвет фигуры (точка, линия, многоугольник). */
    private static final Color SHAPE_COLOR = Color.BLACK;

    /** Субъект времени — getState() даёт текущую секунду, state % 10 определяет тип фигуры. */
    private final Subject subject;

    /** Контейнер, в котором рисуется фигура (очищается и заполняется заново при каждом update()). */
    private final Pane shapePane;

    /** Включено ли отображение фигуры (false после "Выкл", снова true после "Вкл"). */
    private boolean enabled = true;

    /** Подписан ли компонент на Subject (false после "Выкл", true после "Вкл"). */
    private boolean attached = true;

    /**
     * @param subject субъект времени (TimeServer)
     * @param shapePane контейнер из main-view.fxml (fx:id="shapePane")
     */
    public ComponentThree(Subject subject, Pane shapePane) {
        this.subject = subject;
        this.shapePane = shapePane;
    }

    /**
     * Включить отображение и при необходимости подписаться на Subject. Вызывается по кнопке "Вкл".
     */
    public void onOn() {
        if (!attached) {
            subject.attach(this);
            attached = true;
        }
        enabled = true;
        update();
    }

    /**
     * Отписаться от Subject (фигура перестаёт обновляться). Вызывается по кнопке "Выкл".
     */
    public void onOff() {
        subject.detach(this);
        attached = false;
    }

    /**
     * Создаёт фигуру для значения n = state % 10: 0 — null (ничего), 1 — точка, 2 — линия, 3+ — многоугольник с n углами.
     */
    private Shape shapeFor(int n) {
        if (n == 0) return null;
        if (n == 1) {
            Circle point = new Circle(CENTER_X, CENTER_Y, 3);
            point.setFill(SHAPE_COLOR);
            return point;
        }
        if (n == 2) {
            Line line = new Line(CENTER_X - RADIUS, CENTER_Y, CENTER_X + RADIUS, CENTER_Y);
            line.setStroke(SHAPE_COLOR);
            line.setStrokeWidth(2);
            return line;
        }
        List<Double> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
            points.add(CENTER_X + RADIUS * Math.cos(angle));
            points.add(CENTER_Y + RADIUS * Math.sin(angle));
        }
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(points);
        polygon.setFill(SHAPE_COLOR);
        return polygon;
    }

    /**
     * Вызывается Subject каждую секунду, пока компонент подписан. По getState() % 10
     * определяется тип фигуры; shapePane очищается и в него добавляется новая фигура.
     */
    @Override
    public void update() {
        if (!attached) return;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!enabled) return;
                int n = subject.getState() % 10;
                shapePane.getChildren().clear();
                Shape shape = ComponentThree.this.shapeFor(n);
                if (shape != null) shapePane.getChildren().add(shape);
            }
        });
    }
}
