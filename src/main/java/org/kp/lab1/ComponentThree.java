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
 * Компонент 3 — наблюдатель. Форма по state % 10: 0 — пусто, 1 — точка, 2 — линия, 3+ — многоугольник. Разметка в main-view.fxml.
 */
public class ComponentThree implements IObserver {

    private static final double CENTER_X = 40;
    private static final double CENTER_Y = 40;
    private static final double RADIUS = 35;
    private static final Color SHAPE_COLOR = Color.BLACK;

    private final Subject subject;
    private final Pane shapePane;
    private boolean enabled = true;
    private boolean attached = true;

    public ComponentThree(Subject subject, Pane shapePane) {
        this.subject = subject;
        this.shapePane = shapePane;
    }

    public void onOn() {
        if (!attached) {
            subject.attach(this);
            attached = true;
        }
        enabled = true;
        update();
    }

    public void onOff() {
        subject.detach(this);
        attached = false;
    }

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

    @Override
    public void update() {
        if (!attached) return;
        Platform.runLater(() -> {
            if (!enabled) return;
            int n = subject.getState() % 10;
            shapePane.getChildren().clear();
            Shape shape = shapeFor(n);
            if (shape != null) shapePane.getChildren().add(shape);
        });
    }
}
