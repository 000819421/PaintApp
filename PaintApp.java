import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.InputMismatchException;

/**
 * This program is about creating a drawing app, where users can draw circles or squares.
 * Users can choose color, size, and location. The app supports drawing via mouse clicks or manually entered locations.
 * It also includes an eraser tool to remove shapes by clicking or dragging over them and an option to clear the entire canvas.
 * @author Abdulkarim Mrad - 000819421
 */
public class PaintApp extends Application {

    private boolean isCircle = true; // true for circle, false for square
    private boolean isEraserMode = false; // true for eraser mode
    private ArrayList<GeometricObject> objectLists = new ArrayList<>(); // List to store shapes
    private int length = 0; // Tracks drawn shapes
    private Label exceptionLabel = new Label("No Errors!"); // Displays error messages
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField col1, col2, col3, size, locationX, locationY;
    private CheckBox manualLocationCheckBox; // Checkbox to toggle manual input for location
    private Button eraserBtn, clearBtn; // Button to toggle eraser mode and clear canvas

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing App");

        // Create Canvas
        canvas = new Canvas(800, 530);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 800, 530); // White background

        // Input fields and buttons
        col1 = new TextField();
        col2 = new TextField();
        col3 = new TextField();
        size = new TextField();
        locationX = new TextField();
        locationY = new TextField();
        manualLocationCheckBox = new CheckBox("Enter location manually");
        eraserBtn = new Button("Eraser Mode: OFF");
        clearBtn = new Button("Clear Canvas");

        Button drawBtn = new Button("Draw");
        Button undoBtn = new Button("Undo");
        Button squareBtn = new Button("Square");
        Button circleBtn = new Button("Circle");

        // Event for drawing shapes via the Draw button
        drawBtn.setOnAction(event -> drawShapeFromFields());

        // Event for Undo
        undoBtn.setOnAction(this::Undo);

        // Set square or circle mode
        squareBtn.setOnAction(event -> isCircle = false);
        circleBtn.setOnAction(event -> isCircle = true);

        // Toggle eraser mode
        eraserBtn.setOnAction(event -> toggleEraserMode());

        // Clear canvas event
        clearBtn.setOnAction(event -> clearCanvas());

        // Layout
        GridPane controlPanel = new GridPane();
        controlPanel.setHgap(10);
        controlPanel.setVgap(10);

        controlPanel.add(new Label("Color R:"), 0, 0);
        controlPanel.add(col1, 1, 0);
        controlPanel.add(new Label("Color G:"), 0, 1);
        controlPanel.add(col2, 1, 1);
        controlPanel.add(new Label("Color B:"), 0, 2);
        controlPanel.add(col3, 1, 2);
        controlPanel.add(new Label("Size:"), 0, 3);
        controlPanel.add(size, 1, 3);

        // Add manual location fields and checkbox
        controlPanel.add(manualLocationCheckBox, 0, 4, 2, 1);
        controlPanel.add(new Label("X Position:"), 0, 5);
        controlPanel.add(locationX, 1, 5);
        controlPanel.add(new Label("Y Position:"), 0, 6);
        controlPanel.add(locationY, 1, 6);

        controlPanel.add(drawBtn, 0, 7);
        controlPanel.add(undoBtn, 1, 7);
        controlPanel.add(squareBtn, 0, 8);
        controlPanel.add(circleBtn, 1, 8);
        controlPanel.add(eraserBtn, 0, 9, 2, 1);
        controlPanel.add(clearBtn, 0, 10, 2, 1); // Add clear button
        controlPanel.add(exceptionLabel, 0, 11, 2, 1);

        // Main layout
        BorderPane layout = new BorderPane();
        layout.setCenter(canvas);
        layout.setRight(controlPanel);

        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

        // Add mouse events for click and drag for drawing or erasing
        canvas.setOnMousePressed(e -> {
            if (isEraserMode) {
                eraseShape(e.getX(), e.getY());
            } else if (!manualLocationCheckBox.isSelected()) {
                startDrawing(e.getX(), e.getY()); // Use mouse for location if manual location is not selected
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (isEraserMode) {
                eraseShape(e.getX(), e.getY()); // Erase shapes during drag
            } else if (!manualLocationCheckBox.isSelected()) {
                continueDrawing(e.getX(), e.getY()); // Use mouse for dragging if manual location is not selected
            }
        });
    }

    // Toggle between eraser mode and drawing mode
    private void toggleEraserMode() {
        isEraserMode = !isEraserMode;
        if (isEraserMode) {
            eraserBtn.setText("Eraser Mode: ON");
        } else {
            eraserBtn.setText("Eraser Mode: OFF");
        }
    }

    // Erase a shape at the mouse location
    private void eraseShape(double mouseX, double mouseY) {
        try {
            for (int i = objectLists.size() - 1; i >= 0; i--) { // Iterate in reverse to remove last-drawn shapes first
                GeometricObject shape = objectLists.get(i);
                if (shape.contains(mouseX, mouseY)) { // Check if the shape contains the mouse position
                    objectLists.remove(i); // Remove the shape
                    gc.setFill(Color.WHITE); // Clear canvas
                    gc.fillRect(0, 0, 800, 530); // Redraw background
                    redrawShapes(); // Redraw remaining shapes
                    break; // Only erase one shape per click/drag
                }
            }
        } catch (Exception e) {
            exceptionLabel.setTextFill(Color.RED);
            exceptionLabel.setText("Error during erasing: " + e.getMessage());
        }
    }

    // Redraw all shapes after an erasure or undo
    private void redrawShapes() {
        for (GeometricObject g : objectLists) {
            g.draw(gc);
        }
    }

    // Clear the entire canvas
    private void clearCanvas() {
        objectLists.clear(); // Remove all shapes from the list
        gc.setFill(Color.WHITE); // Clear canvas
        gc.fillRect(0, 0, 800, 530); // Reset to a blank white canvas
        exceptionLabel.setText("Canvas cleared!"); // Feedback
        exceptionLabel.setTextFill(Color.GREEN); // Success message
    }

    // Draw a shape based on user input from text fields
    private void drawShapeFromFields() {
        try {
            // Parse the color values
            int color1 = Integer.parseInt(col1.getText());
            int color2 = Integer.parseInt(col2.getText());
            int color3 = Integer.parseInt(col3.getText());

            // Parse size
            double si = Double.parseDouble(size.getText());

            // Validate inputs
            if (color1 < 0 || color1 > 255 || color2 < 0 || color2 > 255 || color3 < 0 || color3 > 255) {
                throw new IllegalArgumentException("Color values must be between 0 and 255.");
            }
            if (si < 0) {
                throw new InputMismatchException("Size must be greater than zero.");
            }

            // If manual location is selected, use entered location
            double lX = 400, lY = 265; // Default to center
            if (manualLocationCheckBox.isSelected()) {
                lX = Double.parseDouble(locationX.getText());
                lY = Double.parseDouble(locationY.getText());
                if (lX < 0 || lY < 0 || lX > 800 || lY > 530) {
                    throw new InputMismatchException("Invalid location.");
                }
            }

            // Success feedback
            exceptionLabel.setTextFill(Color.GREEN);
            exceptionLabel.setText("No Errors!");

            // Draw shapes
            if (isCircle) {
                Circle c = new Circle(lX - si / 2, lY - si / 2, Color.rgb(color1, color2, color3), si);
                objectLists.add(c);
                c.draw(gc);
            } else {
                Square s = new Square(lX - si / 2, lY - si / 2, Color.rgb(color1, color2, color3), si);
                objectLists.add(s);
                s.draw(gc);
            }
            length++;
        } catch (IllegalArgumentException e) {
            exceptionLabel.setTextFill(Color.RED);
            exceptionLabel.setText("Error: " + e.getMessage());
        } catch (Exception e) {
            exceptionLabel.setTextFill(Color.RED);
            exceptionLabel.setText("Invalid input: " + e.getMessage());
        }
    }

    // Start drawing a shape at the mouse press location
    private void startDrawing(double mouseX, double mouseY) {
        drawShapeAtMouse(mouseX, mouseY);
    }

    // Continue drawing shapes while dragging the mouse
    private void continueDrawing(double mouseX, double mouseY) {
        drawShapeAtMouse(mouseX, mouseY);
    }

    // Draw shapes at the location where the user clicks or drags the mouse
    private void drawShapeAtMouse(double mouseX, double mouseY) {
        try {
            // Parse the color values
            int color1 = Integer.parseInt(col1.getText());
            int color2 = Integer.parseInt(col2.getText());
            int color3 = Integer.parseInt(col3.getText());

            // Parse size
            double si = Double.parseDouble(size.getText());

            // Validate inputs
            if (color1 < 0 || color1 > 255 || color2 < 0 || color2 > 255 || color3 < 0 || color3 > 255) {
                throw new IllegalArgumentException("Color values must be between 0 and 255.");
            }
            if (si < 0) {
                throw new InputMismatchException("Size must be greater than zero.");
            }

            // Success feedback
            exceptionLabel.setTextFill(Color.GREEN);
            exceptionLabel.setText("No Errors!");

            // Draw shapes at the mouse click/drag position
            if (isCircle) {
                Circle c = new Circle(mouseX - si / 2, mouseY - si / 2, Color.rgb(color1, color2, color3), si);
                objectLists.add(c);
                c.draw(gc);
            } else {
                Square s = new Square(mouseX - si / 2, mouseY - si / 2, Color.rgb(color1, color2, color3), si);
                objectLists.add(s);
                s.draw(gc);
            }
            length++;
        } catch (IllegalArgumentException e) {
            exceptionLabel.setTextFill(Color.RED);
            exceptionLabel.setText("Error: " + e.getMessage());
        } catch (Exception e) {
            exceptionLabel.setTextFill(Color.RED);
            exceptionLabel.setText("Invalid input: " + e.getMessage());
        }
    }

    // Undo the last shape drawn
    private void Undo(ActionEvent event) {
        try {
            if (length > 0) {
                objectLists.remove(length - 1); // Remove the last shape
                length--;
                gc.setFill(Color.WHITE); // Clear canvas
                gc.fillRect(0, 0, 800, 530); // Redraw background

                // Redraw all remaining shapes
                redrawShapes();
            } else {
                throw new IllegalStateException("Canvas is already cleared!");
            }
        } catch (Exception e) {
            exceptionLabel.setTextFill(Color.RED);
            exceptionLabel.setText("Error: " + e.getMessage());
        }
    }
}
