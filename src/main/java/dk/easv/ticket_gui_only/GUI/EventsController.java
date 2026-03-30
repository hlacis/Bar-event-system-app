package dk.easv.ticket_gui_only.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class EventsController {

    @FXML
    private void generateEvent() {

        EventCardData newEvent = new EventCardData(
                "Generated Event " + (mockEvents.size() + 1),
                "2026-12-01 at 12:00 • Generated Location",
                "Coordinator: Auto Generated"
        );

        mockEvents.add(newEvent);
        eventsList.getChildren().add(createEventCard(newEvent));
    }

    @FXML
    private VBox eventsList;

    // Mock data (GUI-only prototype)
    private final java.util.List<EventCardData> mockEvents = new java.util.ArrayList<>(List.of(
            new EventCardData("Tech Conference 2026",
                    "2026-03-15 at 09:00 AM • Copenhagen Convention Center",
                    "Coordinator: Sarah Johnson"),
            new EventCardData("Music Festival",
                    "2026-06-20 at 06:00 PM • Aarhus Outdoor Arena",
                    "Coordinator: Emma Nielsen"),
            new EventCardData("Business Networking Event",
                    "2026-04-10 at 05:00 PM • Odense Business Park",
                    "Coordinator: Sarah Johnson")
    ) );

    @FXML
    public void initialize() {
        // Dynamically generate GUI components (requirement!)
        eventsList.getChildren().clear();

        for (EventCardData e : mockEvents) {
            eventsList.getChildren().add(createEventCard(e));
        }
    }

    private HBox createEventCard(EventCardData data) {
        // Left text column
        Label title = new Label(data.title());
        title.getStyleClass().add("card-title");

        Label details = new Label(data.details());
        details.getStyleClass().add("card-subtext");

        Label coordinator = new Label(data.coordinator());
        coordinator.getStyleClass().add("muted");

        VBox left = new VBox(6, title, details, coordinator);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Buttons
        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.getStyleClass().add("secondary-button");
        viewDetailsBtn.setOnAction(evt -> showInfo("View Details", data.title()));

        Button assignBtn = new Button("Assign Coordinator");
        assignBtn.getStyleClass().add("secondary-button");
        assignBtn.setOnAction(evt -> showInfo("Assign Coordinator", data.title()));

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().addAll("icon-button", "danger-button");
        deleteBtn.setOnAction(this::onDeleteClicked);

        // Card container
        HBox card = new HBox(12, left, spacer, viewDetailsBtn, assignBtn, deleteBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");

        return card;
    }

    @FXML
    private void onDeleteClicked(ActionEvent event) {
        Node source = (Node) event.getSource();

        // Find the card HBox
        Node current = source;
        while (current != null && !(current instanceof HBox)) {
            current = current.getParent();
        }

        if (current instanceof HBox card && eventsList != null) {
            eventsList.getChildren().remove(card); // GUI-only delete
        }
    }

    private void showInfo(String action, String eventTitle) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(action);
        alert.setHeaderText(null);
        alert.setContentText(action + " clicked for: " + eventTitle);
        alert.showAndWait();
    }

    // Simple record for mock data (no DAL/BLL)
    private record EventCardData(String title, String details, String coordinator) {}
}
