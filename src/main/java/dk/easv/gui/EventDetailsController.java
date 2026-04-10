package dk.easv.gui;

import dk.easv.be.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Controller for the Event Details view.
 * Responsible for displaying event information
 * and handling navigation (edit + back).
 */
public class EventDetailsController {

    // UI elements from FXML
    @FXML private Label nameLabel;
    @FXML private Label locationLabel;
    @FXML private Label timeLabel;
    @FXML private Label notesLabel;

    // Holds the selected event
    private Event event;

    /**
     * Called from EventsController when a user clicks an event.
     * This method sets the data in the UI.
     */
    public void setEvent(Event event) {
        this.event = event;

        // Display event data in labels
        nameLabel.setText(event.getName());
        locationLabel.setText("Location: " + event.getLocation());

        // Handle time safely (avoid null)
        if (event.getStartTime() != null) {
            timeLabel.setText("Time: " + event.getStartTime().toString());
        }

        // Handle notes (empty vs filled)
        if (event.getNotes() == null || event.getNotes().isBlank()) {
            notesLabel.setText("No notes");
        } else {
            notesLabel.setText(event.getNotes());
        }
    }

    /**
     * Opens the Event Creator view in EDIT mode.
     * Reuses the existing EventCreatorController.
     */
    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventsCreator.fxml")
            );

            Parent view = loader.load();

            // Get controller and pass the selected event
            EventCreatorController controller = loader.getController();
            controller.setEvent(event);

            // Replace current view inside the same content area
            StackPane contentHost = findContentHost(nameLabel);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the Events list view.
     */
    @FXML
    private void handleBack() {
        try {
            Parent view = FXMLLoader.load(
                    getClass().getResource("/dk/easv/gui/EventsView.fxml")
            );

            StackPane contentHost = findContentHost(nameLabel);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrint() {
        System.out.println("Print tickets clicked");
    }

    /**
     * Finds the main content container (StackPane)
     * so we can swap views inside the same window.
     */
    private StackPane findContentHost(Node node) {
        Node current = node;
        while (current != null) {
            if (current instanceof StackPane stackPane) {
                if ("contentHost".equals(stackPane.getId()) ||
                        stackPane.getStyleClass().contains("content-host")) {
                    return stackPane;
                }
            }
            current = current.getParent();
        }
        return null;
    }
}