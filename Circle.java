import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Circle class is the subclass of GeometricObject class, this class is the model of the circle
 * object, so that you will be able to draw a circle by calling its draw method.
 * @author Abdulkarim Mrad - 000819421
 */
public class Circle extends GeometricObject {

    private double radius;

    public Circle(double x, double y, Color color, double radius) {
        super(x, y, color);
        this.radius = radius;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(getColor());
        gc.fillOval(getX(), getY(), radius, radius); // Draw circle
    }

    // Check if the point (x, y) is inside the circle
    @Override
    public boolean contains(double x, double y) {
        double centerX = getX() + radius / 2;
        double centerY = getY() + radius / 2;
        double distance = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2)); // Distance from center to point
        return distance <= radius / 2; // Inside circle if distance <= radius
    }
}
