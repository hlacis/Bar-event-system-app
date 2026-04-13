package dk.easv.gui;

import dk.easv.be.Event;
import dk.easv.bll.EventManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventsController {

    @FXML
    private VBox eventsList;

    @FXML
    private TextField searchField;

    private final EventManager eventManager = new EventManager();

    private final List<EventCardViewData> allEvents = new ArrayList<>();

    @FXML
    public void initialize() {
        loadEventsFromDatabaseAsync();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterEvents(newVal);
        });
    }

    private void filterEvents(String query) {
        if (query == null || query.isBlank()) {
            renderEvents(allEvents);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<EventCardViewData> filteredEvents = new ArrayList<>();

        for (EventCardViewData data : allEvents) {
            Event event = data.event();

            boolean matchesName = event.getName() != null
                    && event.getName().toLowerCase().contains(lowerQuery);

            boolean matchesLocation = event.getLocation() != null
                    && event.getLocation().toLowerCase().contains(lowerQuery);

            boolean matchesCoordinator = data.coordinatorText() != null
                    && data.coordinatorText().toLowerCase().contains(lowerQuery);

            boolean matchesNotes = data.notesText() != null
                    && data.notesText().toLowerCase().contains(lowerQuery);

            if (matchesName || matchesLocation || matchesCoordinator || matchesNotes) {
                filteredEvents.add(data);
            }
        }

        renderEvents(filteredEvents);
    }

    @FXML
    private void createEvent() {
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

    private void loadEventsFromDatabaseAsync() {
        eventsList.getChildren().clear();

        Label loadingLabel = new Label("Loading events...");
        loadingLabel.getStyleClass().add("muted");
        eventsList.getChildren().add(loadingLabel);

        Task<List<EventCardViewData>> loadTask = new Task<>() {
            @Override
            protected List<EventCardViewData> call() throws Exception {
                List<Event> events = eventManager.getAllEvents();
                List<EventCardViewData> cardDataList = new ArrayList<>();

                for (Event event : events) {
                    List<String> coordinatorNames = eventManager.getCoordinatorNamesForEvent(event.getId());

                    String coordinatorText;
                    if (coordinatorNames.isEmpty()) {
                        coordinatorText = "Coordinator: Not assigned";
                    } else {
                        coordinatorText = "Coordinator: " + String.join(", ", coordinatorNames);
                    }

                    String notesText = (event.getNotes() == null || event.getNotes().isBlank())
                            ? "Note: None"
                            : "Note: " + event.getNotes();

                    cardDataList.add(new EventCardViewData(event, coordinatorText, notesText));
                }

                return cardDataList;
            }
        };

        loadTask.setOnSucceeded(workerStateEvent -> {
            allEvents.clear();
            allEvents.addAll(loadTask.getValue());
            renderEvents(allEvents);
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

    private void renderEvents(List<EventCardViewData> eventDataToRender) {
        eventsList.getChildren().clear();

        if (eventDataToRender.isEmpty()) {
            Label emptyLabel = new Label("No events found.");
            emptyLabel.getStyleClass().add("muted");
            eventsList.getChildren().add(emptyLabel);
            return;
        }

        for (EventCardViewData data : eventDataToRender) {
            eventsList.getChildren().add(createEventCard(data));
        }
    }

    private HBox createEventCard(EventCardViewData data) {
        Event event = data.event();

        Label title = new Label(event.getName());
        title.getStyleClass().add("card-title");

        Label details = new Label(formatEventDetails(event));
        details.getStyleClass().add("card-subtext");

        Label coordinatorLabel = new Label(data.coordinatorText());
        coordinatorLabel.getStyleClass().add("muted");

        Label notesLabel = new Label(data.notesText());
        notesLabel.getStyleClass().add("muted");

        HBox bottomInfoRow = new HBox(24, coordinatorLabel, notesLabel);
        bottomInfoRow.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(6, title, details, bottomInfoRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().addAll("icon-button", "danger-button");
        deleteBtn.setOnMouseClicked(e -> e.consume());

        HBox card = new HBox(12, left, spacer, deleteBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");

        card.setOnMouseClicked(e -> openDetailsView(event));
        card.setStyle("-fx-cursor: hand;");

        deleteBtn.setOnAction(evt -> deleteEventFromDatabaseAndUI(event, card, deleteBtn));

        return card;
    }

    private void openDetailsView(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventDetailsView.fxml")
            );

            Parent view = loader.load();

            EventDetailsController controller = loader.getController();
            controller.setEvent(event);

            StackPane contentHost = findContentHost(eventsList);
            if (contentHost != null) {
                contentHost.getChildren().setAll(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open event details.");
        }
    }

    private void openAssignCoordinatorView() {
        try {
            Parent assignView = FXMLLoader.load(
                    getClass().getResource("/dk/easv/gui/AssignCoordinatorsView.fxml")
            );

            StackPane contentHost = findContentHost(eventsList);
            if (contentHost != null) {
                contentHost.getChildren().setAll(assignView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open Assign Coordinators view.");
        }
    }

    private void openEditView(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/dk/easv/gui/EventsCreator.fxml")
            );

            Parent creatorView = loader.load();

            EventCreatorController controller = loader.getController();
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
                    allEvents.removeIf(data -> data.event().getId() == event.getId());
                    filterEvents(searchField.getText());
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

    private StackPane findContentHost(javafx.scene.Node node) {
        javafx.scene.Node current = node;
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

    private record EventCardViewData(Event event, String coordinatorText, String notesText) {}
}