package dk.easv.gui;

import dk.easv.be.Event;
import dk.easv.bll.EventManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventsController {

    @FXML
    private VBox eventsList;

    private final EventManager eventManager = new EventManager();

    @FXML
    public void initialize() {
        loadEventsFromDatabaseAsync();
    }

    @FXML
    private void generateEvent() {
        try {
            Parent creatorView = FXMLLoader.load(
                    getClass().getResource("/dk/easv/gui/EventsCreator.fxml")
            );

            StackPane contentHost = findContentHost(eventsList);
            if (contentHost != null) {
                contentHost.getChildren().setAll(creatorView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open Events Creator view.");
        }
    }

    // 🔥 NEW METHOD (EDIT)
    private void openEditView(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventsCreator.fxml")
            );

            Parent creatorView = loader.load();

            // Get controller of creator view
            EventCreatorController controller = loader.getController();

            // Pass selected event to edit
            controller.setEvent(event);

            StackPane contentHost = findContentHost(eventsList);
            if (contentHost != null) {
                contentHost.getChildren().setAll(creatorView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open edit view.");
        }
    }

    private void loadEventsFromDatabaseAsync() {
        eventsList.getChildren().clear();

        Label loadingLabel = new Label("Loading events...");
        loadingLabel.getStyleClass().add("muted");
        eventsList.getChildren().add(loadingLabel);

        Task<List<Event>> loadTask = new Task<>() {
            @Override
            protected List<Event> call() throws Exception {
                return eventManager.getAllEvents();
            }
        };

        loadTask.setOnSucceeded(workerStateEvent -> {
            List<Event> events = loadTask.getValue();
            eventsList.getChildren().clear();

            for (Event event : events) {
                eventsList.getChildren().add(createEventCard(event));
            }

            if (events.isEmpty()) {
                Label emptyLabel = new Label("No events found.");
                emptyLabel.getStyleClass().add("muted");
                eventsList.getChildren().add(emptyLabel);
            }
        });

        loadTask.setOnFailed(workerStateEvent -> {
            eventsList.getChildren().clear();
            showError("Could not load events from database.");
            loadTask.getException().printStackTrace();
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private HBox createEventCard(Event event) {
        Label title = new Label(event.getName());
        title.getStyleClass().add("card-title");

        Label details = new Label(formatEventDetails(event));
        details.getStyleClass().add("card-subtext");

        Label coordinator = new Label("Coordinator: Not assigned");
        coordinator.getStyleClass().add("muted");

        VBox left = new VBox(6, title, details, coordinator);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.getStyleClass().add("secondary-button");
        viewDetailsBtn.setOnAction(evt -> showInfo("View Details", event.getName()));

        Button assignBtn = new Button("Assign Coordinator");
        assignBtn.getStyleClass().add("secondary-button");
        assignBtn.setOnAction(evt -> showInfo("Assign Coordinator", event.getName()));

        // edit button added
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("secondary-button");
        editBtn.setOnAction(evt -> openEditView(event));

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().addAll("icon-button", "danger-button");

        HBox card = new HBox(12, left, spacer, viewDetailsBtn, assignBtn, editBtn, deleteBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");

        deleteBtn.setOnAction(evt -> deleteEventFromDatabaseAndUI(event, card, deleteBtn));

        return card;
    }

    private void deleteEventFromDatabaseAndUI(Event event, HBox card, Button deleteBtn) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Event");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete \"" + event.getName() + "\"?");

        confirm.showAndWait().ifPresent(response -> {
            String buttonText = response.getText().toLowerCase();

            if (buttonText.contains("ok")) {
                deleteBtn.setDisable(true);

                Task<Void> deleteTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        eventManager.deleteEvent(event);
                        return null;
                    }
                };

                deleteTask.setOnSucceeded(workerStateEvent -> {
                    eventsList.getChildren().remove(card);

                    if (eventsList.getChildren().isEmpty()) {
                        Label emptyLabel = new Label("No events found.");
                        emptyLabel.getStyleClass().add("muted");
                        eventsList.getChildren().add(emptyLabel);
                    }
                });

                deleteTask.setOnFailed(workerStateEvent -> {
                    deleteBtn.setDisable(false);
                    showError("Could not delete event from database.");
                    deleteTask.getException().printStackTrace();
                });

                Thread thread = new Thread(deleteTask);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    private String formatEventDetails(Event event) {
        if (event.getStartTime() == null) {
            return event.getLocation();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' hh:mm a");
        String start = event.getStartTime().format(formatter);

        return start + " • " + event.getLocation();
    }

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

    private void showInfo(String action, String eventTitle) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(action);
        alert.setHeaderText(null);
        alert.setContentText(action + " clicked for: " + eventTitle);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}