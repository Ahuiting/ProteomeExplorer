package ProteomeExplorer.moleculeShow;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * the rotate function
 */

public class MouseInteraction {
    private static double oldX;
    private static double oldY;
    private static RotateTransition rotateTransition;

    public static void installRotate(Stage stage, Pane pane, Property<Transform> figureTransformProperty, Group molecule) {

        pane.setOnMousePressed(e -> {
            oldX = e.getSceneX();
            oldY = e.getSceneY();
            e.consume();
        });

        rotateTransition = new RotateTransition();
        pane.setOnMouseDragged(e -> {
            pane.setCursor(Cursor.CLOSED_HAND);
            var delta = new Point2D(e.getSceneX() - oldX, e.getSceneY() - oldY);
            var dragOrthogonalAxis = new Point3D(delta.getY(), -delta.getX(), 0);
            var rotate = new Rotate(0.25 * delta.magnitude(), dragOrthogonalAxis);
            figureTransformProperty.setValue(
                    rotate.createConcatenation(figureTransformProperty.getValue()));
            oldX = e.getSceneX();
            oldY = e.getSceneY();
            stage.getScene().setOnKeyPressed(keyEvent -> {
                if (keyEvent.isShiftDown()) {
                    rotateTransition.setNode(molecule);
                    rotateTransition.setDuration(Duration.seconds(10));
                    rotateTransition.setAxis(dragOrthogonalAxis);
                    rotateTransition.setToAngle(360);
                    rotateTransition.setInterpolator(Interpolator.LINEAR);
                    rotateTransition.setCycleCount(Timeline.INDEFINITE);
                    rotateTransition.play();
                }

            });
            e.consume();
        });

        pane.setOnMouseReleased(e -> {
            stage.getScene().setOnKeyPressed(keyEvent -> {
                if (keyEvent.isShiftDown()) {
                    rotateTransition.stop();
                    var backToStart = new RotateTransition();
                    backToStart.setToAngle(0);
                    backToStart.setNode(molecule);
                    backToStart.setDuration(Duration.seconds(1));
                    backToStart.setCycleCount(1);
                    backToStart.play();
                }
            });
            pane.setCursor(Cursor.DEFAULT);

        });


    }
}


