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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class InteractiveComponent extends Label {
    protected String componentId; // Renamed to avoid conflict with Node's getId()
    protected List<InteractiveComponent> observers = new ArrayList<>();
    protected int posX = -1, posY = -1; // Default positions

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    protected boolean isActive; // Represents the state of the component

    public InteractiveComponent(String id) {
        super(id);
        this.componentId = id;
        this.isActive = false; // Default state
        this.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-background-color: grey;");
    }

    public String getComponentId() { // Method renamed
        return componentId;
    }


    public void addObserver(InteractiveComponent observer) {
        observers.add(observer);
    }

    protected void notifyObservers(String action) {
        for (InteractiveComponent observer : observers) {
            observer.onActionReceived(action);
        }
    }

    public abstract void onActionReceived(String action);

    protected abstract void setupInteraction();

    // Toggle state between Active and Inactive
    public abstract void changeState();

    // Optionally, you might want to visually represent the state change
    protected void updateVisuals() {
        if (isActive) {
            this.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-background-color: green;");
        } else {
            this.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-background-color: grey;");
        }
    }


}

class Candle extends InteractiveComponent {
    private CandleState state = CandleState.UNLIT;

    public Candle(String id) {
        super(id);
        setupInteraction(); // Ensure this is called
    }

    @Override
    public void changeState() {
        // Toggle state between LIT and UNLIT
        if (state == CandleState.UNLIT) {
            state = CandleState.LIT;
            isActive = true; // Use isActive for visual representation
        } else {
            state = CandleState.UNLIT;
            isActive = false;
        }
        updateVisuals();
    }

    @Override
    protected void updateVisuals() {
        super.updateVisuals(); // Calls InteractiveComponent's updateVisuals
        // Additional visual update specific to Candle
        setText(state == CandleState.LIT ? "Candle: Lit" : "Candle: Unlit");
    }

    @Override
    public void onActionReceived(String action) {
        if ("LIGHT".equals(action) && state == CandleState.UNLIT) {
            state = CandleState.LIT;
            setText("Candle: Lit");
            notifyObservers("CANDLE_LIT");
        }
    }

    @Override
    protected void setupInteraction() {
        this.setOnMouseClicked(event -> changeState());
    }
}

class Box extends InteractiveComponent {
    private boolean isOpen = false;

    public Box(String id) {
        super(id);
        setupInteraction(); // Ensure this is called
    }

    @Override
    public void changeState() {
        isOpen = !isOpen; // Toggle isOpen state
        isActive = isOpen; // Assuming isActive is used for some visual representation
        updateVisuals();
    }

    @Override
    protected void updateVisuals() {
        super.updateVisuals(); // Ensure basic visual updates are applied
        setText("Box: " + (isOpen ? "Open" : "Closed"));
    }

    @Override
    public void onActionReceived(String action) {
        if ("TOGGLE".equals(action)) {
            isOpen = !isOpen;
            setText("Box: " + (isOpen ? "Open" : "Closed"));
        }
    }

    @Override
    protected void setupInteraction() {
        this.setOnMouseClicked(event -> changeState());
    }
}

class Ball extends InteractiveComponent {
    public Ball(String id) {
        super(id);
        setupInteraction(); // Ensure this is called
    }

    @Override
    public void changeState() {
        isActive = !isActive; // Simply toggle isActive for visual change
        updateVisuals();
    }

    @Override
    protected void updateVisuals() {
        super.updateVisuals(); // Calls InteractiveComponent's updateVisuals
        // You might add specific text or color changes here if needed
    }

    @Override
    public void onActionReceived(String action) {
        // Ball might not change state but could interact with others
    }

    @Override
    protected void setupInteraction() {
        this.setOnMouseClicked(event -> changeState());
    }
}

enum CandleState {
    UNLIT, LIT, BURNT_OUT
}

public class HelloApplication extends Application {
    private Map<String, InteractiveComponent> componentMap = new HashMap<>();
    private GridPane boardGrid = new GridPane(); // Define boardGrid as a class member

    public void start(Stage primaryStage) {
        VBox root = new VBox();
        Scene scene = new Scene(root, 600, 400);

        GridPane componentList = new GridPane();
        setupComponentList(componentList);

        setupBoardGrid(); // Adjusted to no longer take boardGrid as parameter

        root.getChildren().addAll(componentList, boardGrid);

        primaryStage.setTitle("Interactive Components");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setupComponentList(GridPane componentList) {
        InteractiveComponent[] components = new InteractiveComponent[]{
                new Candle("Candle1"),
                new Box("Box1"),
                new Ball("Ball1"),
        };

        for (InteractiveComponent component : components) {
            componentMap.put(component.getComponentId(), component);

            component.setOnDragDetected(event -> {
                Dragboard db = component.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(component.getComponentId()); // Correctly use the getComponentId() method
                db.setContent(content);
                event.consume();
            });
            componentList.add(component, componentMap.size() % 3, componentMap.size() / 3); // Simple grid placement
        }
    }

    private void setupBoardGrid() {
        boardGrid.getChildren().clear(); // Clear previous content if needed
        int size = 5; // Assuming a 5x5 grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                GridPane cellPane = new GridPane();
                cellPane.setMinSize(60, 60);
                cellPane.setStyle("-fx-border-color: black; -fx-alignment: center;");

                cellPane.setOnDragOver(event -> {
                    if (event.getGestureSource() != cellPane && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                cellPane.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        String componentId = db.getString();
                        InteractiveComponent component = componentMap.get(componentId);

                        // Ensure the component is not already added elsewhere
                        clearComponentPreviousPosition(component);

                        // Now, place the component in the new cell
                        if (component != null) {
                            GridPane.setConstraints(component, 0, 0); // Position in the cell grid
                            cellPane.getChildren().add(component); // Add to the cell
                            success = true;
                        }
                    }

                    event.setDropCompleted(success);
                    event.consume();
                });
                cellPane.setOnMouseClicked(event -> {
                    String text = cellPane.toString();
                    if (!text.trim().isEmpty()) {
                        InteractiveComponent component = componentMap.get(text);
                        if (component != null) {
                            component.changeState(); // Toggle or change the component's state
                            // Reflect any state change visually (optional, based on your design)
                            cellPane.setStyle(component.isActive ? "-fx-background-color: green;" : "-fx-background-color: lightgray;");
                        }
                    }
                });
                boardGrid.add(cellPane, j, i);
            }
        }

        // boardGrid.add(cell, col, row);
        // Add the cell to the grid
        // boardGrid.add(cell, j, i);
    }


    private void clearComponentPreviousPosition(InteractiveComponent component) {
        for (Node child : boardGrid.getChildren()) {
            if (child instanceof GridPane) {
                GridPane cellPane = (GridPane) child;
                if (cellPane.getChildren().contains(component)) {
                    cellPane.getChildren().remove(component);
                    break;
                }
            }
        }
    }
}