import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

/**
 * GeometricObject class is abstract, so it cannot be instantiated. This class is the parent of Circle and Square classes.
 * It holds common properties like x, y, and color.
 * @author Abdulkarim Mrad - 000819421
 */
public abstract class GeometricObject {
    private double x, y;
    private Color color;

    public GeometricObject(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public abstract void draw(GraphicsContext gc);

    // Abstract method for checking if the shape contains a point (x, y)
    public abstract boolean contains(double x, double y);
}
