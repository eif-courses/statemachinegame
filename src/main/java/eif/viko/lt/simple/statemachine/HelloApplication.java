package eif.viko.lt.simple.statemachine;

import javafx.application.Application;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        Scene scene = new Scene(root, 600, 400);

        // Component List
        GridPane componentList = new GridPane();
        setupComponentList(componentList);

        // Board Grid
        GridPane boardGrid = new GridPane();
        setupBoardGrid(boardGrid);

        root.getChildren().addAll(componentList, boardGrid);

        primaryStage.setTitle("Drag and Drop Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setupComponentList(GridPane componentList) {
        int numberOfComponents = 5; // Example number of components
        for (int i = 0; i < numberOfComponents; i++) {
            Label label = new Label("Component " + (i + 1));
            label.setOnDragDetected(event -> {
                Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(label.getText());
                db.setContent(content);
                event.consume();
            });
            componentList.add(label, i % 3, i / 3); // Organizing components in a grid
        }
    }
    private Label createDraggableLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-padding: 5px;");

        label.setOnDragDetected(event -> {
            Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(label.getText());
            db.setContent(content);
            event.consume();
        });

        label.setOnDragDone(event -> {
            // If the drag was successful, clear the cell's text if it's from the grid
            if (event.getTransferMode() == TransferMode.MOVE) {
                label.setText("      ");
            }
            event.consume();
        });

        return label;
    }
    private void setupBoardGrid(GridPane boardGrid) {
        int size = 3; // Grid size
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Label cell = createDraggableLabel("      "); // Use the same method for consistency

                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        if (cell.getText().trim().isEmpty()) { // Only allow drop if the cell is empty
                            cell.setText(db.getString());
                            success = true;
                        }
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                boardGrid.add(cell, i, j);
            }
        }
    }
    private boolean isComponentAlreadyPlaced(GridPane boardGrid, String component) {
        for (Node child : boardGrid.getChildren()) {
            if (child instanceof Label) {
                Label cell = (Label) child;
                if (cell.getText().equals(component)) {
                    return true; // The component is already placed somewhere in the grid
                }
            }
        }
        return false; // The component has not been placed in the grid
    }
}