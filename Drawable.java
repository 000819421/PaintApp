import javafx.scene.canvas.GraphicsContext;
/**
 * Drawable interface is the parent of GeometricObject class has a draw method
 * @author Abdulkarim Mrad - 000819421
 */
public interface Drawable {
    /**
     * draw method to draw circle and square
     * @param gc
     */
    public void draw(GraphicsContext gc);
}
