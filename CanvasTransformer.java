import java.util.*;

public final class CanvasTransformer {

    public static Canvas<Double> shift(Canvas<Double> canvas, Double deltaX, Double deltaY) {
        return Objects.requireNonNull(canvas).transform(
                xCoordinate -> xCoordinate + Objects.requireNonNull(deltaX),
                yCoordinate -> yCoordinate + Objects.requireNonNull(deltaY));
    }

    public static Canvas<Double> rotate(Canvas<Double> canvas, Double theta) {
        return Objects.requireNonNull(canvas).transform(
                (x, y) -> x*Math.cos(theta) - y*Math.sin(theta),
                (x, y) -> x*Math.sin(theta) + y*Math.cos(theta));
    }

    public static Canvas<Double> magnify(Canvas<Double> canvas, Double m) {
        return Objects.requireNonNull(canvas).transform(
                xCoordinate -> xCoordinate * Objects.requireNonNull(m),
                yCoordinate -> yCoordinate * Objects.requireNonNull(m));
    }

}
