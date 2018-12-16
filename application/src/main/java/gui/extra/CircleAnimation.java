package gui.extra;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class CircleAnimation {

    /**
     * Creates a pane with floating circles
     * (Example use: background for a start menu)
     *
     * @return constructed pane
     */
    public static Pane createCirclePane(double windowHeight, double windowWidth) {
        final int CIRCLE_COUNT = 800;
        Pane backgroundContainer = new Pane();
        backgroundContainer.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 80% , darkslategray, black);");
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            spawnBackgroundCircle(backgroundContainer, 1.0, windowHeight, windowWidth);
        }
        return backgroundContainer;
    }

    /**
     * Spawns a circle on the pane given as parameter, with different animations and colors
     * Respawn a new circle if animation has ended
     *
     * @param circleContainer pane which should contain the circles
     */
    private static void spawnBackgroundCircle(Pane circleContainer, double startOpacity, double windowHeight, double windowWidth) {
        Color[] colors = {
                new Color(0.1, 0.6, 0.5, startOpacity).saturate().brighter().brighter(),
                new Color(0.2, 0.3, 0.3, startOpacity).saturate().brighter().brighter(),
                new Color(0.4, 0.4, 0.4, startOpacity).saturate().brighter().brighter(),
                new Color(0.2, 0.4, 0.4, startOpacity).saturate().brighter().brighter(),
                new Color(0.1, 0.6, 0.3, startOpacity).saturate().brighter().brighter()
        };

        Circle circle = new Circle(0);
        circle.setManaged(true);
        circle.setFill(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
        //Take a random position within window size
        circle.setCenterX(ThreadLocalRandom.current().nextDouble(windowWidth));
        circle.setCenterY(ThreadLocalRandom.current().nextDouble(windowHeight));
        circleContainer.getChildren().add(circle);

        //Add animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(circle.radiusProperty(), 0),
                        new KeyValue(circle.centerXProperty(), circle.getCenterX()),
                        new KeyValue(circle.centerYProperty(), circle.getCenterY()),
                        new KeyValue(circle.opacityProperty(), 0)),

                new KeyFrame(Duration.seconds(5 + ThreadLocalRandom.current().nextDouble() * 5),
                        new KeyValue(circle.opacityProperty(), ThreadLocalRandom.current().nextDouble()),
                        new KeyValue(circle.radiusProperty(), ThreadLocalRandom.current().nextDouble() * 20)),

                new KeyFrame(Duration.seconds(10 + ThreadLocalRandom.current().nextDouble() * 20),
                        new KeyValue(circle.radiusProperty(), 0),
                        new KeyValue(circle.centerXProperty(), ThreadLocalRandom.current().nextDouble() * windowWidth),
                        new KeyValue(circle.centerYProperty(), ThreadLocalRandom.current().nextDouble() * windowHeight),
                        new KeyValue(circle.opacityProperty(), 0))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> {
            circleContainer.getChildren().remove(circle);
            spawnBackgroundCircle(circleContainer, 1.0, windowHeight, windowWidth);
        });
        timeline.play();
    }
}
