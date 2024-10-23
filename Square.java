import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Square class is the subclass of GeometricObject class, this class is the model of the square
 * object, so that you will be able to draw a square by calling its draw method.
 * @author Abdulkarim Mrad - 000819421
 */
public class Square extends GeometricObject {

    private double size;

    public Square(double x, double y, Color color, double size) {
        super(x, y, color);
        this.size = size;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(getColor());
        gc.fillRect(getX(), getY(), size, size); // Draw square
    }

    // Check if the point (x, y) is inside the square
    @Override
    public boolean contains(double x, double y) {
        return x >= getX() && x <= getX() + size && y >= getY() && y <= getY() + size;
    }
}
